/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler;

import lombok.Getter;
import me.waliedyassen.runescript.CompilerError;
import me.waliedyassen.runescript.commons.stream.BufferedCharStream;
import me.waliedyassen.runescript.compiler.ast.AstScript;
import me.waliedyassen.runescript.compiler.ast.expr.AstExpression;
import me.waliedyassen.runescript.compiler.codegen.CodeGenerator;
import me.waliedyassen.runescript.compiler.codegen.InstructionMap;
import me.waliedyassen.runescript.compiler.codegen.optimizer.Optimizer;
import me.waliedyassen.runescript.compiler.codegen.optimizer.impl.DeadBlockOptimization;
import me.waliedyassen.runescript.compiler.codegen.optimizer.impl.DeadBranchOptimization;
import me.waliedyassen.runescript.compiler.codegen.optimizer.impl.NaturalFlowOptimization;
import me.waliedyassen.runescript.compiler.codegen.writer.bytecode.BytecodeCodeWriter;
import me.waliedyassen.runescript.compiler.env.CompilerEnvironment;
import me.waliedyassen.runescript.compiler.lexer.Lexer;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.lexer.tokenizer.Tokenizer;
import me.waliedyassen.runescript.compiler.parser.ScriptParser;
import me.waliedyassen.runescript.compiler.semantics.SemanticChecker;
import me.waliedyassen.runescript.compiler.symbol.SymbolTable;
import me.waliedyassen.runescript.compiler.util.Operator;
import me.waliedyassen.runescript.lexer.table.LexicalTable;
import me.waliedyassen.runescript.type.PrimitiveType;
import me.waliedyassen.runescript.type.StackType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the main class for the RuneScript language compiler module.
 *
 * @author Walied K. Yassen
 */
public final class Compiler {

    /**
     * The charset of the source files.
     */
    private static final Charset CHARSET = Charset.forName("cp1252");

    /**
     * The symbol table of the compiler.
     */
    @Getter
    private final SymbolTable symbolTable = new SymbolTable();

    /**
     * The code writer of the compiler.
     */
    private final BytecodeCodeWriter codeWriter = new BytecodeCodeWriter();

    /**
     * The lexical table for our lexical analysis, it contains vario
     */
    @Getter
    private final LexicalTable<Kind> lexicalTable;

    /**
     * The compiler environment which is basically a user level symbol table.
     */
    private final CompilerEnvironment environment;

    /**
     * The instruction map to use for the
     */
    private final InstructionMap instructionMap;

    /**
     * The generated scripts optimizer.
     */
    private final Optimizer optimizer;

    /**
     * Constructs a new {@link Compiler} type object instance.
     *
     * @param environment    the environment of the compiler.
     * @param instructionMap the instruction map to use for this compiler.
     */
    public Compiler(CompilerEnvironment environment, InstructionMap instructionMap) {
        if (!instructionMap.isReady()) {
            throw new IllegalArgumentException("The provided InstructionMap is not ready, please register all of core opcodes before using it.");
        }
        this.environment = environment;
        this.instructionMap = instructionMap;
        lexicalTable = createLexicalTable();
        optimizer = new Optimizer(instructionMap);
        optimizer.register(new NaturalFlowOptimization());
        optimizer.register(new DeadBranchOptimization());
        optimizer.register(new DeadBlockOptimization());
    }

    /**
     * Parses the Abstract Syntax Tree of the specified source file data.
     *
     * @param symbolTable the symbol table to use for parsing.
     * @param data        the source file data in bytes.
     * @return a {@link List list} of the parsed {@link AstScript} objects.
     */
    private List<AstScript> parseSyntaxTree(SymbolTable symbolTable, byte[] data) throws IOException {
        var stream = new BufferedCharStream(new ByteArrayInputStream(data));
        var tokenizer = new Tokenizer(lexicalTable, stream);
        var lexer = new Lexer(tokenizer);
        var parser = new ScriptParser(environment, symbolTable, lexer);
        var scripts = new ArrayList<AstScript>();
        while (lexer.remaining() > 0) {
            scripts.add(parser.script());
        }
        return scripts;
    }

    /**
     * Attempts to compile all of the source code specified in the {@link CompileInput input} object
     * and produce a {@link CompileResult} object which contains the compiled form of the object
     * and the associated errors produced during that compilation process.
     *
     * @param input the input object which contains the all of the source code that we want to compile.
     * @return the {@link CompileResult} object instance.
     * @throws IOException if somehow a problem occurred while writing or reading from the temporary streams.
     */
    public CompileResult compile(CompileInput input) throws IOException {
        var compilingScripts = new ArrayList<AstScript>();
        var errors = new ArrayList<CompilerError>();
        for (var source : input.getSourceData()) {
            try {
                compilingScripts.addAll(parseSyntaxTree(symbolTable, source));
            } catch (CompilerError e) {
                errors.add(e);
            }
        }
        if (compilingScripts.isEmpty()) {
            return CompileResult.of(Collections.emptyList(), errors);
        }
        var symbolTable = this.symbolTable.createSubTable();
        // Perform semantic analysis checking on the parsed AST.
        var checker = new SemanticChecker(environment, symbolTable);
        checker.executePre(compilingScripts);
        checker.execute(compilingScripts);
        // Check if there is any compilation errors and throw them if there is any.
        if (checker.getErrors().size() > 0) {
            errors.addAll(checker.getErrors());
        }
        // TODO: Find a way to trace errors back to their source script and filter that script so that we can compile
        // all of the non-erroneous scripts.
        if (!errors.isEmpty()) {
            return CompileResult.of(Collections.emptyList(), errors);
        }
        var codeGenerator = new CodeGenerator(symbolTable, instructionMap);
        // Compile all of the parsed and checked scripts into a bytecode format.
        var compiledScripts = new ArrayList<CompiledScript>();
        for (var script : compilingScripts) {

            var trigger = environment.lookupTrigger(script.getTrigger().getText());
            var info = symbolTable.lookupScript(trigger, AstExpression.extractNameText(script.getName()));
            // Run the code generator on each script.,
            var generated = codeGenerator.visit(script);
            // Optimize the generated script.
            optimizer.run(generated);
            // Write the generated script to a bytecode format.
            var bytecode = codeWriter.write(generated);
            try (var stream = new ByteArrayOutputStream()) {
                bytecode.write(stream);
                compiledScripts.add(new CompiledScript(generated.getName(), stream.toByteArray(), info));
            }
        }
        return CompileResult.of(compiledScripts, errors);
    }

    /**
     * Create a new {@link LexicalTable} object and then register all of the lexical symbols for our RuneScript language
     * syntax.
     *
     * @return the created {@link LexicalTable} object.
     */
    public static LexicalTable<Kind> createLexicalTable() {
        // TODO: Cache LexicalTable
        var table = new LexicalTable<Kind>();
        // the keywords chunk.
        table.registerKeyword("true", Kind.BOOL);
        table.registerKeyword("false", Kind.BOOL);
        table.registerKeyword("if", Kind.IF);
        table.registerKeyword("else", Kind.ELSE);
        table.registerKeyword("while", Kind.WHILE);
        table.registerKeyword("return", Kind.RETURN);
        table.registerKeyword("case", Kind.CASE);
        table.registerKeyword("default", Kind.DEFAULT);
        table.registerKeyword("calc", Kind.CALC);
        for (var type : PrimitiveType.values()) {
            if (type.getRepresentation() != null) {
                table.registerKeyword(type.getRepresentation(), Kind.TYPE);
            }
            if (type.isDeclarable()) {
                table.registerKeyword("def_" + type.getRepresentation(), Kind.DEFINE);
            }
            if (type.isArrayable()) {
                table.registerKeyword(type.getRepresentation() + "array", Kind.ARRAY_TYPE);
            }
            if (type.getStackType() == StackType.INT) {
                table.registerKeyword("switch_" + type.getRepresentation(), Kind.SWITCH);
            }
        }
        // the separators chunk.
        table.registerSeparator('(', Kind.LPAREN);
        table.registerSeparator(')', Kind.RPAREN);
        table.registerSeparator('[', Kind.LBRACKET);
        table.registerSeparator(']', Kind.RBRACKET);
        table.registerSeparator('{', Kind.LBRACE);
        table.registerSeparator('}', Kind.RBRACE);
        table.registerSeparator(',', Kind.COMMA);
        table.registerSeparator('~', Kind.TILDE);
        table.registerSeparator('@', Kind.AT);
        table.registerSeparator('$', Kind.DOLLAR);
        table.registerSeparator('^', Kind.CARET);
        table.registerSeparator(':', Kind.COLON);
        table.registerSeparator(';', Kind.SEMICOLON);
        table.registerSeparator('.', Kind.DOT);
        table.registerSeparator('#', Kind.HASH);
        // register all of the operators.
        for (var operator : Operator.values()) {
            table.registerOperator(operator.getRepresentation(), operator.getKind());
        }
        return table;
    }
}

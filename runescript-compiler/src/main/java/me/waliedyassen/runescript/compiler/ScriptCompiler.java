/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler;

import lombok.Getter;
import lombok.var;
import me.waliedyassen.runescript.commons.stream.BufferedCharStream;
import me.waliedyassen.runescript.compiler.codegen.optimizer.impl.ConstantFoldingOptimization;
import me.waliedyassen.runescript.compiler.error.ErrorReporter;
import me.waliedyassen.runescript.compiler.syntax.ScriptSyntax;
import me.waliedyassen.runescript.compiler.codegen.CodeGenerator;
import me.waliedyassen.runescript.compiler.codegen.InstructionMap;
import me.waliedyassen.runescript.compiler.codegen.optimizer.Optimizer;
import me.waliedyassen.runescript.compiler.codegen.optimizer.impl.DeadBlockOptimization;
import me.waliedyassen.runescript.compiler.codegen.optimizer.impl.DeadBranchOptimization;
import me.waliedyassen.runescript.compiler.codegen.optimizer.impl.NaturalFlowOptimization;
import me.waliedyassen.runescript.compiler.codegen.writer.CodeWriter;
import me.waliedyassen.runescript.compiler.codegen.writer.bytecode.BytecodeCodeWriter;
import me.waliedyassen.runescript.compiler.env.CompilerEnvironment;
import me.waliedyassen.runescript.compiler.idmapping.IDManager;
import me.waliedyassen.runescript.compiler.lexer.Lexer;
import me.waliedyassen.runescript.compiler.lexer.table.LexicalTable;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.lexer.tokenizer.Tokenizer;
import me.waliedyassen.runescript.compiler.syntax.SyntaxParser;
import me.waliedyassen.runescript.compiler.semantics.SemanticChecker;
import me.waliedyassen.runescript.compiler.symbol.ScriptSymbolTable;
import me.waliedyassen.runescript.compiler.util.Operator;
import me.waliedyassen.runescript.type.PrimitiveType;
import me.waliedyassen.runescript.type.StackType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the main class for the RuneScript language compiler module.
 *
 * @author Walied K. Yassen
 */
public final class ScriptCompiler extends CompilerBase<CompiledScriptUnit> {

    /**
     * The symbol table of the compiler.
     */
    @Getter
    private final ScriptSymbolTable symbolTable;

    /**
     * The code writer of the compiler.
     */
    @Getter
    private final CodeWriter<?> codeWriter;

    /**
     * The lexical table for our lexical analysis, it contains vario
     */
    @Getter
    private final LexicalTable<Kind> lexicalTable;

    /**
     * The compiler environment which is basically a user level symbol table.
     */
    @Getter
    private final CompilerEnvironment environment;

    /**
     * The instruction map to use for the
     */
    @Getter
    private final InstructionMap instructionMap;

    /**
     * The generated scripts optimizer.
     */
    @Getter
    private final Optimizer optimizer;

    /**
     * Whether or not the compiler should override the symbols.
     */
    @Getter
    private final boolean allowOverride;


    // TODO: support supportsLongPrimitiveType in type checking.

    // TODO: Add context object so we don't have to manage
    // parameters constantly and update calls.

    /**
     * Constructs a new {@link ScriptCompiler} type object instance.
     *
     * @param environment    the environment of the compiler.
     * @param instructionMap the instruction map to use for this compiler.
     * @param codeWriter     the code writer to use for the compiler.
     * @param allowOverride  whether or not the compiler should override the symbols.
     */
    private ScriptCompiler(CompilerEnvironment environment,
                           InstructionMap instructionMap,
                           ScriptSymbolTable symbolTable,
                           CodeWriter<?> codeWriter,
                           boolean allowOverride) {
        super(null);
        if (!instructionMap.isReady()) {
            throw new IllegalArgumentException("The provided InstructionMap is not ready, please register all of core opcodes before using it.");
        }
        this.environment = environment;
        this.instructionMap = instructionMap;
        this.symbolTable = symbolTable;
        this.codeWriter = codeWriter;
        this.allowOverride = allowOverride;
        lexicalTable = createLexicalTable();
        optimizer = new Optimizer(instructionMap);
        optimizer.register(new NaturalFlowOptimization());
        optimizer.register(new DeadBranchOptimization());
        optimizer.register(new DeadBlockOptimization());
        optimizer.register(new ConstantFoldingOptimization());
    }

    /**
     * Parses the Abstract Syntax Tree of the specified source file data.
     *
     * @param symbolTable the symbol table to use for parsing.
     * @param data        the source file data in bytes.
     * @param extension   the extension of the file containing the script.
     * @return a {@link List list} of the parsed {@link ScriptSyntax} objects.
     */
    private List<ScriptSyntax> parseSyntaxTree(ScriptSymbolTable symbolTable, ErrorReporter errorReporter, byte[] data, String extension) throws IOException {
        var stream = new BufferedCharStream(new ByteArrayInputStream(data));
        var tokenizer = new Tokenizer(errorReporter, lexicalTable, stream);
        var lexer = new Lexer(tokenizer);
        var parser = new SyntaxParser(environment, symbolTable, errorReporter, lexer, extension);
        var scripts = new ArrayList<ScriptSyntax>();
        while (lexer.remaining() > 0) {
            scripts.add(parser.script());
        }
        return scripts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Output<CompiledScriptUnit> compile(Input input) throws IOException {
        var symbolTable = this.symbolTable.createSubTable();
        var output = new Output<CompiledScriptUnit>();
        for (var sourceFile : input.getSourceFiles()) {
            var errorReporter = new ErrorReporter();
            try {
                var scripts = parseSyntaxTree(symbolTable, errorReporter, sourceFile.getContent(), sourceFile.getExtension());
                for (var script : scripts) {
                    var compiledUnit = new CompiledScriptUnit();
                    compiledUnit.setScript(script);
                    output.addUnit(sourceFile, compiledUnit);
                }
            } catch (CompilerError error) {
                output.addError(sourceFile, error);
            }
            errorReporter.getErrors().forEach(error -> {
                output.addError(sourceFile, error);
            });
        }
        var checker = new SemanticChecker(environment, symbolTable, allowOverride);
        for (var compiledFile : output.getFiles().values()) {
            checker.executePre(compiledFile.getUnits());
            compiledFile.getErrors().addAll(checker.getErrors());
            checker.getErrors().clear();
        }
        for (var compiledFile : output.getFiles().values()) {
            checker.execute(compiledFile.getUnits());
            compiledFile.getErrors().addAll(checker.getErrors());
            checker.getErrors().clear();
        }
        if (input.isRunCodeGeneration()) {
            var codeGenerator = new CodeGenerator(environment, symbolTable, instructionMap, environment.getHookTriggerType());
            for (var compiledFile : output.getFiles().values()) {
                for (var unit : compiledFile.getUnits()) {
                    var binaryScript = codeGenerator.visit(unit.getScript());
                    optimizer.run(binaryScript);
                    unit.setBinaryScript(binaryScript);
                }
            }
        }
        return output;
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
        table.registerKeyword("do", Kind.DO);
        table.registerKeyword("return", Kind.RETURN);
        table.registerKeyword("case", Kind.CASE);
        table.registerKeyword("default", Kind.DEFAULT);
        table.registerKeyword("calc", Kind.CALC);
        table.registerKeyword("null", Kind.NULL);
        table.registerKeyword("continue", Kind.CONTINUE);
        table.registerKeyword("break", Kind.BREAK);
        for (var type : PrimitiveType.values()) {
            if (type.isReferencable()) {
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

    /**
     * Returns a new {@link CompilerBuilder} object.
     *
     * @return the created {@link CompilerBuilder} object.
     */
    public static CompilerBuilder builder() {
        return new CompilerBuilder();
    }

    /**
     * A builder class for the {@link ScriptCompiler} type.
     *
     * @author Walied k. Yassen
     */
    public static final class CompilerBuilder {

        /**
         * The environment of the compiler.
         */
        private CompilerEnvironment environment;

        /**
         * The instruction map of the compiler.
         */
        private InstructionMap instructionMap;

        /**
         * The symbol table of the compiler.
         */
        private ScriptSymbolTable symbolTable;

        /**
         * The code writer of the compiler.
         */
        private CodeWriter<?> codeWriter;

        /**
         * Whether or not the compiler supports the long primitive type.
         */
        private boolean supportsLongPrimitiveType;

        /**
         * Whether or not the compiler should override the symbols.
         */
        private boolean overrideSymbols;

        /**
         * The {@link IDManager} of the compiler.
         */
        private IDManager idProvider;

        /**
         * Sets the environment object we are going to use for the compiler.
         *
         * @param environment the environment object to set.
         * @return this {@link CompilerBuilder} object instance.
         */
        public CompilerBuilder withEnvironment(CompilerEnvironment environment) {
            this.environment = environment;
            return this;
        }

        /**
         * Sets the instruction map object we are going to use for the compiler.
         *
         * @param instructionMap the instruction map object to set.
         * @return this {@link CompilerBuilder} object instance.
         */
        public CompilerBuilder withInstructionMap(InstructionMap instructionMap) {
            this.instructionMap = instructionMap;
            return this;
        }

        /**
         * Sets the symbol table object we are going to use for the compiler.
         *
         * @param symbolTable the symbol table object to set.
         * @return this {@link CompilerBuilder} object instance.
         */
        public CompilerBuilder withSymbolTable(ScriptSymbolTable symbolTable) {
            this.symbolTable = symbolTable;
            return this;
        }


        /**
         * Sets whether or not the compiler that we are going to build should support the long primitive type.
         *
         * @param supportsLongPrimitiveType <code>true</code> if it supports the long primitive type otherwise
         *                                  <code>false</code>.
         * @return this {@link CompilerBuilder} object instance.
         */
        public CompilerBuilder withSupportsLongPrimitiveType(boolean supportsLongPrimitiveType) {
            this.supportsLongPrimitiveType = supportsLongPrimitiveType;
            return this;
        }

        /**
         * Sets the id provider that we are going to use for the compiler.
         *
         * @param idProvider the id provider of the compiler.
         * @return this {@link CompilerBuilder} object instance.
         */
        public CompilerBuilder withIdProvider(IDManager idProvider) {
            this.idProvider = idProvider;
            return this;
        }

        /**
         * Sets whether or not the compiler that we are going to build should override the symbols in the symbol
         * table.
         *
         * @param overrideSymbols whether or not we should override symbols.
         * @return this {@link CompilerBuilder} object instance.
         */
        public CompilerBuilder withOverrideSymbols(boolean overrideSymbols) {
            this.overrideSymbols = overrideSymbols;
            return this;
        }

        /**
         * Sets the code writer that we are going to use for the compiler.
         *
         * @param codeWriter the code writer of the compiler.
         * @return this {@link CompilerBuilder} object instance.
         */
        public CompilerBuilder withCodeWriter(CodeWriter<?> codeWriter) {
            this.codeWriter = codeWriter;
            return this;
        }

        /**
         * Builds the {@link ScriptCompiler} object with the details configured in the builder.
         *
         * @return the built {@link ScriptCompiler} object.
         * @throws IllegalStateException if one or more of the configuration is invalid or missing.
         */
        public ScriptCompiler build() {
            if (instructionMap == null) {
                throw new IllegalStateException("You must provide an InstructionMap before performing build() operation");
            }
            if (idProvider == null) {
                throw new IllegalStateException("You must provide an IdProvider before performing build() operation");
            }
            if (environment == null) {
                environment = new CompilerEnvironment();
            }
            if (codeWriter == null) {
                codeWriter = new BytecodeCodeWriter(idProvider, supportsLongPrimitiveType);
            }
            if (symbolTable == null) {
                symbolTable = new ScriptSymbolTable();
            }
            return new ScriptCompiler(environment, instructionMap, symbolTable, codeWriter, overrideSymbols);
        }
    }
}

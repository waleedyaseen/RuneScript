/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler;

import me.waliedyassen.runescript.commons.stream.BufferedCharStream;
import me.waliedyassen.runescript.compiler.ast.AstScript;
import me.waliedyassen.runescript.compiler.codegen.CodeGenerator;
import me.waliedyassen.runescript.compiler.codegen.InstructionMap;
import me.waliedyassen.runescript.compiler.codegen.optimizer.Optimizer;
import me.waliedyassen.runescript.compiler.codegen.optimizer.impl.DeadBlockOptimization;
import me.waliedyassen.runescript.compiler.codegen.optimizer.impl.DeadBranchOptimization;
import me.waliedyassen.runescript.compiler.codegen.optimizer.impl.NaturalFlowOptimization;
import me.waliedyassen.runescript.compiler.codegen.writer.bytecode.BytecodeCodeWriter;
import me.waliedyassen.runescript.compiler.codegen.writer.bytecode.BytecodeScript;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the main class for the RuneScript language compiler module.
 *
 * @author Walied K. Yassen
 */
public final class Compiler {

    /**
     * The source file extension.
     */
    private static final String SOURCE_EXTENSION = ".rs2";

    /**
     * The output file extension.
     */
    private static final String OUTPUT_EXTENSION = ".cs2";

    /**
     * The charset of the source files.
     */
    private static final Charset CHARSET = Charset.forName("cp1252");

    /**
     * The lexical table for our lexical analysis, it contains vario
     */
    private final LexicalTable<Kind> lexicalTable;

    /**
     * The symbol table of the compiler.
     */
    private final SymbolTable symbolTable = new SymbolTable();

    /**
     * The compiler environment which is basically a user level symbol table.
     */
    private final CompilerEnvironment environment;

    /**
     * The instruction map to use for the
     */
    private final InstructionMap instructionMap;

    /**
     * The code generator of the compiler.
     */
    private final CodeGenerator codeGenerator;

    /**
     * The generated scripts optimizer.
     */
    private final Optimizer optimizer;

    /**
     * The code writer of the compiler.
     */
    private final BytecodeCodeWriter codeWriter = new BytecodeCodeWriter();

    /**
     * Constructs a new {@link Compiler} type object instance.
     *
     * @param environment
     *         the environment of the compiler.
     * @param instructionMap
     *         the instruction map to use for this compiler.
     */
    public Compiler(CompilerEnvironment environment, InstructionMap instructionMap) {
        if (!instructionMap.isReady()) {
            throw new IllegalArgumentException("The provided InstructionMap is not ready, please register all of core opcodes before using it.");
        }
        this.environment = environment;
        this.instructionMap = instructionMap;
        lexicalTable = createLexicalTable();
        codeGenerator = new CodeGenerator(symbolTable, instructionMap);
        optimizer = new Optimizer(instructionMap);
        optimizer.register(new NaturalFlowOptimization());
        optimizer.register(new DeadBranchOptimization());
        optimizer.register(new DeadBlockOptimization());
    }

    /**
     * Compiles all of the scripts (the files that ends with .rs2) that are in the specified source directory and
     * outputs the compiled scripts into the output directory. This method will also compile all of the scripts in the
     * sub-directories.
     *
     * @param sourceDirectory
     *         the source directory which contains all of the scripts
     * @param outputDirectory
     *         the output directory to output the compiled script to.
     */
    public void compileDirectory(Path sourceDirectory, Path outputDirectory) throws IOException, CompilerErrors {
        // Collect all of the script files that we will compile.
        var sourceFiles = collectSourceFiles(sourceDirectory);
        // Do nothing if we have no files to compile.
        if (sourceFiles.size() < 1) {
            return;
        }
        // Parse all of the script files.
        var scripts = new ArrayList<AstScript>();
        for (var sourceFile : sourceFiles) {
            scripts.addAll(parseSyntaxTree(Files.readAllBytes(sourceFile)));
        }
        // Perform pre type checking on all of the files.
        var checker = new SemanticChecker(environment, symbolTable);
        checker.executePre(scripts);
        checker.execute(scripts);
        // Check if we have any errors and if so we do not compile.
        if (checker.getErrors().size() > 0) {
            throw new CompilerErrors(checker.getErrors());
        }
        // Compile all of the scripts and store them in a list.
        var result = new ArrayList<CompiledScript>();
        for (var script : scripts) {
            // Run the code generator on the script.
            var generated = codeGenerator.visit(script);
            // Optimize the generated script.
            optimizer.run(generated);
            // Write the generated script to a bytecode format.
            BytecodeScript bytecode = codeWriter.write(generated);
            try (var stream = new ByteArrayOutputStream()) {
                bytecode.write(stream);
                result.add(new CompiledScript(generated.getName(), stream.toByteArray()));
            }
        }
        // Loop through each compiled script and write it to the output directory.
        for (var script : result) {
            Files.write(outputDirectory.resolve(script.getName() + OUTPUT_EXTENSION), script.getData(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    /**
     * Collects all of the script source files that are within the specified directory.
     *
     * @param directory
     *         the directory path to collect from.
     */
    private List<Path> collectSourceFiles(Path directory) throws IOException {
        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException("The specified source directory does not exist or is not a directory");
        }
        return Files.walk(directory).filter(sourceFile -> Files.isRegularFile(sourceFile) && sourceFile.toString().endsWith(SOURCE_EXTENSION)).collect(Collectors.toList());
    }

    /**
     * Compiles the specified file content and outputs them into the specified directory.
     *
     * @param sourceFile
     *         the source file path to compile.
     * @param outputDirectory
     *         the output directory path for the compiled file.
     *
     * @throws IOException
     *         if anything occurs while attempting to read or write the data to the source and output files.
     * @throws CompilerErrors
     *         if there was any syntax or semantic errors in the given source code file.
     */
    public void compileFile(Path sourceFile, Path outputDirectory) throws IOException, CompilerErrors {
        if (!Files.isRegularFile(sourceFile)) {
            throw new IllegalArgumentException("The specified source file path does not exist or is not a regular file path.");
        }
        if (!Files.exists(outputDirectory)) {
            Files.createDirectories(outputDirectory);
        }
        var source = Files.readAllBytes(sourceFile);
        var compiled = compile(source);
        for (var script : compiled) {
            Files.write(outputDirectory.resolve(script.getName() + ".cs2"), script.getData(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    /**
     * Compiles the specified source file content.
     *
     * @param source
     *         the content of the source file.
     *
     * @return an array of {@link CompiledScript} objects.
     * @throws IOException
     *         if anything occurs while writing the bytecode data.
     * @throws CompilerErrors
     *         if there was any syntax or semantic errors in the given source code.
     */
    public CompiledScript[] compile(String source) throws IOException, CompilerErrors {
        return compile(source.getBytes(CHARSET));
    }

    /**
     * Compiles the specified source file data.
     *
     * @param source
     *         the data of the source file in bytes.
     *
     * @return an array of {@link CompiledScript} objects.
     * @throws IOException
     *         if anything occurs while writing the bytecode data.
     * @throws CompilerErrors
     *         if there was any syntax or semantic errors in the given source code data.
     */
    public CompiledScript[] compile(byte[] source) throws IOException, CompilerErrors {
        // Parse the Abstract Syntax Tree of the source.
        var scripts = parseSyntaxTree(source);
        if (scripts.size() < 1) {
            return new CompiledScript[0];
        }
        // Perform semantic analysis checking on the parsed AST.
        var checker = new SemanticChecker(environment, symbolTable);
        checker.executePre(scripts);
        checker.execute(scripts);
        // Check if there is any compilation errors and throw them if there is any.
        if (checker.getErrors().size() > 0) {
            throw new CompilerErrors(checker.getErrors());
        }
        // Compile all of the parsed and checked scripts into a bytecode format.
        var result = new ArrayList<CompiledScript>();
        for (var script : scripts) {
            // Run the code generator on each script.,
            var generated = codeGenerator.visit(script);
            // Optimize the generated script.
            optimizer.run(generated);
            // Write the generated script to a bytecode format.
            BytecodeScript bytecode = codeWriter.write(generated);
            try (var stream = new ByteArrayOutputStream()) {
                bytecode.write(stream);
                result.add(new CompiledScript(generated.getName(), stream.toByteArray()));
            }
        }
        return result.toArray(CompiledScript[]::new);
    }

    /**
     * Parses the Abstract Syntax Tree of the specified source file data.
     *
     * @param data
     *         the source file data in bytes.
     *
     * @return a {@link List list} of the parsed {@link AstScript} objects.
     */
    private List<AstScript> parseSyntaxTree(byte[] data) throws IOException {
        var stream = new BufferedCharStream(new ByteArrayInputStream(data));
        var tokenizer = new Tokenizer(lexicalTable, stream);
        var lexer = new Lexer(tokenizer);
        var parser = new ScriptParser(environment, lexer);
        var scripts = new ArrayList<AstScript>();
        while (lexer.remaining() > 0) {
            scripts.add(parser.script());
        }
        return scripts;
    }

    /**
     * Create a new {@link LexicalTable} object and then register all of the lexical symbols for our RuneScript language
     * syntax.
     *
     * @return the created {@link LexicalTable} object.
     */
    public static LexicalTable<Kind> createLexicalTable() {
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

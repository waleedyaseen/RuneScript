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
import me.waliedyassen.runescript.compiler.lexer.Lexer;
import me.waliedyassen.runescript.compiler.lexer.table.LexicalTable;
import me.waliedyassen.runescript.compiler.lexer.tokenizer.Tokenizer;
import me.waliedyassen.runescript.compiler.parser.ScriptParser;
import me.waliedyassen.runescript.compiler.semantics.SemanticChecker;
import me.waliedyassen.runescript.compiler.symbol.SymbolTable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
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
     * The lexical table for our lexical analysis, it contains vario
     */
    private final LexicalTable lexicalTable = new LexicalTable(true);

    /**
     * The symbol table of the compiler.
     */
    private final SymbolTable symbolTable = new SymbolTable();

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
     * @param instructionMap
     *         the instruction map to use for this compiler.
     */
    public Compiler(InstructionMap instructionMap) {
        if (!instructionMap.isReady()) {
            throw new IllegalArgumentException("The provided InstructionMap is not ready, please register all of core opcodes before using it.");
        }
        this.instructionMap = instructionMap;
        codeGenerator = new CodeGenerator(symbolTable, instructionMap);
        optimizer = new Optimizer(instructionMap);
        optimizer.register(new NaturalFlowOptimization());
        optimizer.register(new DeadBranchOptimization());
        optimizer.register(new DeadBlockOptimization());
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
     */
    public void compileFile(Path sourceFile, Path outputDirectory) throws IOException {
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
     */
    public CompiledScript[] compile(String source) throws IOException {
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
     */
    public CompiledScript[] compile(byte[] source) throws IOException {
        // Parse the Abstract Syntax Tree of the source.
        var scripts = parseSyntaxTree(source);
        if (scripts.size() < 1) {
            return new CompiledScript[0];
        }
        // Perform semantic analysis checking on the parsed AST.
        var checker = new SemanticChecker(symbolTable);
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
        var parser = new ScriptParser(lexer);
        var scripts = new ArrayList<AstScript>();
        while (lexer.reminaing() > 0) {
            scripts.add(parser.script());
        }
        return scripts;
    }
}

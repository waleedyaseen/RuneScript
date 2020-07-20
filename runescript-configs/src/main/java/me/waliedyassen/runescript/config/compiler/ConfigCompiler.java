/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.compiler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.commons.stream.BufferedCharStream;
import me.waliedyassen.runescript.compiler.CompilerBase;
import me.waliedyassen.runescript.compiler.CompilerError;
import me.waliedyassen.runescript.compiler.Input;
import me.waliedyassen.runescript.compiler.Output;
import me.waliedyassen.runescript.compiler.lexer.table.LexicalTable;
import me.waliedyassen.runescript.compiler.symbol.SymbolTable;
import me.waliedyassen.runescript.config.binding.ConfigBinding;
import me.waliedyassen.runescript.config.codegen.BinaryConfig;
import me.waliedyassen.runescript.config.codegen.CodeGenerator;
import me.waliedyassen.runescript.config.lexer.Lexer;
import me.waliedyassen.runescript.config.lexer.Tokenizer;
import me.waliedyassen.runescript.config.lexer.token.Kind;
import me.waliedyassen.runescript.config.parser.ConfigParser;
import me.waliedyassen.runescript.config.semantics.SemanticChecker;
import me.waliedyassen.runescript.type.PrimitiveType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the RuneScript configurations compiler.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class ConfigCompiler extends CompilerBase<Input, Output<BinaryConfig>> {

    /**
     * A map of all the bindings that can be used by this compiler mapped by their extension.
     */
    private final Map<String, ConfigBinding> bindings = new HashMap<>();

    /**
     * The lexical table of the configuration compiler.
     */
    @Getter
    private final LexicalTable<Kind> lexicalTable = createLexicalTable();

    /**
     * The symbol table of the configuration compiler.
     */
    @Getter
    private final SymbolTable symbolTable;

    /**
     * {@inheritDoc}
     */
    @Override
    public Output<BinaryConfig> compile(Input input) throws IOException {
        var symbolTable = this.symbolTable.createSubTable();
        var output = new Output<BinaryConfig>();
        for (var sourceFile : input.getSourceFiles()) {
            String extension = sourceFile.getExtension().toLowerCase();
            var binding = bindings.get(extension);
            if (binding == null) {
                throw new IllegalStateException("Missing configuration binding for file extension: " + extension);
            }
            var stream = new BufferedCharStream(new ByteArrayInputStream(sourceFile.getContent()));
            var tokenizer = new Tokenizer(lexicalTable, stream);
            var lexer = new Lexer(tokenizer);
            var parser = new ConfigParser(lexer);
            try {
                var configs = parser.configs();
                if (configs.length == 0) {
                    continue;
                }
                var checker = new SemanticChecker(symbolTable, binding);
                checker.executePre(configs);
                checker.execute(configs);
                if (checker.getErrors().isEmpty()) {
                    var codeGen = new CodeGenerator(binding);
                    for (var config : configs) {
                        var binaryConfig = codeGen.visit(config);
                        output.addUnit(sourceFile, binaryConfig);
                    }
                } else {
                    checker.getErrors().forEach(error -> output.addError(sourceFile, error));
                }
            } catch (CompilerError error) {
                output.addError(sourceFile, error);
            }
        }
        return output;
    }

    /**
     * Registers a new configuration binding into this compiler.
     *
     * @param extension
     *         the configuration file extension.
     * @param binding
     *         the configuration binding.
     */
    public void registerBinding(String extension, ConfigBinding binding) {
        extension = extension.toLowerCase();
        if (bindings.containsKey(extension)) {
            throw new IllegalArgumentException("The specified binding extension is already registered: " + extension);
        }
        bindings.put(extension, binding);
    }

    /**
     * Create a new {@link LexicalTable} object and then register all of the lexical symbols for our configurations
     * syntax.
     *
     * @return the created {@link LexicalTable} object.
     */
    public static LexicalTable<Kind> createLexicalTable() {
        var table = new LexicalTable<Kind>();
        table.registerSeparator('[', Kind.LBRACKET);
        table.registerSeparator(']', Kind.RBRACKET);
        table.registerSeparator('=', Kind.EQUAL);
        table.registerSeparator(',', Kind.COMMA);
        table.registerKeyword("yes", Kind.BOOLEAN);
        table.registerKeyword("no", Kind.BOOLEAN);
        table.registerKeyword("true", Kind.BOOLEAN);
        table.registerKeyword("false", Kind.BOOLEAN);
        for (PrimitiveType type : PrimitiveType.values()) {
            if (type.isReferencable()) {
                table.registerKeyword(type.getRepresentation(), Kind.TYPE);
            }
        }
        return table;
    }
}

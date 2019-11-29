/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.semantics.typecheck;

import me.waliedyassen.runescript.commons.stream.BufferedCharStream;
import me.waliedyassen.runescript.compiler.Compiler;
import me.waliedyassen.runescript.compiler.ast.AstScript;
import me.waliedyassen.runescript.compiler.codegen.InstructionMap;
import me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode;
import me.waliedyassen.runescript.compiler.lexer.Lexer;
import me.waliedyassen.runescript.compiler.lexer.tokenizer.Tokenizer;
import me.waliedyassen.runescript.compiler.parser.ScriptParser;
import me.waliedyassen.runescript.compiler.semantics.SemanticChecker;
import me.waliedyassen.runescript.compiler.symbol.SymbolTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TypeCheckingTest {

    private SemanticChecker checker;

    @BeforeEach
    void setupGenerator() {
        var table = new SymbolTable();
        checker = new SemanticChecker(table);
    }

    @Test
    void testArray() throws IOException {
        checkResource("array_01.rs2");
        assertEquals(0, checker.getErrors().size());
    }

    void checkResource(String name) throws IOException {
        try (var stream = getClass().getResourceAsStream(name)) {
            var tokenizer = new Tokenizer(Compiler.createLexicalTable(), new BufferedCharStream(stream));
            var lexer = new Lexer(tokenizer);
            var parser = new ScriptParser(lexer);
            var scripts = new ArrayList<AstScript>();
            do {
                scripts.add(parser.script());
            } while (lexer.remaining() > 0);
            checker.executePre(scripts);
            checker.execute(scripts);
            if (checker.getErrors().size() > 0) {
                checker.getErrors().forEach(System.out::println);
            }
        }
    }

    void checkString(String text) throws IOException {
        try (var stream = new ByteArrayInputStream(text.getBytes())) {
            var tokenizer = new Tokenizer(Compiler.createLexicalTable(), new BufferedCharStream(stream));
            var lexer = new Lexer(tokenizer);
            var parser = new ScriptParser(lexer);
            var scripts = new ArrayList<AstScript>();
            do {
                scripts.add(parser.script());
            } while (lexer.remaining() > 0);
            checker.executePre(scripts);
            checker.execute(scripts);
            if (checker.getErrors().size() > 0) {
                checker.getErrors().forEach(System.out::println);
            }
        }
    }
}
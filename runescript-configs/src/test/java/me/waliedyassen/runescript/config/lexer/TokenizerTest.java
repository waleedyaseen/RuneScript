/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package me.waliedyassen.runescript.config.lexer;

import lombok.var;
import me.waliedyassen.runescript.compiler.lexer.LexicalError;
import me.waliedyassen.runescript.commons.stream.BufferedCharStream;
import me.waliedyassen.runescript.config.compiler.ConfigCompiler;
import me.waliedyassen.runescript.config.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.lexer.table.LexicalTable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokenizerTest {

    private static LexicalTable<Kind> table;

    @BeforeAll
    static void setupLexicalTable() {
        table = ConfigCompiler.createLexicalTable();
    }

    @Test
    void testIdentifier() throws IOException {
        var lexer = fromString("id id2 id3 4id");
        assertEquals(Kind.IDENTIFIER, lexer.parse().getKind());
        assertEquals("id2", lexer.parse().getLexeme());
        assertEquals("id3", lexer.parse().getLexeme());
        assertEquals("4id", lexer.parse().getLexeme());
    }

    @Test
    void testString() throws IOException {
        var lexer = fromString("\"\"\"Hello from the Lexer\"\"Unclosed string");
        assertEquals(Kind.STRING, lexer.parse().getKind());
        assertEquals("Hello from the Lexer", lexer.parse().getLexeme());
        assertThrows(LexicalError.class, lexer::parse);
        assertThrows(LexicalError.class, () -> fromString("\"Unclosed string\n").parse());
        assertEquals(" \b \t \n \f \" \\ < > ", fromString("\" \\b \\t \\n \\f \\\" \\\\ \\< \\> \"").parse().getLexeme());
        assertThrows(LexicalError.class, () -> fromString("\"\\c\"").parse());
    }

    @Test
    void testNumber() throws IOException {
        var lexer = fromString("1 2l 12345 54321L");
        assertEquals(Kind.INTEGER, lexer.parse().getKind());
        assertEquals(Kind.LONG, lexer.parse().getKind());
        assertEquals("12345", lexer.parse().getLexeme());
        assertEquals("54321", lexer.parse().getLexeme());

    }

    @Test
    void testEof() throws IOException {
        var lexer = fromString("\r\n\t ");
        assertEquals(Kind.EOF, lexer.parse().getKind());
        assertEquals(Kind.EOF, lexer.parse().getKind());
    }

    @Test
    void testLineComment() throws IOException {
        assertEquals(Kind.COMMENT, fromString("//test").parse().getKind());
        assertEquals("line comment #1", fromString("// line comment #1\r\n").parse().getLexeme());
        assertEquals("line comment #2", fromString("//line comment #2").parse().getLexeme());
    }

    @Test
    void testMultiLineComment() throws IOException {
        assertEquals(Kind.COMMENT, fromString("/* test */").parse().getKind());
        assertEquals("the first line\nthe second line", fromString("/* the first line \r\n*the second line */").parse().getLexeme());
        assertThrows(LexicalError.class, () -> fromString("/* unclosed comment").parse());
    }

    @Test
    void testSeparators() throws IOException {
        var text = new StringBuilder();
        for (var ch : table.getSeparators().keySet()) {
            text.append(ch);
        }
        var lexer = fromString(text.toString());
        do {
            var token = lexer.parse();
            if (token.getKind() == Kind.EOF) {
                break;
            }
            assertEquals(table.lookupSeparator(token.getLexeme().charAt(0)), token.getKind());
        } while (true);
    }

    @Test
    void testInvalidCharacter() {
        assertThrows(LexicalError.class, () -> fromString("()").parse());
    }

    static Tokenizer fromString(String source) throws IOException {
        var stream = new BufferedCharStream(new ByteArrayInputStream(source.getBytes()));
        return new Tokenizer(table, stream);
    }
}
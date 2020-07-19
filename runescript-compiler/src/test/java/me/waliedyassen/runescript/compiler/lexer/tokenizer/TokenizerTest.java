/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.lexer.tokenizer;

import lombok.var;
import me.waliedyassen.runescript.compiler.lexer.LexicalError;
import me.waliedyassen.runescript.commons.stream.BufferedCharStream;
import me.waliedyassen.runescript.compiler.ScriptCompiler;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringBufferInputStream;

import static me.waliedyassen.runescript.compiler.lexer.token.Kind.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Holds all of the test cases for {@link Tokenizer} type.
 *
 * @author Walied K. Yassen
 */
@SuppressWarnings("deprecation")
class TokenizerTest {

    @Test
    void testUnexpectedCharacters() {
        // this is unexpected because escapes are only allowed within strings
        assertThrows(LexicalError.class, () -> fromString("\\").parse());
    }

    @Test
    void testStringLiteral() {
        assertAll("string literal", () -> {
            // valid string without escapes
            var token = fromString("\"Basic Sample\"").parse();
            assertEquals(token.getKind(), Kind.STRING);
            assertEquals("Basic Sample", token.getLexeme());
        }, () -> {
            // valid string with escapes
            var token = fromString("\" \\< \\> \\b \\t \\n \\f \\\" \\\\ \"").parse();
            assertEquals(token.getKind(), Kind.STRING);
            assertEquals(" < > \b \t \n \f \" \\ ", token.getLexeme());
        }, () -> {
            // valid empty string
            assertEquals(fromString("\"\"").parse().getLexeme(), "");
        }, () -> {
            // invalid string end
            assertThrows(LexicalError.class, () -> fromString("\"test").parse());
            assertThrows(LexicalError.class, () -> fromString("\"\n").parse());
        }, () -> {
            // invalid string escapes.
            assertThrows(LexicalError.class, () -> fromString("\"\\g\"").parse());
        });
    }


    @Test
    void testStringInterpolation() {
        var tokenizer = fromString("\"Literal <tostring(1)> another literal <tostring(2)> and <function(\"number is <tostring(6)>\">\"");
        var expected = new Kind[]{CONCATB, STRING, IDENTIFIER, LPAREN, INTEGER, RPAREN, STRING, IDENTIFIER, LPAREN, INTEGER, RPAREN, STRING, IDENTIFIER, LPAREN, CONCATB, STRING, IDENTIFIER, LPAREN, INTEGER, RPAREN, CONCATE, CONCATE,};
        for (var kind : expected) {
            var token = tokenizer.parse();
            assertEquals(token.getKind(), kind);
        }
    }

    @Test
    void testLineComment() {
        var tokenizer = fromString("\"Test\"// I am a comment");
        var token = tokenizer.parse();
        assertEquals(token.getKind(), Kind.STRING);
        token = tokenizer.parse();
        assertEquals(token.getKind(), Kind.COMMENT);
        assertEquals(token.getLexeme(), "I am a comment");
    }

    @Test
    void testMultilineComment() {
        assertAll("mulitline comment", () -> {
            // valid multi line comment
            var tokenizer = fromString("		/*\r\n" +
                    "		 * Line with the star decoration.\r\n" +
                    "		   Line without the star decoration.\r\n" +
                    "		 * \r\n" +
                    "		 */");
            var token = tokenizer.parse();
            assertEquals(token.getKind(), Kind.COMMENT);
            assertEquals(token.getLexeme(), "Line with the star decoration.\nLine without the star decoration.\n");
        }, () -> {
            // valid single line comment
            assertEquals(fromString("/*hey*/").parse().getLexeme(), "hey");
        }, () -> {
            // unclosed multi line comment
            assertThrows(LexicalError.class, () -> fromString("/*\nline1\nline2").parse());
        });
    }

    @Test
    void testIdentifier() {
        assertAll("identifier", () -> {
            // identifier start
            var identifiers = new String[]{"myIdentifier0", "My_Identifier1", "_my_identifier2"};
            for (var identifier : identifiers) {
                var token = fromString(identifier).parse();
                assertEquals(token.getKind(), Kind.IDENTIFIER);
                assertEquals(token.getLexeme(), identifier);
            }
        });
    }

    @Test
    void testKeywords() {
        var tokenizer = fromString("true\tfalse");
        var trueToken = tokenizer.parse();
        assertEquals(trueToken.getKind(), Kind.BOOL);
        assertEquals(trueToken.getLexeme(), "true");
        var falseToken = tokenizer.parse();
        assertEquals(falseToken.getKind(), Kind.BOOL);
        assertEquals(falseToken.getLexeme(), "false");
    }

    @Test
    void testSeparators() {
        var tokenizer = fromString("[label] { }");
        var expected = new Kind[]{Kind.LBRACKET, Kind.IDENTIFIER, Kind.RBRACKET, Kind.LBRACE, Kind.RBRACE};
        for (var kind : expected) {
            var token = tokenizer.parse();
            assertEquals(token.getKind(), kind);
        }
    }

    @Test
    void testOperatorsSimple() {
        var tokenizer = fromString("test = \"Hello\";");
        assertEquals(tokenizer.parse().getKind(), Kind.IDENTIFIER);
        assertEquals(tokenizer.parse().getKind(), Kind.EQUALS);
        assertEquals(tokenizer.parse().getKind(), Kind.STRING);
        assertEquals(tokenizer.parse().getKind(), Kind.SEMICOLON);
    }


    @Test
    void testCoordLiteral() {
        var tokenzier = fromString("0_5_5_5_5");
        assertEquals(tokenzier.parse().getKind(), COORDGRID);
    }

    private Tokenizer fromString(String text) {
        try (var stream = new StringBufferInputStream(text)) {
            return new Tokenizer(ScriptCompiler.createLexicalTable(), new BufferedCharStream(stream));
        } catch (IOException e) {
            // won't happen anyways
            e.printStackTrace();
            return null;
        }
    }
}

/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.lexer.tokenizer;

import me.waliedyassen.runescript.commons.stream.BufferedCharStream;
import me.waliedyassen.runescript.compiler.lexer.table.LexicalTable;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringBufferInputStream;

import static me.waliedyassen.runescript.compiler.lexer.token.Kind.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Holds all of the test cases for {@link Tokenizer} type.
 *
 * @author Walied K. Yassen
 */
@SuppressWarnings("deprecation")
class TokenizerTest {

	@Test
	void testStringLiteralUnescaped() {
		var tokenizer = fromString("\"Basic Sample\"");
		var token = tokenizer.parse();
		assertEquals(token.getKind(), Kind.STRING);
		assertEquals("Basic Sample", token.getLexeme());
	}

	@Test
	void testStringLiteralEscaped() {
		var tokenizer = fromString("\"Escaped\\t\\\"Sample\"");
		var token = tokenizer.parse();
		assertEquals(token.getKind(), Kind.STRING);
		assertEquals("Escaped\t\"Sample", token.getLexeme());
	}

	@Test
	void testStringInterpolation() {
		var tokenizer = fromString("\"Literal <tostring(1)> another literal <tostring(2)> and <function(\"number is <tostring(6)>\">\"");
		var expected = new Kind[] { CONCATB, STRING, IDENTIFIER, LPAREN, INTEGER, RPAREN, STRING, IDENTIFIER, LPAREN, INTEGER, RPAREN, STRING, IDENTIFIER, LPAREN, CONCATB, STRING, IDENTIFIER, LPAREN, INTEGER, RPAREN, CONCATE, CONCATE, };
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
		//@formatter:off
		var tokenizer = fromString("		/*\r\n" +
										"		 * Line with the star decoration.\r\n" +
										"		   Line without the star decoration.\r\n" +
										"		 * \r\n" +
										"		 */");
		//@formatter:on
		var token = tokenizer.parse();
		assertEquals(token.getKind(), Kind.COMMENT);
		assertEquals(token.getLexeme(), "Line with the star decoration.\nLine without the star decoration.\n");

	}

	@Test
	void testIdentifier() {
		var tokenizer = fromString("654321myIdentifier");
		var token = tokenizer.parse();
		assertEquals(token.getKind(), Kind.INTEGER);
		assertEquals(token.getLexeme(), String.valueOf(654321));
		token = tokenizer.parse();
		assertEquals(token.getKind(), Kind.IDENTIFIER);
		assertEquals(token.getLexeme(), "myIdentifier");
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
		var expected = new Kind[] { Kind.LBRACKET, Kind.IDENTIFIER, Kind.RBRACKET, Kind.LBRACE, Kind.RBRACE };
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

	private Tokenizer fromString(String text) {
		try (var stream = new StringBufferInputStream(text)) {
			return new Tokenizer(LexicalTable.DEFAULT_TABLE, new BufferedCharStream(stream));
		} catch (IOException e) {
			// won't happen anyways
			e.printStackTrace();
			return null;
		}
	}
}

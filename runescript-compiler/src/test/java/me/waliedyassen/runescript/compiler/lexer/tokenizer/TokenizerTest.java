/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.lexer.tokenizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;

import org.junit.jupiter.api.Test;

import me.waliedyassen.runescript.commons.stream.BufferedCharStream;
import me.waliedyassen.runescript.compiler.lexer.table.LexicalTable;
import me.waliedyassen.runescript.compiler.lexer.token.CommentToken;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.lexer.token.Token;

/**
 * Holds all of the test cases for {@link Tokenizer} type.
 * 
 * @author Walied K. Yassen
 */
class TokenizerTest {

	@Test
	void testStringLiteralUnescaped() {
		Tokenizer tokenizer = fromString("\"Basic Sample\"");
		Token token = tokenizer.parse();
		assertEquals(token.getKind(), Kind.STRING);
		assertEquals("Basic Sample", token.getLexeme());
	}

	@Test
	void testStringLiteralEscaped() {
		Tokenizer tokenizer = fromString("\"Escaped\\t\\\"Sample\"");
		Token token = tokenizer.parse();
		assertEquals(token.getKind(), Kind.STRING);
		assertEquals("Escaped\t\"Sample", token.getLexeme());
	}

	@Test
	void testLineComment() {
		Tokenizer tokenizer = fromString("\"Test\"// I am a comment");
		Token token = tokenizer.parse();
		assertEquals(token.getKind(), Kind.STRING);
		token = tokenizer.parse();
		assertEquals(token.getKind(), Kind.COMMENT);
		assertEquals(((CommentToken) token).getLines().get(0), "I am a comment");
	}

	@Test
	void testMultilineComment() {
		//@formatter:off
		Tokenizer tokenizer = fromString("		/*\r\n" +
										"		 * Line with the star decoration.\r\n" +
										"		   Line without the star decoration.\r\n" +
										"		 * \r\n" +
										"		 */");
		//@formatter:on
		Token token = tokenizer.parse();
		assertEquals(token.getKind(), Kind.COMMENT);
		CommentToken comment = (CommentToken) token;
		assertTrue(comment.getLines().size() == 3);
		assertEquals(comment.getLines().get(0), "Line with the star decoration.");
		assertEquals(comment.getLines().get(1), "Line without the star decoration.");
		assertEquals(comment.getLines().get(2), "");

	}

	@Test
	void testNumber() {
		Tokenizer tokenizer = fromString("1234567 7654321  ");
		Token token = tokenizer.parse();
		assertEquals(token.getKind(), Kind.NUMBER);
		assertEquals(token.getLexeme(), String.valueOf(1234567));
		token = tokenizer.parse();
		assertEquals(token.getKind(), Kind.NUMBER);
		assertEquals(token.getLexeme(), String.valueOf(7654321));
	}

	@Test
	void testIdentifier() {
		Tokenizer tokenizer = fromString("654321myIdentifier");
		Token token = tokenizer.parse();
		assertEquals(token.getKind(), Kind.NUMBER);
		assertEquals(token.getLexeme(), String.valueOf(654321));
		token = tokenizer.parse();
		assertEquals(token.getKind(), Kind.IDENTIFIER);
		assertEquals(token.getLexeme(), "myIdentifier");
	}

	@Test
	void testKeywords() {
		Tokenizer tokenizer = fromString("true\tfalse");
		Token trueToken = tokenizer.parse();
		assertEquals(trueToken.getKind(), Kind.BOOL);
		assertEquals(trueToken.getLexeme(), "true");
		Token falseToken = tokenizer.parse();
		assertEquals(falseToken.getKind(), Kind.BOOL);
		assertEquals(falseToken.getLexeme(), "false");
	}

	@Test
	void testSeparators() {
		Tokenizer tokenizer = fromString("[label] { }");
		Kind[] expected = { Kind.LBRACKET, Kind.IDENTIFIER, Kind.RBRACKET, Kind.LBRACE, Kind.RBRACE };
		for (Kind kind : expected) {
			Token token = tokenizer.parse();
			assertEquals(token.getKind(), kind);
		}
	}

	private Tokenizer fromString(String text) {
		try (InputStream stream = new StringBufferInputStream(text)) {
			Tokenizer tokenizer = new Tokenizer(LexicalTable.DEFAULT_TABLE, new BufferedCharStream(stream));
			return tokenizer;
		} catch (IOException e) {
			// won't happen anyways
			e.printStackTrace();
			return null;
		}
	}

	private static Tokenizer fromResource(String name) {
		try (InputStream stream = ClassLoader.getSystemResourceAsStream(name)) {
			Tokenizer tokenizer = new Tokenizer(LexicalTable.DEFAULT_TABLE, new BufferedCharStream(stream));
			return tokenizer;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}

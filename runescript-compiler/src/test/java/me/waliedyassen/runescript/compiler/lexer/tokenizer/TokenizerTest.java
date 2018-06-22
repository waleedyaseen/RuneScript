/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.lexer.tokenizer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;

import org.junit.jupiter.api.Test;

import me.waliedyassen.runescript.commons.stream.BufferedCharStream;
import me.waliedyassen.runescript.compiler.lexer.token.Token;
import me.waliedyassen.runescript.compiler.lexer.token.TokenKind;

/**
 * Holds all of the test cases for {@link Tokenizer} type.
 * 
 * @author Walied K. Yassen
 */
class TokenizerTest {

	@Test
	void testBasicStringLiteral() {
		Tokenizer tokenizer = fromString("\"Basic Sample\"");
		Token token = tokenizer.parse();
		assertEquals(token.getKind(), TokenKind.STRING_LITERAL);
		assertEquals("Basic Sample", token.getLexeme());
	}

	@Test
	void testEscapedStringLiteral() {
		Tokenizer tokenizer = fromString("\"Basic\\t\\\"Sample\"");
		Token token = tokenizer.parse();
		assertEquals(token.getKind(), TokenKind.STRING_LITERAL);
		assertEquals("Basic\t\"Sample", token.getLexeme());
	}

	private Tokenizer fromString(String text) {
		try (InputStream stream = new StringBufferInputStream(text)) {
			Tokenizer tokenizer = new Tokenizer(new BufferedCharStream(stream));
			return tokenizer;
		} catch (IOException e) {
			// won't happen anyways
			e.printStackTrace();
			return null;
		}
	}

	private static Tokenizer fromResource(String name) {
		try (InputStream stream = ClassLoader.getSystemResourceAsStream(name)) {
			Tokenizer tokenizer = new Tokenizer(new BufferedCharStream(stream));
			return tokenizer;
		} catch (IOException e) {
			// won't happen anyways
			e.printStackTrace();
			return null;
		}
	}

}

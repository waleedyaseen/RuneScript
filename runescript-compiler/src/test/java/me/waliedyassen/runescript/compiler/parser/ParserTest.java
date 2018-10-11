/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;

import org.junit.jupiter.api.Test;

import me.waliedyassen.runescript.commons.stream.BufferedCharStream;
import me.waliedyassen.runescript.compiler.lexer.Lexer;
import me.waliedyassen.runescript.compiler.lexer.table.LexicalTable;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.lexer.tokenizer.Tokenizer;

/**
 * @author Walied K. Yassen
 */
final class ParserTest {

	@Test
	public void testIntParsing() {
		assertAll("int parsing",
		() -> {
			// non-signed integer.
			assertEquals(fromString("881251628").integerNumber().getValue(), 881251628);
		},
		() -> {
			// negative signed integer.
			assertEquals(fromString("-1040462968").integerNumber().getValue(), -1040462968);
		},
		() -> {
			// positive signed integer.
			assertEquals(fromString("1035471165").integerNumber().getValue(), 1035471165);
		}
		);
	}
	
	@Test
	public void testIntRange() {
		assertAll("int range", 
		() -> {
			// integer underflow
			SyntaxError error = assertThrows(SyntaxError.class, () -> fromString("-2147483649").integerNumber());
			assertNotNull(error);
			assertEquals(error.getToken().getKind(), Kind.INTEGER);
		},
		() -> {
			// integer overflow
			SyntaxError error = assertThrows(SyntaxError.class, () -> fromString("2147483648").integerNumber());
			assertNotNull(error);
			assertEquals(error.getToken().getKind(), Kind.INTEGER);
		},
		()->{
			// within range
			assertEquals(fromString("1785498889").integerNumber().getValue(), 1785498889);
		});
	}

	@Test
	public void testLongParsing() {
		assertAll("long parsing",
		() -> {
			// lower case long identifier
			assertEquals(fromString("4327430278518173700l").longNumber().getValue(), 4327430278518173700l);
		},
		() -> {
			// upper case long identifier
			assertEquals(fromString("5837188049693458000L").longNumber().getValue(), 5837188049693458000L);
		},
		() -> {
			// non-signed long.
			assertEquals(fromString("6883184492006257000L").longNumber().getValue(), 6883184492006257000L);
		},
		() -> {
			// negative signed long.
			assertEquals(fromString("-7226522914666815000L").longNumber().getValue(), -7226522914666815000L);
		},
		() -> {
			// positive signed long.
			assertEquals(fromString("+4809541778570648000L").longNumber().getValue(), +4809541778570648000L);
		}
		);
	}
	
	@Test
	public void testLongRange() {
		assertAll("long range", 
		() -> {
			// long underflow
			SyntaxError error = assertThrows(SyntaxError.class, () -> fromString("-9223372036854775809L").longNumber());
			assertNotNull(error);
			assertEquals(error.getToken().getKind(), Kind.LONG);
		},
		() -> {
			// long overflow
			SyntaxError error = assertThrows(SyntaxError.class, () -> fromString("9223372036854775808L").longNumber());
			assertNotNull(error);
			assertEquals(error.getToken().getKind(), Kind.LONG);
		},
		()->{
			// within range
			assertEquals(fromString("8490600559331033000L").longNumber().getValue(), 8490600559331033000L);
		});
	}


	private static Parser fromString(String text) {
		try (InputStream stream = new StringBufferInputStream(text)) {
			Tokenizer tokenizer = new Tokenizer(LexicalTable.DEFAULT_TABLE, new BufferedCharStream(stream));
			Lexer lexer = new Lexer(tokenizer);
			Parser parser = new Parser(lexer);
			return parser;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Parser fromResource(String name) {
		try (InputStream stream = ClassLoader.getSystemResourceAsStream(name)) {
			Tokenizer tokenizer = new Tokenizer(LexicalTable.DEFAULT_TABLE, new BufferedCharStream(stream));
			Lexer lexer = new Lexer(tokenizer);
			Parser parser = new Parser(lexer);
			return parser;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}

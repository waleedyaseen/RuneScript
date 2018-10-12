/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla 
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;

import org.junit.jupiter.api.Test;

import me.waliedyassen.runescript.commons.stream.BufferedCharStream;
import me.waliedyassen.runescript.compiler.ast.literal.AstInteger;
import me.waliedyassen.runescript.compiler.ast.literal.AstLong;
import me.waliedyassen.runescript.compiler.ast.literal.AstString;
import me.waliedyassen.runescript.compiler.ast.stmt.AstBlockStatement;
import me.waliedyassen.runescript.compiler.ast.stmt.AstIfStatement;
import me.waliedyassen.runescript.compiler.lexer.Lexer;
import me.waliedyassen.runescript.compiler.lexer.table.LexicalTable;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.lexer.tokenizer.Tokenizer;

/**
 * @author Walied K. Yassen
 */
@SuppressWarnings("deprecation")
final class ParserTest {

	@Test
	void testExpression() {
		assertAll("expression", () -> {
			// string
			assertTrue(fromString("\"myString\"").expression() instanceof AstString);
		}, () -> {
			// integer
			assertTrue(fromString("123456").expression() instanceof AstInteger);
		}, () -> {
			// long
			assertTrue(fromString("123456L").expression() instanceof AstLong);
		}, () -> {
			// empty
			assertThrows(SyntaxError.class, () -> fromString("").expression());
		}, () -> {
			// not expression
			assertThrows(SyntaxError.class, () -> fromString("if(123);").expression());
		});
	}

	@Test
	void testParExpression() {
		assertAll("par expression", () -> {
			// valid expression
			assertTrue(fromString("(1234)").parExpression() instanceof AstInteger);
		}, () -> {
			// invalid expression 1
			assertThrows(SyntaxError.class, () -> fromString("(1234").parExpression());
		}, () -> {
			// invalid expression 2
			assertThrows(SyntaxError.class, () -> fromString("1234)").parExpression());
		});
	}

	@Test
	void testStatement() {
		assertAll("statement", () -> {
			// valid if statement
			assertTrue(fromString("if(1234){}").statement() instanceof AstIfStatement);
		}, () -> {
			// valid block statement
			assertTrue(fromString("{}").statement() instanceof AstBlockStatement);
		}, () -> {
			// empty statement
			assertThrows(SyntaxError.class, () -> fromString("").statement());
		}, () -> {
			// invalid statement
			assertThrows(SyntaxError.class, () -> fromString("123456").statement());
		});
	}

	@Test
	void testIfStatement() {
		assertAll("if statement", () -> {
			// valid if statement
			assertTrue(fromString("if(1){}").ifStatement() instanceof AstIfStatement);
		}, () -> {
			// no code statement
			assertThrows(SyntaxError.class, () -> fromString("if(2)").ifStatement());
		}, () -> {
			// no condition expression
			assertThrows(SyntaxError.class, () -> fromString("if(){}").ifStatement());
		});
	}

	@Test
	void testBlockStatement() {
		assertAll("braced block", () -> {
			// valid block
			AstBlockStatement statements = fromString("{if(1234){}}").blockStatement();
			assertNotNull(statements);
			assertTrue(statements.getStatements().length == 1);
			assertTrue(statements.getStatements()[0] instanceof AstIfStatement);
		}, () -> {
			// empty block
			assertTrue(fromString("{}").blockStatement() instanceof AstBlockStatement);
		}, () -> {

			// unclosed block
			assertThrows(SyntaxError.class, () -> fromString("{").blockStatement());
		});
	}

	@Test
	void testUnbracedBlock() {
		assertAll("unbraced block", () -> {
			// meaningful block
			AstBlockStatement statement = fromString("if(1){}if(2){}").unbracedBlockStatement();
			assertNotNull(statement);
			assertTrue(statement.getStatements().length == 2);
			assertTrue(statement.getStatements()[0] instanceof AstIfStatement);
			assertTrue(statement.getStatements()[1] instanceof AstIfStatement);
		}, () -> {
			// empty block
			assertTrue(fromString("").unbracedBlockStatement() instanceof AstBlockStatement);
		});
	}

	@Test
	void testIntParsing() {
		assertAll("int parsing", () -> {
			// non-signed integer.
			assertEquals(fromString("881251628").integerNumber().getValue(), 881251628);
		}, () -> {
			// negative signed integer.
			assertEquals(fromString("-1040462968").integerNumber().getValue(), -1040462968);
		}, () -> {
			// positive signed integer.
			assertEquals(fromString("1035471165").integerNumber().getValue(), 1035471165);
		});
	}

	@Test
	void testIntRange() {
		assertAll("int range", () -> {
			// integer underflow
			SyntaxError error = assertThrows(SyntaxError.class, () -> fromString("-2147483649").integerNumber());
			assertNotNull(error);
			assertEquals(error.getToken().getKind(), Kind.INTEGER);
		}, () -> {
			// integer overflow
			SyntaxError error = assertThrows(SyntaxError.class, () -> fromString("2147483648").integerNumber());
			assertNotNull(error);
			assertEquals(error.getToken().getKind(), Kind.INTEGER);
		}, () -> {
			// within range
			assertEquals(fromString("1785498889").integerNumber().getValue(), 1785498889);
		});
	}

	@Test
	void testLongParsing() {
		assertAll("long parsing", () -> {
			// lower case long identifier
			assertEquals(fromString("4327430278518173700l").longNumber().getValue(), 4327430278518173700l);
		}, () -> {
			// upper case long identifier
			assertEquals(fromString("5837188049693458000L").longNumber().getValue(), 5837188049693458000L);
		}, () -> {
			// non-signed long.
			assertEquals(fromString("6883184492006257000L").longNumber().getValue(), 6883184492006257000L);
		}, () -> {
			// negative signed long.
			assertEquals(fromString("-7226522914666815000L").longNumber().getValue(), -7226522914666815000L);
		}, () -> {
			// positive signed long.
			assertEquals(fromString("+4809541778570648000L").longNumber().getValue(), +4809541778570648000L);
		});
	}

	@Test
	void testLongRange() {
		assertAll("long range", () -> {
			// long underflow
			SyntaxError error = assertThrows(SyntaxError.class, () -> fromString("-9223372036854775809L").longNumber());
			assertNotNull(error);
			assertEquals(error.getToken().getKind(), Kind.LONG);
		}, () -> {
			// long overflow
			SyntaxError error = assertThrows(SyntaxError.class, () -> fromString("9223372036854775808L").longNumber());
			assertNotNull(error);
			assertEquals(error.getToken().getKind(), Kind.LONG);
		}, () -> {
			// within range
			assertEquals(fromString("8490600559331033000L").longNumber().getValue(), 8490600559331033000L);
		});
	}

	@Test
	void testString() {
		assertEquals(fromString("\"my test string\"").string().getValue(), "my test string");
	}

	@Test
	void testBool() {
		assertAll("bool", () -> {
			// valid boolean
			assertTrue(fromString("true").bool().getValue());
			assertFalse(fromString("false").bool().getValue());
		}, () -> {
			// invalid boolean
			assertThrows(SyntaxError.class, () -> fromString("tru").bool());
		});
	}

	@Test
	void testIdentifier() {
		assertEquals(fromString("testKeyword").identifier().getText(), "testKeyword");
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

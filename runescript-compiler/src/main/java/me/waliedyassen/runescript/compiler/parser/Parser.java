/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.parser;

import static me.waliedyassen.runescript.compiler.lexer.token.Kind.BOOL;
import static me.waliedyassen.runescript.compiler.lexer.token.Kind.COMMA;
import static me.waliedyassen.runescript.compiler.lexer.token.Kind.IDENTIFIER;
import static me.waliedyassen.runescript.compiler.lexer.token.Kind.IF;
import static me.waliedyassen.runescript.compiler.lexer.token.Kind.INTEGER;
import static me.waliedyassen.runescript.compiler.lexer.token.Kind.LBRACE;
import static me.waliedyassen.runescript.compiler.lexer.token.Kind.LBRACKET;
import static me.waliedyassen.runescript.compiler.lexer.token.Kind.LONG;
import static me.waliedyassen.runescript.compiler.lexer.token.Kind.LPAREN;
import static me.waliedyassen.runescript.compiler.lexer.token.Kind.RBRACE;
import static me.waliedyassen.runescript.compiler.lexer.token.Kind.RBRACKET;
import static me.waliedyassen.runescript.compiler.lexer.token.Kind.RPAREN;
import static me.waliedyassen.runescript.compiler.lexer.token.Kind.STRING;

import java.util.ArrayList;
import java.util.List;

import me.waliedyassen.runescript.commons.document.Element;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.AstScript;
import me.waliedyassen.runescript.compiler.ast.expr.AstExpression;
import me.waliedyassen.runescript.compiler.ast.expr.AstIdentifier;
import me.waliedyassen.runescript.compiler.ast.literal.AstBool;
import me.waliedyassen.runescript.compiler.ast.literal.AstInteger;
import me.waliedyassen.runescript.compiler.ast.literal.AstLong;
import me.waliedyassen.runescript.compiler.ast.literal.AstString;
import me.waliedyassen.runescript.compiler.ast.stmt.AstBlockStatement;
import me.waliedyassen.runescript.compiler.ast.stmt.AstIfStatement;
import me.waliedyassen.runescript.compiler.ast.stmt.AstStatement;
import me.waliedyassen.runescript.compiler.lexer.Lexer;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.lexer.token.Token;

/**
 * Represents the grammar parser, it takes a {@link Lexer} fed with
 * {@link Token} objects, and then it attempts to apply our RuneScript grammar
 * rules to these tokens.
 * 
 * @author Walied K. Yassen
 */
public final class Parser {

	// TODO: Detailed documentation
	// TODO: Add accurate Range in every parsing rule.

	/**
	 * The lexical phase result object.
	 */
	private final Lexer lexer;

	/**
	 * Constructs a new {@link Parser} type object instance.
	 * 
	 * @param lexer
	 *              the lexical phase result object.
	 */
	public Parser(Lexer lexer) {
		this.lexer = lexer;
	}

	/**
	 * Attempts to match all of the next tokens to a {@link AstScript} object.
	 * 
	 * @return the parsed {@link AstScript} object.
	 */
	public AstScript script() {
		// ------------------ the header parsing ------------------//
		consume(LBRACKET);
		AstIdentifier trigger = identifier();
		consume(COMMA);
		AstIdentifier name = identifier();
		consume(RBRACKET);
		// ------------------ the code parsing ------------------//
		List<AstStatement> statements = new ArrayList<AstStatement>();
		// keep parsing until we have no more tokens left in the file.
		while (lexer.remaining() > 0) {
			// check whether we reached the end of the file or not.
			if (peekKind() == Kind.EOF) {
				break;
			}
			statements.add(statement());
		}
		return new AstScript(trigger, name, statements.toArray(new AstStatement[statements.size()]));
	}

	/**
	 * Attempts to match the next token to any {@link AstExpression} sub-class
	 * object instance.
	 * 
	 * @return the parsed {@link AstExpression} object.
	 */
	public AstExpression expression() {
		Kind kind = peekKind();
		switch (kind) {
		case INTEGER:
			return integerNumber();
		case LONG:
			return longNumber();
		case STRING:
			return string();
		case BOOL:
			return bool();
		default:
			throw createError(consume(), "Expecting an expression");
		}
	}

	/**
	 * Attempts to parse an {@link AstExpression} that is surrounded with
	 * parenthesis. The return value is equal to calling {@link #expression()}
	 * method, the only difference in this method that it checks for parenthesis
	 * before and after the expression and consume them.
	 * 
	 * @return the parsed {@link AstExpression} object.
	 */
	public AstExpression parExpression() {
		consume(LPAREN);
		AstExpression expression = expression();
		consume(RPAREN);
		return expression;
	}

	/**
	 * Attempts to match the next token set to any valid {@link AstStatement} types.
	 * 
	 * @return the matched {@link AstStatement} type object instance.
	 */
	public AstStatement statement() {
		Kind kind = peekKind();
		switch (kind) {
		case IF:
			return ifStatement();
		case LBRACE:
			return blockStatement();
		default:
			throw createError(consume(), "Expecting a statement");
		}
	}

	/**
	 * Checks whether or not the next token is a valid statement start.
	 * 
	 * @return <code>true</code> if it is otherwise <code>false</code>.
	 */
	private boolean isStatement() {
		Kind kind = peekKind();
		return kind == IF || kind == LBRACE;
	}

	/**
	 * Attempts to match the next token set to an if-statement rule.
	 * 
	 * @return the matched {@link AstIfStatement} type object instance.
	 */
	public AstIfStatement ifStatement() {
		Token start = consume(IF);
		AstExpression expression = parExpression();
		AstStatement statement = statement();
		return new AstIfStatement(makeRange(start), expression, statement);
	}

	/**
	 * Attempts to match the next token set to an block-statement rule.
	 * 
	 * @return the matched {@link AstBlockStatement} type object instance.
	 */
	public AstBlockStatement blockStatement() {
		Token start = consume(LBRACE);
		AstStatement[] statements = statementsList();
		Token end = consume(RBRACE);
		return new AstBlockStatement(makeRange(start, end), statements);
	}

	/**
	 * Attempts to parse all of the next sequential code-statements.
	 * 
	 * @return the parsed code-statements as {@link AstBlockStatement} object.
	 */
	public AstBlockStatement unbracedBlockStatement() {
		AstStatement[] statements = statementsList();
		return new AstBlockStatement(null, statements);
	}

	/**
	 * Parses a list of sequential code statements.
	 * 
	 * @return the parsed code-statements as {@link AstStatement} array object.
	 */
	private AstStatement[] statementsList() {
		List<AstStatement> list = new ArrayList<AstStatement>();
		while (isStatement()) {
			list.add(statement());
		}
		return list.toArray(new AstStatement[list.size()]);

	}

	/**
	 * Attempts to match the next token to an {@link AstInteger} object instance.
	 * 
	 * @return the parsed {@link AstInteger} object.
	 */
	public AstInteger integerNumber() {
		Token token = consume(INTEGER);
		try {
			return new AstInteger(makeRange(token), Integer.parseInt(token.getLexeme()));
		} catch (NumberFormatException e) {
			throw createError(token, "The literal " + token.getLexeme() + " of type int is out of range");
		}
	}

	/**
	 * Attempts to match the next token to an {@link AstLong} object instance.
	 * 
	 * @return the parsed {@link AstLong} object.
	 */
	public AstLong longNumber() {
		Token token = consume(LONG);
		try {
			return new AstLong(makeRange(token), Long.parseLong(token.getLexeme()));
		} catch (NumberFormatException e) {
			throw createError(token, "The literal " + token.getLexeme() + " of type long is out of range");
		}
	}

	/**
	 * Attempts to match the next token to an {@link AstString} object.
	 * 
	 * @return the parsed {@link AstString} object.
	 */
	public AstString string() {
		Token token = consume(STRING);
		return new AstString(makeRange(token), token.getLexeme());
	}

	/**
	 * Attempts to the match the next token to an {@link AstBool} object.
	 * 
	 * @return the parsed {@link AstBool} object.
	 */
	public AstBool bool() {
		Token token = consume(BOOL);
		return new AstBool(makeRange(token), Boolean.parseBoolean(token.getLexeme()));
	}

	/**
	 * Attempts to match the next list of tokens to an {@link AstIdentifier} object.
	 * 
	 * @return the parsed {@link AstIdentifier} object.
	 */
	public AstIdentifier identifier() {
		Token token = consume(IDENTIFIER);
		return new AstIdentifier(makeRange(token), token.getLexeme());
	}

	/**
	 * Takes the next {@link Token} object and checks whether or not it's
	 * {@linkplain Kind kind} matches the specified {@linkplain Kind kind}.
	 * 
	 * @param expected
	 *                 the expected token kind.
	 * @return the expected {@link Token} object.
	 * @throws SyntaxError
	 *                     if the next token does not match the expected token.
	 */
	public Token consume(Kind expected) {
		Token token = consume();
		Kind kind = token == null ? Kind.EOF : token.getKind();
		if (kind != expected) {
			throwError(token, "Unexpected rule: " + kind + ", expected: " + expected);
		}
		return token;
	}

	/**
	 * Takes the next {@link Token} object from the lexer.
	 * 
	 * @return the next {@link Token} object or {@code null}.
	 * @see Lexer#take()
	 */
	public Token consume() {
		return lexer.take();
	}

	/**
	 * Takes the next {@link Token} object from the lexer and return it's kind if it
	 * was present or {@link Kind#EOF}.
	 * 
	 * @return the token {@link Kind} or {@link Kind#EOF} if it was not present.
	 */
	public Kind kind() {
		Token token = consume();
		if (token == null) {
			return Kind.EOF;
		}
		return token.getKind();
	}

	/**
	 * Takes the next {@link Token} object without advancing the lexer cursor.
	 * 
	 * @return the next {@link Token} object or {@code null}.
	 * @see Lexer#peek()
	 */
	public Token peek() {
		return lexer.peek();
	}

	/**
	 * Gets the next token {@link Kind} from the lexer without advancing the lexer
	 * cursor.
	 * 
	 * @return the next {@link Kind} or {@link Kind#EOF} if there is no more tokens.
	 */
	public Kind peekKind() {
		Token token = peek();
		if (token == null) {
			return Kind.EOF;
		}
		return token.getKind();
	}

	/**
	 * Creates a new {@link Range} object which includes the range of each one of
	 * the specified {@code elements}.
	 * 
	 * @param elements
	 *                 the elements which we will take the ranges from.
	 * @return the created {@link Range} object.
	 */
	private Range makeRange(Element... elements) {
		Range range = new Range();
		for (Element element : elements) {
			range.add(element.getRange());
		}
		return range;
	}

	/**
	 * Throws a syntax error indicating a mismatched grammar rule.
	 * 
	 * @param token
	 *                the token which the error has occurred at.
	 * @param message
	 *                the error message describing why the error has occurred.
	 */
	private void throwError(Token token, String message) {
		throw createError(token, message);
	}

	/**
	 * Creates a syntax error indicating a mismatched grammar rule.
	 * 
	 * @param token
	 *                the token which the error has occurred at.
	 * @param message
	 *                the error message describing why the error has occurred
	 * @return the created {@link SyntaaxError} object.
	 */
	private SyntaxError createError(Token token, String message) {
		return new SyntaxError(token, message);
	}
}

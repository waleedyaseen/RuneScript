/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.parser;

import static me.waliedyassen.runescript.compiler.lexer.token.Kind.IDENTIFIER;
import static me.waliedyassen.runescript.compiler.lexer.token.Kind.STRING;

import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.expr.AstIdentifier;
import me.waliedyassen.runescript.compiler.ast.literal.AstString;
import me.waliedyassen.runescript.compiler.lexer.Lexer;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.lexer.token.Token;

/**
 * Represents the grammar parser, it takes a {@link Lexer} fed with {@link Token} objects, and then it attempts to apply
 * our RuneScript grammar rules to these tokens.
 * 
 * @author Walied K. Yassen
 */
public final class Parser {

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
	 * Attempts to match the next token to an {@link AstString} object.
	 * 
	 * @return the parsed {@link AstString} object.
	 */
	public AstString string() {
		Token token = expect(STRING);
		return new AstString(makeRange(token), token.getLexeme());
	}

	/**
	 * Attempts to match the next list of tokens to an {@link AstIdentifier} object.
	 * 
	 * @return the parsed {@link AstIdentifier} object.
	 */
	public AstIdentifier identifier() {
		Token token = expect(IDENTIFIER);
		return new AstIdentifier(makeRange(token), token.getLexeme());
	}

	/**
	 * Takes the next {@link Token} object and checks whether or not it's {@linkplain Kind kind} matches the specified
	 * {@linkplain Kind kind}.
	 * 
	 * @param kind
	 *             the expected token kind.
	 * @return the expected {@link Token} object.
	 * @throws SyntaxError
	 *                     if the next token does not match the expected token.
	 */
	public Token expect(Kind kind) {
		Token token = lexer.take();
		if (kind != token.getKind()) {
			throwError(token, "Unexpected rule: " + token.getKind() + ", expected: " + kind);
		}
		return token;
	}

	/**
	 * Creates a new {@link Range} object which includes the range of each one of the specified {@code tokens}.
	 * 
	 * @param tokens
	 *               the tokens which we will take the ranges from.
	 * @return the created {@link Range} object.
	 */
	private Range makeRange(Token... tokens) {
		Range range = new Range();
		for (Token token : tokens) {
			range.add(token.getRange());
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
		throw new SyntaxError(token, message);
	}
}

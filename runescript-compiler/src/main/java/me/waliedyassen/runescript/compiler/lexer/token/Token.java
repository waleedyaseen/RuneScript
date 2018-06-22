/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.lexer.token;

import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.lexer.tokenizer.Tokenizer;

/**
 * Represents the base class of the {@link Tokenizer} output. A token is the smallest unit of the source code in the
 * lexical analysis, usually one token repersents a single symbol.
 * 
 * @author Walied K. Yassen
 */
public class Token {

	/**
	 * The token kind.
	 */
	private final TokenKind kind;

	/**
	 * The token source code range.
	 */
	private final Range range;

	/**
	 * The token lexeme value.
	 */
	private final String lexeme;

	/**
	 * Constructs a new {@link Token} type object instance.
	 * 
	 * @param kind
	 *               the token kind which tells what the token is.
	 * @param range
	 *               the token source code range.
	 * @param lexeme
	 *               the token lexeme value
	 */
	public Token(TokenKind kind, Range range, String lexeme) {
		this.kind = kind;
		this.range = range;
		this.lexeme = lexeme;
	}

	/**
	 * Constructs a new {@link Token} type object instance.
	 * 
	 * @param kind
	 *               the token kind which tells what the token is.
	 * @param range
	 *               the token source code range.
	 */
	public Token(TokenKind kind, Range range) {
		this(kind, range, null);
	}

	/**
	 * Gets the token kind.
	 * 
	 * @return the token kind.
	 */
	public TokenKind getKind() {
		return kind;
	}

	/**
	 * Gets the source code range.
	 * 
	 * @return the source code range.
	 */
	public Range getRange() {
		return range;
	}

	/**
	 * Gets the token lexeme value.
	 * 
	 * @return the lexeme value.
	 */
	public String getLexeme() {
		return lexeme;
	}

}

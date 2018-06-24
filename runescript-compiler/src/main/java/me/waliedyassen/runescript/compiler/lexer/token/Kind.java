/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.lexer.token;

/**
 * Represents the token kind, which tells us what each token is, whether an identifier, or a number, or a string, or a
 * keyword etc..
 * 
 * @author Walied K. Yassen
 */
public enum Kind {

	// the core chunk.

	/**
	 * The identifier token kind.
	 */
	IDENTIFIER,

	/**
	 * The number literal token kind.
	 */
	NUMBER,

	/**
	 * The string literal token kind.
	 */
	STRING,

	/**
	 * The boolean literal token kind.
	 */
	BOOL,

	// the separators chunk,

	/**
	 * The left parenthesis separator token kind.
	 */
	LPAREN,

	/**
	 * The right parenthesis separator token kind.
	 */
	RPAREN,

	/**
	 * The left bracket separator token kind.
	 */
	LBRACKET,

	/**
	 * The right bracket separator token kind.
	 */
	RBRACKET,

	/**
	 * The left brace separator token kind.
	 */
	LBRACE,

	/**
	 * The right brace separator token kind.
	 */
	RBRACE,

	/**
	 * The comma separator token kind.
	 */
	COMMA,

	/**
	 * The semicolon separator token kind.
	 */
	SEMICOLON,

	/* the operators chunk */
	
	/**
	 * The equal operator token kind.
	 */
	EQUAL,
	
	// the misc chunk.

	/**
	 * The comment token kind.
	 */
	COMMENT,

	/**
	 * The end of file token kind.
	 */
	EOF,
}

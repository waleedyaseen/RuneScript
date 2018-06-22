/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.token;

import me.waliedyassen.runescript.commons.document.Range;

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
	 * Constructs a new {@link Token} type object instance.
	 * 
	 * @param kind
	 *              the token kind which tells what the token is.
	 * @param range
	 *              the token source code range.
	 */
	public Token(TokenKind kind, Range range) {
		this.kind = kind;
		this.range = range;
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

}

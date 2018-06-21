/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.tokeniser;

import me.waliedyassen.runescript.commons.document.LineColumn;

/**
 * Represents the base class of the {@link Tokenizer} output. A token is the smallest unit of the source code in the
 * lexical analysis, usually one token repersents a single symbol.
 * 
 * @author Walied K. Yassen
 */
public abstract class Token {

	/**
	 * The token kind.
	 */
	private final int kind;

	/**
	 * The token beginning positin within the document.
	 */
	private final LineColumn start;

	/**
	 * The toen ending position within the document.
	 */
	private final LineColumn end;

	/**
	 * Constructs a new {@link Token} type object instance.
	 * 
	 * @param kind
	 *                  the token kind which tells what the token is.
	 * @param start
	 *                  the token beginning position within the document.
	 * @param end
	 *                  the token ending position within the document.
	 */
	public Token(int kind, LineColumn start, LineColumn end) {
		this.kind = kind;
		this.start = start;
		this.end = end;
	}

	/**
	 * Gets the token kind.
	 * 
	 * @return the token kind.
	 */
	public int getKind() {
		return kind;
	}

	/**
	 * Gets the token beginning position within the document.
	 * 
	 * @return the token beginning position as {@link LineColumn} object.
	 */
	public LineColumn getStart() {
		return start;
	}

	/**
	 * Gets the token ending position within the document.
	 * 
	 * @return the token ending position as {@link LineColumn} object.
	 */
	public LineColumn getEnd() {
		return end;
	}

}

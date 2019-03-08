/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.lexer.token;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.waliedyassen.runescript.commons.document.Element;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.lexer.tokenizer.Tokenizer;

/**
 * Represents the base class of the {@link Tokenizer} output. A token is the
 * smallest unit of the source code in the lexical analysis, usually one token
 * represents a single symbol.
 * 
 * @author Walied K. Yassen
 */
@EqualsAndHashCode
@ToString
public class Token implements Element {

	/**
	 * The token kind.
	 */
	@Getter
	private final Kind kind;

	/**
	 * The token source code range.
	 */
	@Getter
	private final Range range;

	/**
	 * The token lexeme value.
	 */
	@Getter
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
	public Token(Kind kind, Range range, String lexeme) {
		this.kind = kind;
		this.range = range;
		this.lexeme = lexeme;
	}

	/**
	 * Constructs a new {@link Token} type object instance.
	 *
	 * @param kind
	 *              the token kind which tells what the token is.
	 * @param range
	 *              the token source code range.
	 */
	public Token(Kind kind, Range range) {
		this(kind, range, null);
	}
}

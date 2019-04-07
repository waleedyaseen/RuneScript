/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.lexer;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.lexer.token.Token;
import me.waliedyassen.runescript.compiler.lexer.tokenizer.Tokenizer;

/**
 * Represents the main lexical phase interface, it is responsible for collecting
 * all the {@link Token} objects from a {@link Tokenizer} object and to provide
 * the extra utilities we require in the lexical phase.
 *
 * @author Walied K. Yassen
 */
public final class Lexer {

	/**
	 * The list of the {@linkplain Token}s that are availabe to this lexer.
	 */
	private final List<Token> tokens = new ArrayList<Token>();

	/**
	 * The current pointer index value.
	 */
	private int index;

	/**
	 * Constructs a new {@link Lexer} type object instance.
	 *
	 * @param tokenizer the tokenizer which we will take all the {@link Token}
	 *                  objects from.
	 */
	public Lexer(Tokenizer tokenizer) {
		tokens: do {
			var token = tokenizer.parse();
			switch (token.getKind()) {
				case EOF:
					break tokens;
				case COMMENT:
					continue tokens;
				default:
					tokens.add(token);
			}
		} while (true);
	}

	/**
	 * Gets the {@link Token} object at the current pointer index and then increment
	 * the pointer index.
	 *
	 * @return the {@link Token} object if it was present otherwise {@code null}.
	 */
	public Token take() {
		if (index >= tokens.size()) {
			return null;
		}
		return tokens.get(index++);
	}

	/**
	 * Gets the {@link Token} object at the current pointer index without
	 * incrementing the pointer index.
	 *
	 * @return the {@link Token} object if it was present otherwise {@code null}.
	 */
	public Token peek() {
		if (index >= tokens.size()) {
			return null;
		}
		return tokens.get(index);
	}

	/**
	 * Gets the last {@link Token} object within this lexer.
	 *
	 * @return the last {@link Token} object.
	 */
	public Token last() {
		if (tokens.size() == 0) {
			return null;
		}
		return tokens.get(tokens.size() - 1);
	}

	/**
	 * Advances the current pointer index by one.
	 */
	public void advance() {
		if (index < tokens.size()) {
			index++;
		}
	}

	/**
	 * Gets the remaining tokens count.
	 *
	 * @return the remaining tokens count.
	 */
	public int remaining() {
		return tokens.size() - index;
	}
}

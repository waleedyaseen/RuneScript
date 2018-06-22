/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.lexer.tokenizer;

import static me.waliedyassen.runescript.commons.stream.CharStream.NULL;

import java.util.Arrays;

import me.waliedyassen.runescript.commons.document.LineColumn;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.commons.stream.CharStream;
import me.waliedyassen.runescript.compiler.lexer.token.CommentToken;
import me.waliedyassen.runescript.compiler.lexer.token.Token;

/**
 * Represents the tokenizer tool, takes {@link CharStream} object then turns it's content into {@link Token} objects.
 * 
 * @author Walied K. Yassen
 */
public final class Tokenizer {

	/**
	 * The characters input stream.
	 */
	private final CharStream stream;

	/**
	 * The current token lexeme builder.
	 */
	private final StringBuilder builder;

	/**
	 * The current token start position.
	 */
	private LineColumn position;

	/**
	 * Constructs a new {@link Tokenizer} type object instance.
	 * 
	 * @param stream
	 *               the source code input characters stream.
	 */
	public Tokenizer(CharStream stream) {
		this.stream = stream;
		builder = new StringBuilder();
	}

	/**
	 * Tokenize the next sequence of characters into some meaningful {@link Token} object.
	 * 
	 * @return the {@link Token} object or {@code null} if none could be tokenized.
	 */
	public Token parse() {
		State state = State.NONE;
		char current, next;
		while (true) {
			current = stream.take();
			next = stream.peek();
			switch (state) {
				case NONE:
					if (Character.isWhitespace(current)) {
						continue;
					} else {
						// considered as the token start position, mark it to be used later in #range() method.
						mark();
						if (current == '/' && next == '/') {
							// the single line comment token, starts with // until the end of the line
							stream.take();
							state = State.LINE_COMMENT;
						}
					}
				break;
				case LINE_COMMENT:
					if (current == NULL || current == '\n') {
						state = State.NONE;
						return new CommentToken(range(), Arrays.asList(builder.toString()));
					} else {
						builder.append(current);
					}
				break;

			}
		}
	}

	/**
	 * Marks the current position as the token start position.
	 */
	private void mark() {
		position = stream.position();
	}

	/**
	 * Creates a {@link Range} object starting at the current marked position and ending at the current position.
	 * 
	 * @return the creasted {@link Range} object.
	 * @see #mark()
	 */
	private Range range() {
		return new Range(position, stream.position());
	}

	/**
	 * Repesents the parser state.
	 * 
	 * @author Walied K. Yassen
	 */
	private static enum State {
		/**
		 * The parser is currently trying to find what kind of token is next.
		 */
		NONE,
		/**
		 * The parser is currently parsing a single line comment.
		 */
		LINE_COMMENT,
	}
}

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
import me.waliedyassen.runescript.compiler.lexer.LexicalError;
import me.waliedyassen.runescript.compiler.lexer.token.CommentToken;
import me.waliedyassen.runescript.compiler.lexer.token.Token;
import me.waliedyassen.runescript.compiler.lexer.token.TokenKind;

/**
 * Represents the tokenizer tool, takes {@link CharStream} object then turns it's content into {@link Token} objects.
 * 
 * @author Walied K. Yassen
 */
public final class Tokenizer {

	// Note: using the ECJ/JavaC tools error messages for now.

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
	 * Tokenizes the next sequence of characters into some meaningful {@link Token} object.
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
						mark();
						resetBuilder();
						if (current == '\"') {
							// the string token, starts with '"' until we meet another unescaped '"'
							state = State.STRING_LITERAL;
						} else if (current == '/' && next == '/') {
							// the single line comment token, starts with '//' until the end of the line
							stream.take();
							state = State.LINE_COMMENT;
						}
					}
					break;
				case STRING_LITERAL:
					if (current == NULL) {
						throwError("String literal is not properly closed by a double-quote");
					} else if (current == '\\') {
						stream.take();
						switch (next) {
							case 'b':
								builder.append('\b');
								break;
							case 't':
								builder.append('\t');
								break;
							case 'n':
								builder.append('\n');
								break;
							case 'f':
								builder.append('\f');
								break;
							case '"':
								builder.append('\"');
								break;
							case '\\':
								builder.append('\\');
								break;
							default:
								throwError("Invalid escape sequence (valid ones are  \\b  \\t  \\n  \\f  \\r  \\\"  \\'  \\\\)");
								break;
						}
					} else if (current == '\"') {
						return new Token(TokenKind.STRING_LITERAL, range(), builder.toString());
					} else {
						builder.append(current);
					}
					break;
				case LINE_COMMENT:
					if (current == NULL || current == '\n') {
						return new CommentToken(range(), Arrays.asList(builder.toString()));
					} else {
						builder.append(current);
					}
					break;

			}
		}
	}

	/**
	 * Resets the lexeme builder state.
	 */
	private void resetBuilder() {
		if (builder.length() > 0) {
			builder.setLength(0);
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
	 * @return the created {@link Range} object.
	 * @see #mark()
	 */
	private Range range() {
		return new Range(position, stream.position());
	}

	/**
	 * Creates and throws a parser error ranging from the marked position to the current position.
	 * 
	 * @param message
	 *                the error message of why the error has occurred.
	 */
	private void throwError(String message) {
		throw new LexicalError(range(), message);
	}

	/**
	 * Represents the parser state.
	 * 
	 * @author Walied K. Yassen
	 */
	private static enum State {
		/**
		 * Indicates that the parser is currently not parsing anything.
		 */
		NONE,

		/**
		 * Indicates that the parser is currently parsing a string literal.
		 */
		STRING_LITERAL,

		/**
		 * Indicates that the parser is currently parsing a line comment.
		 */
		LINE_COMMENT,
	}
}

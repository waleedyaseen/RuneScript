/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.lexer.tokenizer;

import static me.waliedyassen.runescript.commons.stream.CharStream.NULL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.waliedyassen.runescript.commons.document.LineColumn;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.commons.stream.CharStream;
import me.waliedyassen.runescript.compiler.lexer.LexicalError;
import me.waliedyassen.runescript.compiler.lexer.table.LexicalTable;
import me.waliedyassen.runescript.compiler.lexer.token.CommentToken;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.lexer.token.Token;

/**
 * Represents the tokenizer tool, takes {@link CharStream} object then turns it's content into {@link Token} objects.
 * 
 * @author Walied K. Yassen
 */
public final class Tokenizer {

	// Note: using the ECJ/JavaC tools error messages for now.

	/**
	 * The lexical symbol table.
	 */
	private final LexicalTable table;

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
	 * @param table
	 *               the lexical symbol table.
	 * @param stream
	 *               the source code input characters stream.
	 */
	public Tokenizer(LexicalTable table, CharStream stream) {
		this.table = table;
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
		List<String> comment = null;
		char current, next;
		while (true) {
			// mark the current position if we have no state yet.
			if (state == State.NONE) {
				mark();
			}
			// take the current and next characters from the stream.
			current = stream.take();
			next = stream.peek();
			// parse the current character depending on the current state.
			switch (state) {
				case NONE:
					if (Character.isWhitespace(current)) {
						continue;
					} else {
						resetBuilder();
						if (current == NULL) {
							return new Token(Kind.EOF, range());
						} else if (isIdentifierStart(current)) {
							builder.append(current);
							state = State.IDENTIFIER;
						} else if (current == '\"') {
							state = State.STRING_LITERAL;
						} else if (Character.isDigit(current)) {
							builder.append(current);
							state = State.NUMBER_LITERAL;
						} else if (current == '/' && next == '/') {
							stream.take();
							state = State.LINE_COMMENT;
						} else if (current == '/' && next == '*') {
							stream.take();
							comment = new ArrayList<String>();
							state = State.MULTI_COMMENT;
						} else if (table.isSeparator(current)) {
							return new Token(table.lookupSeparator(current), range(), Character.toString(current));
						} else {
							if (table.isOperatorStart(current)) {
								builder.append(current);
								for (int index = 1; index < table.getOperatorSize(); index++) {
									// TODO: update the behaviour of peek() to skip the CR character, the same thing take() does.
									if (!stream.hasRemaining() || stream.peek() == '\r' && stream.peek() == '\n') {
										break;
									}
									builder.append(stream.take());
								}
								while (builder.length() > 0) {
									String sequence = builder.toString();
									if (table.isOperator(sequence)) {
										return new Token(table.lookupOperator(sequence), range(), sequence);
									}
									builder.setLength(builder.length() - 1);
									stream.rollback(1);
								}
							}
							throwError("Unexpected character: " + current);
						}
					}
					break;
				case IDENTIFIER:
					if (isIdentifierPart(current)) {
						builder.append(current);
						stream.mark();
					} else {
						stream.reset();
						String word = builder.toString();
						return new Token(table.isKeyword(word) ? table.lookupKeyword(word) : Kind.IDENTIFIER, range(), builder.toString());
					}
					break;
				case STRING_LITERAL:
					if (current == NULL || current == '\n') {
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
						return new Token(Kind.STRING, range(), builder.toString());
					} else {
						builder.append(current);
					}
					break;
				case NUMBER_LITERAL:
					if (Character.isDigit(current)) {
						builder.append(current);
					} else {
						if (current == 'L' || current == 'l') {
							builder.append(current);
						} else {
							stream.rollback(1);
						}
						return new Token(Kind.NUMBER, range(), builder.toString());
					}
					break;
				case LINE_COMMENT:
					if (current == NULL || current == '\n') {
						return new CommentToken(range(), Arrays.asList(trimComment(builder.toString(), false)));
					} else {
						builder.append(current);
					}
					break;
				case MULTI_COMMENT:
					if (current == NULL) {
						throwError("Unexpected end of comment");
					} else if (current == '\n') {
						String line = trimComment(builder.toString(), true);
						// Ignores the header line if it was empty.
						if (comment.size() != 0 || line.length() != 0) {
							comment.add(line);
						}
						resetBuilder();
					} else if (current == '*' && next == '/') {
						stream.take();
						return new CommentToken(range(), comment);
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
	 * Trims the comment from any decoration they have, whether it was the line start decoration character or it was a
	 * redundant whitespace.
	 * 
	 * @param line
	 *                 the comment line.
	 * @param trimStar
	 *                 whether to trim the decorative star or not.
	 * @return the trimmed comment line content.
	 */
	private static String trimComment(String line, boolean trimStar) {
		int start = -1;
		for (int chpos = 0; chpos < line.length(); chpos++) {
			if (!Character.isWhitespace(line.charAt(chpos))) {
				start = chpos;
				break;
			}
		}
		if (start == -1) {
			return "";
		}
		if (trimStar && line.charAt(start) == '*') {
			return trimComment(line.substring(start + 1), false);
		}
		int end = -1;
		for (int chpos = line.length() - 1; chpos >= start; chpos--) {
			if (!Character.isWhitespace(line.charAt(chpos))) {
				end = chpos + 1;
				break;
			}
		}
		return line.substring(start, end);
	}

	/**
	 * Checks whether or not the specified character can be used as the identifier's starting character.
	 * 
	 * @param ch
	 *           the character to check.
	 * @return <code>true</code> if it can otherwise <code>false</code>.
	 */
	public static boolean isIdentifierStart(char ch) {
		return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch == '_';
	}

	/**
	 * Checks whether or not the specified character can be used as an identifier's character.
	 * 
	 * @param ch
	 *           the character to check.
	 * @return <code>true</code> if it can otherwise <code>false</code>.
	 */
	public static boolean isIdentifierPart(char ch) {
		return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9' || ch == '_';
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
		 * Indicates that the parser is currently parsing an identifier.
		 */
		IDENTIFIER,

		/**
		 * Indicates that the parser is currently parsing a string literal.
		 */
		STRING_LITERAL,

		/**
		 * Indicates that the parser is currently parsing a number literal.
		 */
		NUMBER_LITERAL,

		/**
		 * Indicates that the parser is currently parsing a line comment.
		 */
		LINE_COMMENT,

		/**
		 * Indicates that the parser is currently parsing a multi-line comment.
		 */
		MULTI_COMMENT,
	}
}
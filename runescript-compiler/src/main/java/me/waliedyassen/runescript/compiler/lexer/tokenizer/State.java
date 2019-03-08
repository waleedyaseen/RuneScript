package me.waliedyassen.runescript.compiler.lexer.tokenizer;

import me.waliedyassen.runescript.commons.document.LineColumn;
import me.waliedyassen.runescript.commons.stream.CharStream;

import java.util.List;

/**
 * Represents the {@link Tokenizer} state, it holds the current parsing
 * state variables for that tokenizer.
 *
 * @author Walied K. Yassen
 */
final class State {
	/**
	 * The current lexeme builder, it should be reset after each token.
	 */
	final StringBuilder builder = new StringBuilder();

	/**
	 * The current character position within the document
	 */
	LineColumn position;

	/**
	 * The current parsing mode, tells what we are currently parsing.
	 */
	Mode mode = Mode.NONE;

	/**
	 * A helper {@link List} of {@link String}, mainly used for multi-line
	 * comments.
	 */
	List<String> lines;

	/**
	 * Creates an empty {@link State} object instance.
	 *
	 * @return the created {@link State} object instance.
	 */
	static State emptyState() {
		return new State();
	}
}
package me.waliedyassen.runescript.compiler.lexer.tokenizer;

/**
 * Represents a parser mode, it tells what we are currently parsing
 * whether it is string literal, integral literal, or something else.
 *
 * @author Walied K. Yassen
 */
public enum Mode {
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
	MULTI_COMMENT
}
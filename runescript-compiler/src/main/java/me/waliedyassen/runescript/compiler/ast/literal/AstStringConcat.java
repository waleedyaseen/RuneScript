package me.waliedyassen.runescript.compiler.ast.literal;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.expr.AstExpression;

/**
 * Represents an interpolated string concatenation  node.
 *
 * @author Walied K. Yassen
 */
public final class AstStringConcat extends AstLiteral {

	/**
	 * The expressions of the concatenation.
	 */
	@Getter
	private final AstExpression[] expressions;

	/**
	 * Constructs a new {@link AstLiteral} type object instance.
	 *
	 * @param range
	 * 		the node source code range.
	 * @param expressions
	 * 		the expressions of the concatenation.
	 */
	public AstStringConcat(Range range, AstExpression[] expressions) {
		super(range);
		this.expressions = expressions;
	}
}

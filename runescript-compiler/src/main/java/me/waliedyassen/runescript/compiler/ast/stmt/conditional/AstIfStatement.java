/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.stmt.control;

import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.expr.AstExpression;
import me.waliedyassen.runescript.compiler.ast.stmt.AstStatement;

/**
 * Represents an if-statement in the Abstract Syntax Tree.
 *
 * @author Walied K. Yassen
 */
public final class AstIfStatement extends AstStatement {

	/**
	 * The if statement condition expression.
	 */
	private final AstExpression expression;

	/**
	 * The if true code statement.
	 */
	private final AstStatement trueStatement;

	/**
	 * The if false code statement.
	 */
	private final AstStatement falseStatement;

	/**
	 * Construct a new {@link AstIfStatement} type object instance.
	 *
	 * @param range
	 * 		the node source code range.
	 * @param expression
	 * 		the if statement condition expression.
	 * @param trueStatement
	 * 		the if true code statement.
	 * @param falseStatement
	 * 		the if false code statement.
	 */
	public AstIfStatement(Range range, AstExpression expression, AstStatement trueStatement, AstStatement falseStatement) {
		super(range);
		this.expression = expression;
		this.trueStatement = trueStatement;
		this.falseStatement = falseStatement;
	}

	/**
	 * Gets the if statement condition expression.
	 *
	 * @return the condition expression as {@link AstExpression} object.
	 */
	public AstExpression getExpression() {
		return expression;
	}

	/**
	 * Gets the if true code statement.
	 *
	 * @return the code statement as {@link AstStatement} object.
	 */
	public AstStatement getTrueStatement() {
		return trueStatement;
	}

	/**
	 * Gets the if false code statement.
	 *
	 * @return the code statement as {@link AstStatement} object.
	 */
	public AstStatement getFalseStatement() {
		return falseStatement;
	}
}

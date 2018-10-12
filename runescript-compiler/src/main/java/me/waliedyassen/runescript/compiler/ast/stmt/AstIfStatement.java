/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.stmt;

import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.expr.AstExpression;

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
	 * The if statement code statement.
	 */
	private final AstStatement statement;

	/**
	 * Construct a new {@link AstIfStatement} type object instance.
	 * 
	 * @param range
	 *                   the node source code range.
	 * @param expression
	 *                   the if statement condition expression.
	 * @param statement
	 *                   the if statement code statement.
	 */
	public AstIfStatement(Range range, AstExpression expression, AstStatement statement) {
		super(range);
		this.expression = expression;
		this.statement = statement;
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
	 * Gets the if statement code statement.
	 * 
	 * @return the code statement as {@link AstStatement} object.
	 */
	public AstStatement getStatement() {
		return statement;
	}
}

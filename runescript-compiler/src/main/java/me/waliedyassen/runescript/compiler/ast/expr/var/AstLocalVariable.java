/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.expr.var;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.expr.AstExpression;
import me.waliedyassen.runescript.compiler.ast.expr.AstIdentifier;

/**
 * Represents a local variable node, a local variable can be accessed within the
 * declaring scope and all of it's nested/child scopes.
 *
 * @author Walied K. Yassen
 */
public final class AstLocalVariable extends AstExpression {

	/**
	 * The name of the variable.
	 */
	@Getter
	private final AstIdentifier name;

	/**
	 * Constructs a new {@link AstLocalVariable} type object instance.
	 *
	 * @param range
	 * 		the expression source code range.
	 * @param name
	 * 		the name of the variable.
	 */
	public AstLocalVariable(Range range, AstIdentifier name) {
		super(range);
		this.name = name;
	}
}

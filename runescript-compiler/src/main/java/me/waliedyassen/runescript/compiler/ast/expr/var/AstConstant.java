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
 * Represents a constant node, a constant is temporal "variable" that will be replaced
 * with it's value in the compile-time.
 *
 * @author Walied K. Yassen
 */
public final class AstConstant extends AstExpression {

	/**
	 * The name of the constant.
	 */
	@Getter
	private final AstIdentifier name;

	/**
	 * Constructs a new {@link AstConstant} type object instance.
	 *
	 * @param range
	 * 		the expression source code range.
	 * @param name
	 * 		the name of the constant.
	 */
	public AstConstant(Range range, AstIdentifier name) {
		super(range);
		this.name = name;
	}
}

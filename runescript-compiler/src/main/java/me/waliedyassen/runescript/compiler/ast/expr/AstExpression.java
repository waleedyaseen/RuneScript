/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.expr;

import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.AstNode;

/**
 * Represents an expression node, all the language expressions must be subclasses of this class.
 * 
 * @author Walied K. Yassen
 */
public abstract class AstExpression extends AstNode {

	/**
	 * Constructs a new {@link AstExpression} type object instance.
	 * 
	 * @param range
	 *              the expression source code range.
	 */
	public AstExpression(Range range) {
		super(range);
	}
}

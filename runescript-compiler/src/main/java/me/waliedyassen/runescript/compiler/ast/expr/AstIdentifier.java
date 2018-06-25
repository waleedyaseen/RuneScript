/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.expr;

import me.waliedyassen.runescript.commons.document.Range;

/**
 * @author Walied K. Yassen
 */
public class AstIdentifier extends AstExpression {

	/**
	 * The identifier text content.
	 */
	private final String text;

	/**
	 * Constructs a new {@link AstIdentifier} type object instance.
	 * 
	 * @param range
	 *              the identifier source code range.
	 * @param text
	 *              the identifier text content.
	 */
	public AstIdentifier(Range range, String text) {
		super(range);
		this.text = text;
	}

	/**
	 * GEts the identifier text content.
	 * 
	 * @return the identifier text content.
	 */
	public String getText() {
		return text;
	}
}

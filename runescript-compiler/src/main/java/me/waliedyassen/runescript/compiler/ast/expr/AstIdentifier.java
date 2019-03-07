/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.expr;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;

/**
 * Represents an identifier node, an identifier is any word within the document
 * that is not a keyword.
 * 
 * @author Walied K. Yassen
 */
public final class AstIdentifier extends AstExpression {

	/**
	 * The identifier text content.
	 */
	@Getter
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
}

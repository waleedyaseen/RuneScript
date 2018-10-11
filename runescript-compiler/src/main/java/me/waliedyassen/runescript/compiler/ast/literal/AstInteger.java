/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.literal;

import me.waliedyassen.runescript.commons.document.Range;

/**
 * Represents an integer literal expression node.
 * 
 * @author Walied K. Yassen
 */
public final class AstInteger extends AstNumber {

	/**
	 * The integer value.
	 */
	private final int value;

	/**
	 * Constructs a new {@link AstInteger} type object instance.
	 * 
	 * @param range
	 *              the node source code range.
	 * @param value
	 *              the integer value.
	 */
	public AstInteger(Range range, int value) {
		super(range);
		this.value = value;
	}

	/**
	 * Gets the integer value.
	 * 
	 * @return the integer value.
	 */
	public int getValue() {
		return value;
	}
}

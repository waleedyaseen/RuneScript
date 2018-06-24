/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast;

import me.waliedyassen.runescript.commons.document.Range;

/**
 * Represents the smallest unit in the Abstract Syntax Tree (AST).
 * 
 * @author Walied K. Yassen
 */
public abstract class AstNode {

	/**
	 * The node source code range.
	 */
	private final Range range;

	/**
	 * Constructs a new {@link AstNode} type object instance.
	 * 
	 * @param range
	 *              the node source code range.
	 */
	public AstNode(Range range) {
		this.range = range;
	}

	/**
	 * Gets the node source code range
	 * 
	 * @return the {@link Range} object of this node.
	 */
	public Range getRange() {
		return range;
	}
}

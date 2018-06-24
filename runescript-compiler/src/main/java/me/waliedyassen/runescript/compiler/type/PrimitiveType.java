/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.type;

import me.waliedyassen.runescript.compiler.stack.StackType;

/**
 * Represents the primitive types within our type system.
 * 
 * @author Walied K. Yassen
 */
public enum PrimitiveType implements Type {
	/**
	 * The integer primitive type.
	 */
	INT("int", StackType.INT),

	/**
	 * The string primitive type.
	 */
	STRING("string", StackType.STRING),

	/**
	 * The long primitive type.
	 */
	LONG("long", StackType.LONG),

	/**
	 * The boolean primitive type.
	 */
	BOOL("bool", StackType.INT);

	/**
	 * The type name.
	 */
	private final String name;

	/**
	 * The type stack type.
	 */
	private final StackType stackType;

	/**
	 * Constructs a new {@link PrimitiveType} enum constant.
	 * 
	 * @param name
	 *                  the type name.
	 * @param stackType
	 *                  the type stack type.
	 */
	private PrimitiveType(String name, StackType stackType) {
		this.name = name;
		this.stackType = stackType;
	}

	/*
	 * (non-Javadoc)
	 * @see me.waliedyassen.runescript.compiler.type.Type#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see me.waliedyassen.runescript.compiler.type.Type#getStackType()
	 */
	@Override
	public StackType getStackType() {
		return stackType;
	}
}
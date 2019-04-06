/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.stack.StackType;

/**
 * Represents the primitive types within our type system.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public enum PrimitiveType implements Type {

	/**
	 * The void primitive type.
	 */
	VOID("void", null),

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
	 * The name of the type, used as keywords for the compiler
	 * parser.
	 */
	@Getter
	private final String name;

	/**
	 * The stack which this type belongs to or encodes to.
	 */
	@Getter
	private final StackType stackType;
}
/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a RuneScript operator type.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public enum Operator {

	/**
	 * The equals operator type.
	 */
	EQUAL(0, "=", Kind.EQUALS, Associativity.LEFT),

	/**
	 * The not equals operator type.
	 */
	NOT_EQUAL(0, "!", Kind.NOT_EQUALS, Associativity.LEFT),

	/**
	 * The less than operator type.
	 */
	LESS_THAN(1, "<", Kind.LESS_THAN, Associativity.NONE),

	/**
	 * The greater than operator type.
	 */
	GREATER_THAN(1, ">", Kind.GREATER_THAN, Associativity.NONE),

	/**
	 * The less than or equals operator type.
	 */
	LESS_THAN_OR_EQUALS(1, "<=", Kind.LESS_THAN_OR_EQUAL, Associativity.NONE),

	/**
	 * The greater than or equals operator type.
	 */
	GREATER_THAN_OR_EQUALS(1, ">=", Kind.GREATER_THAN_OR_EQUAL, Associativity.NONE);

	/**
	 * The operators look-up map.
	 */
	private static final Map<Kind, Operator> lookupMap = Arrays.stream(values()).collect(Collectors.toMap(Operator::getKind, operator -> operator));

	/**
	 * The operator precedence.
	 */
	@Getter
	private final int precedence;

	/**
	 * The representation of the operator.
	 */
	@Getter
	private final String representation;

	/**
	 * The token kind of the operator.
	 */
	@Getter
	private final Kind kind;

	/**
	 * The associativity of the operator.
	 */
	@Getter
	private final Associativity associativity;

	/**
	 * Looks up for the {@link Operator} constant of the specified {@link Kind}.
	 *
	 * @param kind
	 * 		the token kind to look for it's associated operator.
	 *
	 * @return the {@link Operator} of that token kind if it is present otherwise <code>null</code>.
	 */
	public static Operator lookup(Kind kind) {
		return lookupMap.get(kind);
	}

	/**
	 * Represents the operator associativity.
	 *
	 * @author Walied K. Yassen
	 */
	public enum Associativity {

		/**
		 * The operator is not associative, and is not allowed to be chained.
		 */
		NONE,

		/**
		 * The operator is left associative.
		 */
		LEFT,

		/**
		 * The operator is right associative.
		 */
		RIGHT
	}
}

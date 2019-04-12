/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
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
     * The logical OR operator type.
     */
    LOGICAL_OR(2, "|", Kind.LOGICAL_OR, Associativity.LEFT),

    /**
     * The logical AND operator type.
     */
    LOGICAL_AND(3, "&", Kind.LOGICAL_AND, Associativity.LEFT),

    /**
     * The equals operator type.
     */
    EQUAL(8, "=", Kind.EQUALS, Associativity.LEFT),

    /**
     * The not equals operator type.
     */
    NOT_EQUAL(8, "!", Kind.NOT_EQUALS, Associativity.LEFT),

    /**
     * The less than operator type.
     */
    LESS_THAN(9, "<", Kind.LESS_THAN, Associativity.LEFT),

    /**
     * The greater than operator type.
     */
    GREATER_THAN(9, ">", Kind.GREATER_THAN, Associativity.LEFT),

    /**
     * The less than or equals operator type.
     */
    LESS_THAN_OR_EQUALS(9, "<=", Kind.LESS_THAN_OR_EQUAL, Associativity.LEFT),

    /**
     * The greater than or equals operator type.
     */
    GREATER_THAN_OR_EQUALS(9, ">=", Kind.GREATER_THAN_OR_EQUAL, Associativity.LEFT);

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
     * Checks whether or not this operator is an equality operator.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public boolean isEquality() {
        switch (this) {
            case EQUAL:
            case NOT_EQUAL:
                return true;
            default:
                return false;
        }
    }

    /**
     * Checks whether or not this operator is relational operator.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public boolean isRelational() {
        switch (this) {
            case LESS_THAN:
            case LESS_THAN_OR_EQUALS:
            case GREATER_THAN:
            case GREATER_THAN_OR_EQUALS:
                return true;
            default:
                return false;
        }
    }

    /**
     * Checks whether or not this operator is a logical operator.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public boolean isLogical() {
        switch (this) {
            case LOGICAL_AND:
            case LOGICAL_OR:
                return true;
            default:
                return false;
        }
    }

    /**
     * Looks up for the {@link Operator} constant of the specified {@link Kind}.
     *
     * @param kind
     *         the token kind to look for it's associated operator.
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
         * The operator is left associative.
         */
        LEFT,

        /**
         * The operator is right associative.
         */
        RIGHT
    }
}

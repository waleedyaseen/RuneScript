/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents the primitive types within our type system.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public enum PrimitiveType implements Type {

    /**
     * The undefined primitive type.
     */
    UNDEFINED('\0', null, null, null),

    /**
     * The void primitive type.
     */
    VOID('\0', "void", null, null),

    /**
     * The integer primitive type.
     */
    INT('i', "int", StackType.INT, 0),

    /**
     * The string primitive type.
     */
    STRING('s', "string", StackType.STRING, ""),

    /**
     * The long primitive type.
     */
    LONG('\u00cf', "long", StackType.LONG, 0L),

    /**
     * The boolean primitive type.
     */
    BOOL('1', "bool", StackType.INT, false);

    /**
     * The {@link PrimitiveType} by {@link #representation} look-up map.
     */
    private static Map<String, PrimitiveType> lookupMap = Arrays.stream(values()).collect(Collectors.toMap(PrimitiveType::getRepresentation, type -> type));

    /**
     * The code of this primitive type.
     */
    @Getter
    private final char code;

    /**
     * The primitive type textual representation.
     */
    @Getter
    private final String representation;

    /**
     * The stack which this type belongs or encodes to.
     */
    @Getter
    private final StackType stackType;

    /**
     * The default vlaue of this type.
     */
    @Getter
    private final Object defaultValue;

    /**
     * Checks whether or not this {@link PrimitiveType type} is a declarable type. Which means that it can be used as
     * parameters, or local variable declarations.
     *
     * @return {@code true} if it is otherwise {@code false}.
     */
    public boolean isDeclarable() {
        return stackType != null;
    }

    /**
     * Looks-up for the {@link PrimitiveType} with the textual representation.
     *
     * @param representation
     *         the textual representation of the {@link PrimitiveType}.
     *
     * @return the {@link PrimitiveType} if found otherwise {@code null}.
     */
    public static PrimitiveType forRepresentation(String representation) {
        return lookupMap.get(representation);
    }
}
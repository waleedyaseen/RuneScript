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
    UNDEFINED('\ufff0', "undefined", null, null),

    /**
     * The hook primitive type.
     */
    HOOK('\ufff1', "hook", null, null),

    /**
     * The void primitive type.
     */
    VOID('\ufff2', "void", null, null),

    /**
     * The integer primitive type.
     */
    INT('i', "int", StackType.INT, 0),

    /**
     * The string primitive type.
     */
    STRING('s', "string", StackType.STRING, ""),

    /**
     * The seq primitive type.
     */
    SEQ('A', "seq", StackType.INT, -1),

    /**
     * The stat primitive type.
     */
    STAT('S', "stat", StackType.INT, -1),

    /**
     * The synth primitive type.
     */
    SYNTH('P', "synth", StackType.INT, -1),

    /**
     * The coordgrid primitive type.
     */
    COORDGRID('c', "coordgrid", StackType.INT, -1),

    /**
     * The char primitive type.
     */
    CHAR('z', "char", StackType.INT, -1),

    /**
     * The fontmetrics primitive type.
     */
    FONTMETRICS('f', "fontmetrics", StackType.INT, -1),

    /**
     * The maparea primitive type.
     */
    MAPAREA('`', "maparea", StackType.INT, -1),

    /**
     * The enum primitive type.
     */
    ENUM('g', "enum", StackType.INT, -1),

    /**
     * The npc primitive type.
     */
    NPC('n', "npc", StackType.INT, -1),

    /**
     * The model primitive type.
     */
    MODEL('m', "model", StackType.INT, -1),

    /**
     * The interface primitive type.
     */
    INTERFACE('a', "interface", StackType.INT, -1),

    /**
     * The component primitive type.
     */
    COMPONENT('I', "component", StackType.INT, -1),

    /**
     * The long primitive type.
     */
    LONG('\u00cf', "long", StackType.LONG, 0L),

    /**
     * The boolean primitive type.
     */
    BOOLEAN('1', "boolean", StackType.INT, false),

    /**
     * The category primitive type.
     */
    CATEGORY('y', "category", StackType.INT, -1),

    /**
     * The object primitive type.
     */
    OBJ('o', "obj", StackType.INT, -1),

    /**
     * The inv primitive type.
     */
    INV('v', "inv", StackType.INT, -1),

    /**
     * The mapelement primitive type.
     */
    MAPELEMENT('µ', "mapelement", StackType.INT, -1),

    /**
     * The graphic primitive type.
     */
    GRAPHIC('d', "graphic", StackType.INT, -1),

    /**
     * The var primitive type.
     */
    // TODO: Verify the char code is correct
    VAR('2', "var", StackType.INT, -1);



    /**
     * The {@link PrimitiveType} by {@link #representation} look-up map.
     */
    private static final Map<String, PrimitiveType> lookupMap = Arrays.stream(values()).filter(PrimitiveType::isReferencable).collect(Collectors.toMap(PrimitiveType::getRepresentation, type -> type));

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
     * Checks whether er or not the primitive type is a referencable type.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public boolean isReferencable() {
        return representation != null && this != HOOK && this != UNDEFINED;
    }

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
     * @param representation the textual representation of the {@link PrimitiveType}.
     * @return the {@link PrimitiveType} if found otherwise {@code null}.
     */
    public static PrimitiveType forRepresentation(String representation) {
        return lookupMap.get(representation);
    }

    /**
     * Checks whether or not the type can be used as an array.
     *
     * @return <code>true</code> if it does otherwise <code>false</code>.
     */
    public boolean isArrayable() {
        if (this == PrimitiveType.BOOLEAN) {
            return false;
        }
        return stackType == StackType.INT;
    }

    /**
     * Checks whether or not the primitive type belongs to config type.
     *
     * @return <code>true</code> if it does otherwise <code>false</code>.
     */
    public boolean isConfigType() {
        return isDeclarable() && this != INT && this != BOOLEAN && this != LONG && this != STRING;
    }
}
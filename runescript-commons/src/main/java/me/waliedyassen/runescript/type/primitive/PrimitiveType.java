/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.type.primitive;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.type.Type;
import me.waliedyassen.runescript.type.serializer.TypeSerializer;
import me.waliedyassen.runescript.type.stack.StackType;

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
    UNDEFINED('\ufff0', "undefined", null, null, null),

    /**
     * The hook primitive type.
     */
    HOOK('\ufff1', "hook", null, null, null),

    /**
     * The void primitive type.
     */
    VOID('\ufff2', "void", null, null, null),

    /**
     * The byte special type.
     */
    BYTE('\ufff3', "byte", null, null, TypeSerializer.BYTE),

    /**
     * The short special type.
     */
    SHORT('\ufff4', "short", null, null, TypeSerializer.SHORT),

    /**
     * The tribyte special type.
     */
    TRIBYTE('\ufff5', "tribyte", null, null, TypeSerializer.TRIBYTE),

    /**
     * The type special type.
     */
    TYPE('\ufff6', "type", null, null, TypeSerializer.TYPE),

    /**
     * The param configuration type.
     */
    PARAM('\uffd0', "param", null, null, null),

    /**
     * The floor overlay configuration type.
     */
    FLO('\uffd1', "flo", null, null, null),

    /**
     * The floor underlay configuration type.
     */
    FLU('\uffd2', "flu", null, null, null),

    /**
     * The var configuration type.
     */
    VAR('\uffd3', "var", null, null, null),
    /**
     * The var configuration type.
     */
    VARBIT('\uffd4', "varbit", null, null, null),

    /**
     * The var client string configuration type.
     */
    VARCSTR('\uffd5', "varcstr", null, null, null),

    /**
     * The var client integer configuration type.
     */
    VARCINT('\uffd6', "varcint", null, null, null),

    /**
     * The null special type.
     */
    NULL('\uffd7', null, null, null, null),

    // All the types below are verified to be part of script var type.

    /**
     * The integer primitive type.
     */
    INT('i', "int", StackType.INT, 0, TypeSerializer.INT),

    /**
     * The string primitive type.
     */
    STRING('s', "string", StackType.STRING, "", TypeSerializer.STRING),

    /**
     * The spotanim primitive type.
     */
    SPOTANIM('t', "spotanim", StackType.INT, -1, TypeSerializer.INT),

    /**
     * The seq primitive type.
     */
    SEQ('A', "seq", StackType.INT, -1, TypeSerializer.SHORT),

    /**
     * The stat primitive type.
     */
    STAT('S', "stat", StackType.INT, -1, TypeSerializer.SHORT),

    /**
     * The synth primitive type.
     */
    SYNTH('P', "synth", StackType.INT, -1, TypeSerializer.SHORT),

    /**
     * The coordgrid primitive type.
     */
    COORDGRID('c', "coordgrid", StackType.INT, -1, TypeSerializer.INT),

    /**
     * The char primitive type.
     */
    CHAR('z', "char", StackType.INT, -1, TypeSerializer.BYTE),

    /**
     * The fontmetrics primitive type.
     */
    FONTMETRICS('f', "fontmetrics", StackType.INT, -1, TypeSerializer.INT),

    /**
     * The maparea primitive type.
     */
    MAPAREA('`', "maparea", StackType.INT, -1, TypeSerializer.INT),

    /**
     * The enum primitive type.
     */
    ENUM('g', "enum", StackType.INT, -1, TypeSerializer.SHORT),

    /**
     * The npc primitive type.
     */
    NPC('n', "npc", StackType.INT, -1, TypeSerializer.SHORT),

    /**
     * The model primitive type.
     */
    MODEL('m', "model", StackType.INT, -1, TypeSerializer.SHORT),

    /**
     * The top level interface primitive type.
     */
    TOPLEVELINTERFACE('F', "toplevelinterface", StackType.INT, -1, TypeSerializer.SHORT),

    /**
     * The overlay interface primitive type.
     */
    OVERLAYINTERFACE('L', "overlayinterface", StackType.INT, -1, TypeSerializer.SHORT),

    /**
     * The client interface primitive type.
     */
    CLIENTINTERFACE('\u00a9', "clientinterface", StackType.INT, -1, TypeSerializer.SHORT),

    /**
     * The interface primitive type.
     */
    INTERFACE('a', "interface", StackType.INT, -1, TypeSerializer.SHORT),

    /**
     * The component primitive type.
     */
    COMPONENT('I', "component", StackType.INT, -1, TypeSerializer.INT),

    /**
     * The long primitive type.
     */
    LONG('\u00cf', "long", StackType.LONG, 0L, TypeSerializer.LONG),

    /**
     * The boolean primitive type.
     */
    BOOLEAN('1', "boolean", StackType.INT, false, TypeSerializer.BOOLEAN),

    /**
     * The category primitive type.
     */
    CATEGORY('y', "category", StackType.INT, -1, TypeSerializer.SHORT),

    /**
     * The namedobj primitive type.
     */
    NAMEDOBJ('O', "namedobj", StackType.INT, -1, TypeSerializer.SHORT),

    /**
     * The object primitive type.
     */
    OBJ('o', "obj", StackType.INT, -1, TypeSerializer.SHORT),

    /**
     * The inv primitive type.
     */
    INV('v', "inv", StackType.INT, -1, TypeSerializer.SHORT),

    /**
     * The texture primitive type.
     */
    TEXTURE('x', "texture", StackType.INT, -1, TypeSerializer.BYTE),

    /**
     * The mapelement primitive type.
     */
    MAPELEMENT('\u00B5', "mapelement", StackType.INT, -1, TypeSerializer.SHORT),

    /**
     * The graphic primitive type.
     */
    GRAPHIC('d', "graphic", StackType.INT, -1, TypeSerializer.SHORT),

    /**
     * The struct primitive type.
     */
    STRUCT('J', "struct", StackType.INT, -1, TypeSerializer.SHORT),

    /**
     * The loc primitive type.
     */
    LOC('l', "loc", StackType.INT, -1, TypeSerializer.SHORT),

    /**
     * The colour primitive type.
     */
    COLOUR('C', "colour", StackType.INT, -1, TypeSerializer.SHORT),

    /**
     * The identity kit primitive type.
     */
    IDK('K', "idkit", StackType.INT, -1, TypeSerializer.SHORT),

    /**
     * The chat phrase primitive type.
     */
    CHATPHRASE('e', "chatphrase", StackType.INT, -1, TypeSerializer.SHORT),

    /**
     * The BAS primitive type.
     */
    BAS('\u20ac', "bas", StackType.INT, -1, TypeSerializer.SHORT);

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
     * The default value of this type.
     */
    @Getter
    private final Object defaultValue;

    /**
     * The serializer the values of this type will use.
     */
    @Getter
    @SuppressWarnings("rawtypes")
    private final TypeSerializer serializer;

    /**
     * Casts this type and the specified other type to a highest type then compares if they are equals.
     *
     * @param other the other type that we want to compare.
     * @return <code>true</code> if they are equals otherwise <code>false</code>.
     */
    public boolean implicitEquals(PrimitiveType other) {
        return implicitCastToHigher(this) == implicitCastToHigher(other);
    }

    /**
     * Casts the specified {@link PrimitiveType type} to a higher type.
     *
     * @param type the type that we want to cast to a higher type.
     * @return the casted {@link PrimitiveType type} or the {@link PrimitiveType type} that was passed.
     */
    private PrimitiveType implicitCastToHigher(PrimitiveType type) {
        switch (type) {
            case BYTE:
            case SHORT:
            case TRIBYTE:
                return INT;
            default:
                return type;
        }
    }

    /**
     * Checks whether er or not the primitive type is a referencable type.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public boolean isReferencable() {
        switch (this) {
            case HOOK:
            case UNDEFINED:
            case BYTE:
            case SHORT:
            case TRIBYTE:
            case TYPE:
                return false;
            default:
                return representation != null;
        }
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
        switch (this) {
            case SEQ:
            case STAT:
            case MAPAREA:
            case ENUM:
            case NPC:
            case CATEGORY:
            case NAMEDOBJ:
            case OBJ:
            case INV:
            case MAPELEMENT:
            case VAR:
            case VARBIT:
            case VARCINT:
            case VARCSTR:
            case STRUCT:
            case LOC:
            case PARAM:
            case FLO:
            case FLU:
            case SPOTANIM:
                return true;
            default:
                return false;
        }
    }


    /**
     * Checks whether or not this type is nullable.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public boolean isNullable() {
        if (stackType != StackType.INT) {
            return false;
        }
        switch (this) {
            case NULL:
            case PARAM:
                return false;
            default:
                return true;
        }
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
     * Returns the {@link PrimitiveType} with the specified {@code code}.
     *
     * @param code the code that we want it's associated primitive type.
     * @return the {@link PrimitiveType} if found otherwise {@code null}.
     */
    public static PrimitiveType forCode(char code) {
        for (var type : PrimitiveType.values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
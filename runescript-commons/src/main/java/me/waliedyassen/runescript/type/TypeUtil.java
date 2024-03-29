/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.type;

import me.waliedyassen.runescript.type.primitive.PrimitiveType;
import me.waliedyassen.runescript.type.tuple.TupleType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Contains all of the necessary utilities for our semantic analysis.
 *
 * @author Walied K. Yassen
 */
public final class TypeUtil {

    /**
     * Creates a textual representation of the given {@link Type types} then join them all together into one {@link
     * String} using a comma.
     *
     * @param types the types to create the textual representation for.
     * @return the textual representation of the specified types.
     */
    public static String createRepresentation(Type... types) {
        return Arrays.stream(types).map(Type::getRepresentation).collect(Collectors.joining(","));
    }

    /**
     * Flattens the given {@link Type types} into one combined array. It takes each type and turns it into an array if
     * it contains more than one type such as {@link TupleType} then combine all of those types into one array.
     *
     * @param types the types to flatten.
     * @return the flattened array of {@link Type} objects.
     */
    public static Type[] flatten(Type[] types) {
        var flattened = new ArrayList<Type>(types.length);
        for (var type : types) {
            if (type instanceof TupleType) {
                for (var child : ((TupleType) type).getFlattened()) {
                    flattened.add(child);
                }
            } else {
                flattened.add(type);
            }
        }
        return flattened.toArray(new Type[0]);
    }

    /**
     * Checks whether ror not the specified type can be considered as "void". A void type is a type that doesn't
     * hold any kind of data.
     *
     * @param type the type to check if it is void.
     * @return <code>true</code> if it is otherise <code>false</code>.
     */
    public static boolean isVoid(Type type) {
        if (type == null || type == PrimitiveType.VOID.INSTANCE || type == TupleType.EMPTY) {
            return true;
        } else if (type instanceof TupleType) {
            return ((TupleType) type).getChilds().length == 0;
        }
        return false;
    }

    private TypeUtil() {
        // NOOP
    }
}

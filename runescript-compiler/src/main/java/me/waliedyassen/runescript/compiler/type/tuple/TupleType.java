/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.type.tuple;

import lombok.Getter;
import me.waliedyassen.runescript.compiler.stack.StackType;
import me.waliedyassen.runescript.compiler.type.Type;
import me.waliedyassen.runescript.compiler.type.primitive.PrimitiveType;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Represents a tuple type which is a combined type, it combines multiple type to be represented as a single type while
 * still providing access to those combined types.
 *
 * @author Walied K. Yassen
 */
public final class TupleType implements Type {

    /**
     * The child types of this tuple.
     */
    @Getter
    private final PrimitiveType[] childs;


    /**
     * Constructs a new {@link TupleType} type object instance.
     *
     * @param childs
     *         the tuple child types.
     */
    public TupleType(PrimitiveType[] childs) {
        this.childs = childs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRepresentation() {
        return Arrays.stream(childs).map(PrimitiveType::getRepresentation).collect(Collectors.joining(","));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StackType getStackType() {
        return null;
    }
}

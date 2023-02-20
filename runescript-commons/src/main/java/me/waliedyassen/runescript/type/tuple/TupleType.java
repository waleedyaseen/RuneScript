/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.type.tuple;

import lombok.Getter;
import lombok.ToString;
import me.waliedyassen.runescript.type.Type;
import me.waliedyassen.runescript.type.TypeUtil;
import me.waliedyassen.runescript.type.primitive.PrimitiveType;
import me.waliedyassen.runescript.type.stack.StackType;

import java.util.Arrays;

/**
 * Represents a tuple type which is a combined type, it combines multiple type to be represented as a single type while
 * still providing access to those combined types.
 *
 * @author Walied K. Yassen
 */
@ToString
public final class TupleType implements Type {

    /**
     * An {@link TupleType} object with no child types.
     */
    public static final TupleType EMPTY = new TupleType();

    /**
     * The child types of this tuple.
     */
    @Getter
    private final Type[] childs;

    /**
     * The expanded child types of this tuple.
     */
    @Getter
    private final Type[] flattened;

    /**
     * Constructs a new {@link TupleType} type object instance.
     *
     * @param childs the tuple child types.
     */
    public TupleType(Type... childs) {
        this.childs = childs;
        flattened = TypeUtil.flatten(childs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TupleType) {
            return Arrays.equals(flattened, ((TupleType) obj).flattened);
        } else if (flattened.length == 1) {
            return obj == flattened[0];
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRepresentation() {
        return TypeUtil.createRepresentation(flattened);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StackType getStackType() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getDefaultValue() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public char getCode() {
        throw new UnsupportedOperationException();
    }
}

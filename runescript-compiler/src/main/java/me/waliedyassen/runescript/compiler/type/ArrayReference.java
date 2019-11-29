/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.type;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.type.PrimitiveType;
import me.waliedyassen.runescript.type.StackType;
import me.waliedyassen.runescript.type.Type;

/**
 * Represents a cross-script array reference type.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
@EqualsAndHashCode
public final class ArrayReference implements Type {

    /**
     * The type of the array.
     */
    @Getter
    private final PrimitiveType type;

    /**
     * The index of the array.
     */
    @Getter
    private final int index;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%sarray(%d)", type.getRepresentation(), index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRepresentation() {
        // return type.getRepresentation() + "array";
        // This will never be called at parser time.
        return toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public StackType getStackType() {
        return StackType.INT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getDefaultValue() {
        return 0;
    }
}

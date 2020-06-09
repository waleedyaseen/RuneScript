/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package me.waliedyassen.runescript.config.codegen.operand;

import java.nio.ByteBuffer;

/**
 * An operand type which is responsible for writing and providing instances for a specific {@link T type}.
 *
 * @param <T> the class type this operand is responsible for handling.
 * @author Walied K. Yassen
 */
public interface Operand<T> {

    /**
     * Creates a new instance of the type this operand represents.
     *
     * @param primitives the primitive values that were specified.
     * @return the created {@link T} object instance.
     */
    T newInstance(Object[] primitives);

    /**
     * Writes the specified {@code value} of type {@code T} into the specified {@link ByteBuffer buffer}.
     *
     * @param buffer the buffer to write the operand value into.
     */
    void write(ByteBuffer buffer, T value);

    /**
     * Calculates the amount of bytes the specified {@code value} takes when it is serialised to a {@link ByteBuffer}
     * object.
     *
     * @param value he value to calculate the amount for.
     * @return the calculated amount of bytes.
     */
    int size(T value);
}

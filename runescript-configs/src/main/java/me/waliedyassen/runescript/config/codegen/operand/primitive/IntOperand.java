/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.codegen.operand.primitive;

import me.waliedyassen.runescript.config.codegen.operand.Operand;

import java.nio.ByteBuffer;

/**
 * An {@link Operand} implementation for the {@link Integer} type.
 *
 * @author Walied K. Yassen
 */
public final class IntOperand implements Operand<Integer> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer newInstance(Object[] primitives) {
        return (Integer) primitives[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(ByteBuffer buffer, Integer value) {
        buffer.putInt(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size(Integer value) {
        return 4;
    }
}

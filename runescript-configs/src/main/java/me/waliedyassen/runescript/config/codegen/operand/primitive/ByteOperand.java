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
 * An {@link Operand} implementation for the {@link Byte} type.
 *
 * @author Walied K. Yassen
 */
public final class ByteOperand implements Operand<Byte> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte newInstance(Object[] primitives) {
        return (Byte) primitives[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(ByteBuffer buffer, java.lang.Byte value) {
        buffer.put(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size(java.lang.Byte value) {
        return 1;
    }
}

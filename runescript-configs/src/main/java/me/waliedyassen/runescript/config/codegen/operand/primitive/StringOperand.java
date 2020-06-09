/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.codegen.operand.primitive;

import lombok.var;
import me.waliedyassen.runescript.config.codegen.operand.Operand;

import java.nio.ByteBuffer;

/**
 * An {@link Operand} implementation for the {@link String} type.
 *
 * @author Walied K. Yassen
 */
public final class StringOperand implements Operand<String> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String newInstance(Object[] primitives) {
        return (String) primitives[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(ByteBuffer buffer, String value) {
        var length = value.length();
        for (var index = 0; index < length; index++) {
            buffer.put((byte) value.charAt(index));
        }
        buffer.put((byte) 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size(String value) {
        return value.length() + 1;
    }
}

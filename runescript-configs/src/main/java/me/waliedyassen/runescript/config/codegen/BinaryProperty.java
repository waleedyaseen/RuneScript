/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.codegen;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.type.PrimitiveType;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A serializable binary property fora configuration entry.
 *
 * @author Walied K. Yassen
 */
@Getter
@RequiredArgsConstructor
public final class BinaryProperty {

    /**
     * The opcode for this binary property.
     */
    private final int code;

    /**
     * The types of the values of the binary properties.
     */
    private final PrimitiveType[] types;

    /**
     * The values that of the binary property.
     */
    private final Object[] values;

    /**
     * Writes the content of the binary property to the specified {@link DataOutputStream stream}.
     *
     * @param stream
     *         the stream to write the the content data to.
     *
     * @throws IOException
     *         if anything occurs while writing the content data of the binary property to the stream.
     */
    @SuppressWarnings("unchecked")
    public void write(DataOutputStream stream) throws IOException {
        stream.writeByte(code);
        if (values == null) {
            return;
        }
        for (int index = 0; index < types.length; index++) {
            types[index].getSerializer().serialize(values[index], stream);
        }
    }
}

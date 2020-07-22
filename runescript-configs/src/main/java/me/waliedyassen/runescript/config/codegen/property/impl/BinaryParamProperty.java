/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.codegen.property.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.config.codegen.property.BinaryProperty;
import me.waliedyassen.runescript.util.StreamUtil;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A serializable binary property fora configuration entry.
 *
 * @author Walied K. Yassen
 */
@Getter
@RequiredArgsConstructor
public final class BinaryParamProperty implements BinaryProperty {

    /**
     * The types of the values of the binary properties.
     */
    private final Map<Integer, Object> values = new HashMap<>();

    /**
     * The opcode for this binary property.
     */
    private final int code;

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(DataOutputStream stream) throws IOException {
        writeCode(stream);
        stream.writeByte(values.size());
        for (var entry : values.entrySet()) {
            var value = entry.getValue();
            stream.writeByte(value instanceof String ? 1 : 0);
            StreamUtil.writeTriByte(stream, entry.getKey());
            if (value instanceof String) {
                StreamUtil.writeString(stream, (String) value);
            } else {
                stream.writeInt((Integer) value);
            }
        }
    }
}

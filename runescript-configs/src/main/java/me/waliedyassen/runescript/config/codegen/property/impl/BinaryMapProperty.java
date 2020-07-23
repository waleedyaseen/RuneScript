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
import me.waliedyassen.runescript.config.var.ConfigMapProperty;
import me.waliedyassen.runescript.util.StreamUtil;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The binary property implementation of the {@link ConfigMapProperty} type.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class BinaryMapProperty implements BinaryProperty {

    /**
     * A map of all the property values.
     */
    @Getter
    private final Map<Integer, Object> values = new HashMap<>();

    /**
     * The code of the property.
     */
    @Getter
    private final int code;

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(DataOutputStream stream) throws IOException {
        writeCode(stream);
        stream.writeShort(values.size());
        for (var entry : values.entrySet()) {
            stream.writeInt(entry.getKey());
            var value = entry.getValue();
            if (value instanceof String) {
                StreamUtil.writeString(stream, (String) value);
            } else {
                stream.writeInt((Integer) value);
            }
        }
    }
}

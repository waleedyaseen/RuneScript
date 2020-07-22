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
import lombok.SneakyThrows;
import lombok.var;
import me.waliedyassen.runescript.config.ConfigGroup;
import me.waliedyassen.runescript.config.codegen.property.impl.BinaryBasicProperty;
import me.waliedyassen.runescript.config.codegen.property.BinaryProperty;
import me.waliedyassen.runescript.type.PrimitiveType;
import me.waliedyassen.runescript.type.Type;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A binary configuration.
 *
 * @author Walied K. YUassen
 */
@RequiredArgsConstructor
public final class BinaryConfig {

    /**
     * The NULL property used to indicate the end of a serialization stream.
     */
    private static final BinaryProperty NULL = new BinaryBasicProperty(0, new PrimitiveType[0], new Object[0]);

    /**
     * The codes of the configuration.
     */
    @Getter
    private final List<BinaryProperty> properties = new ArrayList<>();

    /**
     * The group of the configuration.
     */
    @Getter
    private final ConfigGroup group;

    /**
     * The name of the configuration.
     */
    @Getter
    private final String name;

    /**
     * The content type  of the configuration.
     */
    @Getter
    private final Type contentType;

    /**
     * Serializes this {@link BinaryConfig configuration} object into an array of {@code byte}.
     *
     * @return the serialized array of bytes that contains this binary configuration object data.
     */
    @SneakyThrows
    public byte[] serialize() {
        try (var bos = new ByteArrayOutputStream()) {
            try (var dos = new DataOutputStream(bos)) {
                for (var property : properties) {
                    property.write(dos);
                }
                NULL.write(dos);
            }
            return bos.toByteArray();
        }
    }

    /**
     * Adds a new property to the configuration.
     *
     * @param property
     *         the property that we want to add.
     */
    public void addProperty(BinaryProperty property) {
        properties.add(property);
    }

    /**
     * Returns the {@link BinaryProperty} with the specified {@code code}.
     *
     * @param code
     *         the code of the property that we want to find.
     *
     * @return the {@link BinaryProperty} object if it was found otherwise {@code null}.
     */
    public BinaryProperty findProperty(int code) {
        for (var property : properties) {
            if (property.getCode() == code) {
                return property;
            }
        }
        return null;
    }
}

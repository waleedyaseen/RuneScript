/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.type;

import java.util.HashMap;
import java.util.Map;

/**
 * A registry of all the types for configuration.
 *
 * @author Walied K. Yassen
 */
public final class TypeRegistry {

    /**
     * A map of all the registered types.
     */
    private final Map<Class<?>, ConfigVarType> types = new HashMap<>();

    /**
     * Registers the specified {@link ConfigVarType type} in the registry.
     *
     * @param type
     *         the type that we want to register.
     */
    public void register(ConfigVarType type) {
        if (types.containsKey(type.getNativeType())) {
            throw new IllegalArgumentException("You cannot register a native type twice: " + type + " and " + types.get(type.getNativeType()));
        }
        types.put(type.getNativeType(), type);
    }

    /**
     * Looks-up for the {@link ConfigVarType} for the specified {@link Class<?> nativeType}.
     *
     * @param nativeType
     *         the native type to register in the registry.
     *
     * @return the {@link ConfigVarType} object if it was present otherwise {@code null}.
     */
    public ConfigVarType lookup(Class<?> nativeType) {
        return types.get(nativeType);
    }
}

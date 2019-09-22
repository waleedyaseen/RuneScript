/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.var;

import lombok.Getter;
import me.waliedyassen.runescript.type.PrimitiveType;

/**
 * Represents a configuration variable type in the RuneScript type system.
 *
 * @author Walied K. Yassen
 */
public enum ConfigVarType {
    /**
     * The integer configuration variable type.
     */
    INT(Integer.class, PrimitiveType.INT),

    /**
     * The string configuration variable type.
     */
    STRING(String.class, PrimitiveType.STRING);

    /**
     * The class type which this variable type is allowed in.
     */
    @Getter
    private final Class<?> classType;

    /**
     * The primitive types which make up this configuration variable type.
     */
    @Getter
    private final PrimitiveType[] primitives;

    /**
     * Constructs a new {@link ConfigVarType} type object instance.
     *
     * @param classType
     *         the class type which this variable type is allowed in.
     * @param primitives
     *         the primitive types that make up this configuration variable type.
     */
    ConfigVarType(Class<?> classType, PrimitiveType... primitives) {
        this.classType = classType;
        this.primitives = primitives;
    }
}

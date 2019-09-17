/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.var;

import lombok.Data;

import java.lang.reflect.Field;

/**
 * Represents a configuration variable properties.
 *
 * @author Walied K. Yassen
 */
@Data
public final class ConfigVar {
    /**
     * The reflection field of the configuration variable.
     */
    private final Field field;

    /**
     * The name of the configuration variable.
     */
    private final String name;

    /**
     * The opcode of the configuration variable.
     */
    private final int opcode;

    /**
     * Whether or not the configuration variable is required.
     */
    private final boolean required;

    /**
     * The variable type of this configuration variable.
     */
    private final ConfigVarType type;

    /**
     * The variable array properties.
     */
    private final ConfigVarArray array;
}
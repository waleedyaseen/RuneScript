/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.var;

import lombok.Data;
import me.waliedyassen.runescript.config.var.rule.ConfigRule;
import me.waliedyassen.runescript.type.PrimitiveType;

import java.util.List;

/**
 * Represents a basic property that is not special in any way.
 *
 * @author Walied K. Yassen
 */
@Data
public final class ConfigBasicProperty implements ConfigProperty {

    /**
     * The name of the property.
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
    private final PrimitiveType[] components;

    /**
     * A list of all the rules this configuration variable abides by.
     */
    private final List<ConfigRule>[] rules;
}
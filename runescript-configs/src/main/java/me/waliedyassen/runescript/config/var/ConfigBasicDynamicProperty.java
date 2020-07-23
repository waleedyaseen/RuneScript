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
 * A basic property with dynamic opcode based on the inferred stack type.
 *
 * @author Walied K. Yassen
 */
@Data
public final class ConfigBasicDynamicProperty implements ConfigProperty {

    /**
     * The name of the property.
     */
    private final String name;

    /**
     * The property we are inferring.
     */
    private final String inferring;

    /**
     * The opcodes of the property.
     */
    private final int[] opcodes;

    /**
     * The rules of the property.
     */
    private final List<ConfigRule> rules;

    /**
     * {@inheritDoc}
     */
    @Override
    public PrimitiveType[] getComponents() {
        throw new UnsupportedOperationException();
    }
}



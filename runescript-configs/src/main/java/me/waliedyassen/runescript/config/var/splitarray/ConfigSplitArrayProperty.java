/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.var.splitarray;

import lombok.Data;
import me.waliedyassen.runescript.config.var.ConfigProperty;
import me.waliedyassen.runescript.config.var.rule.ConfigRule;
import me.waliedyassen.runescript.type.PrimitiveType;

import java.util.List;

/**
 * A single component property for a split array property.
 *
 * @author Yalied K. Yassen
 */
@Data
public class ConfigSplitArrayProperty implements ConfigProperty {

    /**
     * The data of the config split array property.
     */
    private final ConfigSplitArrayData data;

    /**
     * The name of the property.
     */
    private final String name;

    /**
     * The type of the property.
     */
    private final PrimitiveType type;

    /**
     * The rules of the property.
     */
    private final List<ConfigRule> rules;

    /**
     * The element id in the split array data.
     */
    private final int elementId;

    /**
     * The component id in the element in the split array data.
     */
    private final int componentId;

    /**
     * {@inheritDoc}
     */
    @Override
    public PrimitiveType[] getComponents() {
        return new PrimitiveType[]{type};
    }
}


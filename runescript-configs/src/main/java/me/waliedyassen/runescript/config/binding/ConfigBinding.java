/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.binding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.var;
import me.waliedyassen.runescript.config.ConfigGroup;
import me.waliedyassen.runescript.config.var.ConfigBasicProperty;
import me.waliedyassen.runescript.config.var.ConfigProperty;
import me.waliedyassen.runescript.config.var.rule.ConfigRule;
import me.waliedyassen.runescript.type.PrimitiveType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a configuration binding for a specific configuration type.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class ConfigBinding {

    /**
     * A map of all the properties that are in this binding.
     */
    @Getter
    private final Map<String, ConfigProperty> properties = new HashMap<>();

    /**
     * The configuration group this binding is for.
     */
    @Getter
    private final ConfigGroup group;

    /**
     * Whether or not this configuration binding allows param property.
     */
    @Getter
    @Setter
    private boolean allowParamProperty;

    /**
     * Whether or not this configuration binding allow transmit property.
     */
    @Getter
    @Setter
    private boolean allowTransmitProperty;

    /**
     * Adds a specific amount of basic properties to the binding./
     *
     * @param nameTemplate
     *         the name template for the properties.
     * @param opcode
     *         the base opcode for the properties.
     * @param required
     *         whether or not the properties are required.
     * @param components
     *         the value components of each of the properties.
     * @param rules
     *         the rules of the property that applies to each value component of the properties.
     */
    public void addBasicProperty(String nameTemplate, int opcode, boolean required, PrimitiveType[] components, List<ConfigRule> rules, int count) {
        for (var index = 1; index <= count; index++) {
            var componentName = String.format(nameTemplate, index);
            addBasicProperty(componentName, opcode, required, components, rules);
        }
    }


    /**
     * Adds a new basic property to the configuration binding.
     *
     * @param name
     *         the name of the property.
     * @param opcode
     *         the opcode of the property.
     * @param required
     *         whether or not the property is required.
     * @param components
     *         the value components of the property.
     * @param rules
     *         the rules of the property that applies to each value.
     */
    public void addBasicProperty(String name, int opcode, boolean required, PrimitiveType[] components, List<ConfigRule> rules) {
        addProperty(name, new ConfigBasicProperty(name, opcode, required, components, rules));
    }

    /**
     * Adds the specified {@link ConfigProperty property} to the binding.
     *
     * @param name
     *         the name of the property to add it under.
     * @param property
     *         the property that we want to add.
     */
    private void addProperty(String name, ConfigProperty property) {
        if (properties.containsKey(name)) {
            throw new IllegalArgumentException("Another property with the same name is already defined in the binding");
        }
        properties.put(name, property);
    }

    /**
     * Returns the {@link ConfigProperty} with the specified {@code name}.
     *
     * @param name
     *         the configuration property with the specified {@code name}.
     *
     * @return the {@link ConfigProperty} if it was found otherwise {@code null}.
     */
    public ConfigProperty findProperty(String name) {
        return properties.get(name);
    }
}

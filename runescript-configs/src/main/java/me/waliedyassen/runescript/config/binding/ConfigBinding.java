/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
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
import me.waliedyassen.runescript.config.var.ConfigBasicDynamicProperty;
import me.waliedyassen.runescript.config.var.ConfigBasicProperty;
import me.waliedyassen.runescript.config.var.ConfigMapProperty;
import me.waliedyassen.runescript.config.var.ConfigProperty;
import me.waliedyassen.runescript.config.var.rule.ConfigRule;
import me.waliedyassen.runescript.config.var.rule.impl.ConfigRangeRule;
import me.waliedyassen.runescript.config.var.rule.impl.ConfigRequireRule;
import me.waliedyassen.runescript.config.var.splitarray.ConfigSplitArrayData;
import me.waliedyassen.runescript.config.var.splitarray.ConfigSplitArrayProperty;
import me.waliedyassen.runescript.type.PrimitiveType;

import java.util.*;

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
     * The name of the content type property.
     */
    @Getter
    @Setter
    private String contentTypeProperty;

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
    public void addBasicProperty(String nameTemplate, int opcode, boolean required, PrimitiveType[] components, List<ConfigRule>[] rules, int count) {
        rules = prepareRules(components, rules);
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
    public void addBasicProperty(String name, int opcode, boolean required, PrimitiveType[] components, List<ConfigRule>[] rules) {
        rules = prepareRules(components, rules);
        addProperty(name, new ConfigBasicProperty(name, opcode, required, components, rules));
    }

    /**
     * Adds a new basic dynamic opcode property to the configuration binding.
     *
     * @param name
     *         the name of the property.
     * @param inferring
     *         the property name we are inferring the type from.
     * @param opcodes
     *         the opcodes of the property for each base stack type.
     */
    public void addBasicDynamicProperty(String name, String inferring, int[] opcodes) {
        var rules = Collections.<ConfigRule>singletonList(new ConfigRequireRule(inferring));
        var property = new ConfigBasicDynamicProperty(name, inferring, opcodes, rules);
        addProperty(name, property);
    }

    /**
     * Adds a new split array property to the configuration binding.
     *
     * @param name
     *         the name of the property.
     * @param opcode
     *         the opcode of the property.
     * @param required
     *         whether or not the property is required.
     * @param componentNames
     *         the component names of the property.
     * @param components
     *         the components of the property.
     * @param rules
     *         the rules of the property.
     * @param sizeType
     *         the size type of the property.
     * @param maxSize
     *         the maximum amount of elements of the property.
     */
    public void addSplitArrayProperty(String name, int opcode, boolean required, String[] componentNames, PrimitiveType[] components, List<ConfigRule>[] rules, PrimitiveType sizeType, int maxSize) {
        rules = prepareRules(components, rules);
        var data = new ConfigSplitArrayData(name, opcode, required, sizeType, componentNames.length, maxSize);
        for (var id = 0; id < maxSize; id++) {
            for (int index = 0; index < componentNames.length; index++) {
                var componentType = components[index];
                var componentName = String.format(componentNames[index], id);
                var specificRules = new ArrayList<>(rules[index]);
                for (int otherIndex = 0; otherIndex < componentNames.length; otherIndex++) {
                    if (otherIndex == index) {
                        continue;
                    }
                    specificRules.add(new ConfigRequireRule(String.format(componentNames[otherIndex], id)));
                }
                addDefaultRangeRule(specificRules, componentType);
                addProperty(componentName, new ConfigSplitArrayProperty(data, componentName, componentType, specificRules, id, index));
            }
        }
    }

    /**
     * Adds a new split array property to the configuration binding.
     *
     * @param name
     *         the name of the property.
     * @param opcodes
     *         the opcodes of the property.
     * @param keyTypeProperty
     *         the key type property name.
     * @param valueTypeProperty
     *         the value type property name.
     */
    public void addMapProperty(String name, int[] opcodes, String keyTypeProperty, String valueTypeProperty) {
        addProperty(name, new ConfigMapProperty(name, opcodes, keyTypeProperty, valueTypeProperty));
    }

    /**
     * Adds the specified {@link ConfigProperty property} to the binding.
     *
     * @param name
     *         the name of the property to add it under.
     * @param property
     *         the property that we want to add.
     */
    public void addProperty(String name, ConfigProperty property) {
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

    /**
     * Prepares the specified array of {@link ConfigRule} objects for the specified {@link PrimitiveType components}.
     *
     * @param components
     *         the array of components to prepare the rules for.
     * @param rules
     *         the array of config rules to prepare.
     *
     * @return the prepared array of config rules.
     */
    private static List<ConfigRule>[] prepareRules(PrimitiveType[] components, List<ConfigRule>[] rules) {
        if (rules.length > components.length) {
            throw new IllegalArgumentException("There are more rule sections than components");
        }
        if (rules.length != components.length) {
            rules = Arrays.copyOf(rules, components.length);
        }
        for (var index = 0; index < components.length; index++) {
            if (rules[index] == null) {
                rules[index] = new ArrayList<>(1);
            }
            addDefaultRangeRule(rules[index], components[index]);
        }
        return rules;
    }

    /**
     * Adds teh default range rule of the specified {@link PrimitiveType component}.
     *
     * @param rules
     *         the list of rules that we want to add the rule to.
     * @param component
     *         the component to use for determining the rule we want to add.
     */
    private static void addDefaultRangeRule(List<ConfigRule> rules, PrimitiveType component) {
        if (rules.stream().anyMatch(rule -> rule instanceof ConfigRangeRule)) {
            return;
        }
        switch (component) {
            case BYTE:
                rules.add(new ConfigRangeRule(0, 0xff));
                break;
            case SHORT:
                rules.add(new ConfigRangeRule(0, 0xffff));
                break;
            case TRIBYTE:
                rules.add(new ConfigRangeRule(0, 0xffffff));
                break;
        }
    }
}

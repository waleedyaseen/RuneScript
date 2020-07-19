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
import me.waliedyassen.runescript.config.annotation.ConfigArray;
import me.waliedyassen.runescript.config.annotation.ConfigProps;
import me.waliedyassen.runescript.config.type.ConfigVarType;
import me.waliedyassen.runescript.config.type.TypeRegistry;
import me.waliedyassen.runescript.config.type.rule.ConfigRule;
import me.waliedyassen.runescript.config.var.ConfigVar;
import me.waliedyassen.runescript.util.ReflectionUtil;

import java.lang.reflect.Modifier;
import java.util.Collections;
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
     * A map of all the variables that are in this binding.
     */
    @Getter
    private final Map<String, ConfigVar> variables = new HashMap<>();

    /**
     * The configuration group this binding is for.
     */
    @Getter
    private final ConfigGroup group;

    /**
     * Whether or not this configuration binding allows param variable.
     */
    @Getter
    @Setter
    private boolean allowParamVariable;

    /**
     * Whether or not this configuration binding allow transmit variable.
     */
    @Getter
    @Setter
    private boolean allowTransmitVariable;

    /**
     * Tries to find all of the bindings that are declared inside the specified {@link Class class type}
     * through annotations on fields.
     *
     * @param typeRegistry
     *         the type registry that we will use for translating types.
     * @param classType
     *         the class type to populate the binding from it's field annotations.
     */
    public void populateFromAnnotations(TypeRegistry typeRegistry, Class<?> classType) {
        for (var field : classType.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            var props = field.getAnnotation(ConfigProps.class);
            if (props == null) {
                continue;
            }
            var array = field.getAnnotation(ConfigArray.class);
            if (field.getType().isArray() ^ array != null) {
                throw new IllegalStateException("ConfigArray must be always used with array type fields: " + field);
            }
            Class<?> nativeType = field.getType().isArray() ? field.getType().getComponentType() : field.getType();
            nativeType = ReflectionUtil.box(nativeType);
            var varType = typeRegistry.lookup(nativeType);
            if (varType == null) {
                throw new IllegalStateException("Failed to find a matching type for native type: " + nativeType);
            }
            if (array != null) {
                addVariableRepeat(array.format(), props.opcode(), props.required(), varType, Collections.emptyList(), array.size());
            } else {
                addVariable(props.name(), props.opcode(), props.required(), varType, Collections.emptyList());
            }
        }
    }

    /**
     * Adds a new variable to the configuration binding.
     *
     * @param name
     *         the name of the variable that we want to add.
     * @param opcode
     *         the opcode of the variable that we want to add.
     * @param required
     *         whether or not the variable that we want to add is required.
     * @param type
     *         the type of the variable that we want to add.
     * @param rules
     *         the rules of the variable.
     */
    public void addVariable(String name, int opcode, boolean required, ConfigVarType type, List<ConfigRule> rules) {
        if (variables.containsKey(name)) {
            throw new IllegalArgumentException("Another variable with the same name is already defined in the binding");
        }
        variables.put(name, new ConfigVar(name, opcode, required, type, rules));
    }

    /**
     * Adds a new array variable to the configuration binding.
     *
     * @param nameFormat
     *         the format of the array component names.
     * @param opcode
     *         the opcode of the variable that we want to add.
     * @param required
     *         whether or not the variable that we want to add is required.
     * @param type
     *         the type of the variable that we want to add.
     * @param rules
     *         the rules of the variable.
     */
    public void addVariableRepeat(String nameFormat, int opcode, boolean required, ConfigVarType type, List<ConfigRule> rules, int count) {
        for (var index = 1; index <= count; index++) {
            var componentName = String.format(nameFormat, index);
            addVariable(componentName, opcode, required, type, rules);
        }
    }
}

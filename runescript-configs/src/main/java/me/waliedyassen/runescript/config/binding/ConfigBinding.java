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
import lombok.var;
import me.waliedyassen.runescript.config.ConfigGroup;
import me.waliedyassen.runescript.config.annotation.ConfigArray;
import me.waliedyassen.runescript.config.annotation.ConfigProps;
import me.waliedyassen.runescript.config.type.ConfigVarType;
import me.waliedyassen.runescript.config.type.TypeRegistry;
import me.waliedyassen.runescript.config.var.ConfigVar;
import me.waliedyassen.runescript.config.var.ConfigVarArray;

import java.lang.reflect.Modifier;
import java.util.HashMap;
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
            if (nativeType.isPrimitive()) {
                if (nativeType == byte.class) {
                    nativeType = Byte.class;
                } else if (nativeType == short.class) {
                    nativeType = Short.class;
                } else if (nativeType == int.class) {
                    nativeType = Integer.class;
                } else if (nativeType == long.class) {
                    nativeType = Long.class;
                } else if (nativeType == boolean.class) {
                    nativeType = Boolean.class;
                } else if (nativeType == char.class) {
                    nativeType = Character.class;
                } else {
                    throw new IllegalStateException("The specified primitive configuration type is not allowed: " + nativeType.getSimpleName());
                }
            }
            var varType = typeRegistry.lookup(nativeType);
            if (varType == null) {
                throw new IllegalStateException("Failed to find a matching type for native type: " + nativeType);
            }
            if (array != null) {
                addVariableArray(props.name(), props.opcode(), props.required(), varType, array.format(), array.size());
            } else {
                addVariable(props.name(), props.opcode(), props.required(), varType);
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
     */
    public void addVariable(String name, int opcode, boolean required, ConfigVarType type) {
        addVariable(name, opcode, required, type, null);
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
     * @param array
     *         the array properties of the variable.
     */
    private void addVariable(String name, int opcode, boolean required, ConfigVarType type, ConfigVarArray array) {
        if (variables.containsKey(name)) {
            throw new IllegalArgumentException("Another variable with the same name is already defined in the binding");
        }
        variables.put(name, new ConfigVar(name, opcode, required, type, array));
    }

    /**
     * Adds a new array variable to the configuration binding.
     *
     * @param name
     *         the name of the variable that we want to add.
     * @param opcode
     *         the opcode of the variable that we want to add.
     * @param required
     *         whether or not the variable that we want to add is required.
     * @param type
     *         the type of the variable that we want to add.
     * @param arrayFormat
     *         the format of the array component names.
     * @param arraySize
     *         the size of the array (the amount of components).
     */
    public void addVariableArray(String name, int opcode, boolean required, ConfigVarType type, String arrayFormat, int arraySize) {
        for (var index = 1; index <= arraySize; index++) {
            var componentName = String.format(arrayFormat, name, index);
            addVariable(componentName, opcode, required, type, new ConfigVarArray(index - 1, arraySize));
        }
    }
}

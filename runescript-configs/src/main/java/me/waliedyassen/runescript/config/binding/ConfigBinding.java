/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.binding;

import lombok.Getter;
import lombok.var;
import me.waliedyassen.runescript.config.ConfigGroup;
import me.waliedyassen.runescript.config.annotation.ConfigArray;
import me.waliedyassen.runescript.config.annotation.ConfigProps;
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
public final class ConfigBinding<T> {

    /**
     * The variables of the config
     */
    @Getter
    private final Map<String, ConfigVar> variables = new HashMap<>();

    /**
     * The type which this binding is for.
     */
    @Getter
    private final Class<T> type;

    /**
     * The configuration group this binding is for.
     */
    @Getter
    private final ConfigGroup group;

    /**
     * Constructs a new {@link ConfigBinding} type object instance.
     *
     * @param type
     *         the type of the class to populate the bindings from.
     * @param group
     *         the configuration group this binding is for.
     */
    public ConfigBinding(Class<T> type, ConfigGroup group) {
        this.type = type;
        this.group = group;
        populateVars();
    }

    /**
     * Populates the binding variables.
     */
    private void populateVars() {
        for (var field : type.getDeclaredFields()) {
            // Skip the field if it is static
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
            Class<?> type = field.getType().isArray() ? field.getType().getComponentType() : field.getType();
            if (type.isPrimitive()) {
                if (type == byte.class) {
                    type = Byte.class;
                } else if (type == short.class) {
                    type = Short.class;
                } else if (type == int.class) {
                    type = Integer.class;
                } else if (type == boolean.class) {
                    type = Boolean.class;
                } else {
                    throw new IllegalStateException("The specified primitive configuration type is not allowed: " + type.getSimpleName());
                }
            }
            if (!type.equals(props.type().getClassType())) {
                throw new IllegalStateException("The configuration type is not compatible with the field type: " + type.getSimpleName() + " and " + props.type().getClassType().getSimpleName());
            }
            if (array != null) {
                for (var index = 1; index <= array.size(); index++) {
                    var name = String.format(array.format(), props.name(), index);
                    variables.put(name, new ConfigVar(field, props.name(), props.opcode(), props.required(), props.type(), new ConfigVarArray(index - 1, array.size())));
                }
            } else {
                variables.put(props.name(), new ConfigVar(field, props.name(), props.opcode(), props.required(), props.type(), null));
            }
        }
    }
}

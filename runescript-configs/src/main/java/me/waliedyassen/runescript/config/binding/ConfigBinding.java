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
import me.waliedyassen.runescript.config.annotation.ConfigArray;
import me.waliedyassen.runescript.config.annotation.ConfigProps;
import me.waliedyassen.runescript.config.var.ConfigVar;

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
     * Constructs a new {@link ConfigBinding} type object instance.
     *
     * @param type
     *         the type of the class to populate the bindings from.
     */
    public ConfigBinding(Class<T> type) {
        this.type = type;
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
            variables.put(props.name(), new ConfigVar(field, props.name(), props.opcode(), props.required(), props.type()));
        }
    }
}

/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen;

import me.waliedyassen.runescript.compiler.codegen.asm.Local;
import me.waliedyassen.runescript.compiler.stack.StackType;
import me.waliedyassen.runescript.compiler.type.Type;

import java.util.*;

/**
 * Represents the local variables/parameters map.
 *
 * @author Walied K. Yassen
 */
public final class LocalMap {

    /**
     * The local parameters map.
     */
    private final Map<StackType, Map<String, Local>> parameters = new HashMap<>();

    /**
     * The local variables map.
     */
    private final Map<StackType, Map<String, Local>> variables = new HashMap<>();

    /**
     * Creates a new {@link Local} object and registers it in the parameters map.
     *
     * @param name
     *         the parameter name.
     * @param type
     *         the parameter type.
     *
     * @return the created {@link Local} object.
     */
    public Local registerParameter(String name, Type type) {
        var local = new Local(type);
        var map = getParametersMap(type.getStackType());
        map.put(name, local);
        return local;
    }


    /**
     * Creates a new {@link Local} object and registers it in the variables map.
     *
     * @param name
     *         the variable name.
     * @param type
     *         the variable type.
     *
     * @return the created {@link Local} object.
     */
    public Local registerVariable(String name, Type type) {
        var local = new Local(type);
        var map = getVariablesMap(type.getStackType());
        map.put(name, local);
        return local;
    }

    /**
     * Gets the parameters map of the specified {@link StackType}. If the parameter map was not present, a new one will
     * be created and cached.
     *
     * @param stackType
     *         the stack type.
     *
     * @return the parameters map as an {@link Map} object.
     */
    private Map<String, Local> getParametersMap(StackType stackType) {
        var list = parameters.get(stackType);
        if (list == null) {
            parameters.put(stackType, list = new LinkedHashMap<>());
        }
        return list;
    }

    /**
     * Gets the variables map of the specified {@link StackType}. If the variables map was not present, a new one will
     * be created and cached.
     *
     * @param stackType
     *         the stack type.
     *
     * @return the variables map as an {@link Map} object.
     */
    private Map<String, Local> getVariablesMap(StackType stackType) {
        var list = variables.get(stackType);
        if (list == null) {
            variables.put(stackType, list = new LinkedHashMap<>());
        }
        return list;
    }

    /**
     * Resets the state of this local map.
     */
    public void reset() {
        parameters.clear();
        variables.clear();
    }
}

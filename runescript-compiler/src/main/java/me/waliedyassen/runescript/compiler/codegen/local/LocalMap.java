/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.local;

import lombok.Getter;
import me.waliedyassen.runescript.type.StackType;
import me.waliedyassen.runescript.type.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the local variables/parameters map.
 *
 * @author Walied K. Yassen
 */
public final class LocalMap {

    /**
     * The local parameters map.
     */
    @Getter
    private final Map<StackType, List<Local>> parameters = new HashMap<>();

    /**
     * The local variables map.
     */
    @Getter
    private final Map<StackType, List<Local>> variables = new HashMap<>();

    /**
     * The look-up map for both of the parameters and the variables.
     */
    private final Map<String, Local> lookupMap = new HashMap<>();

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
        var local = new Local(name, type);
        var list = getParametersList(type.getStackType());
        list.add(local);
        lookupMap.put(name, local);
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
        var local = new Local(name, type);
        var list = getVariablesList(type.getStackType());
        list.add(local);
        lookupMap.put(name, local);
        return local;
    }

    /**
     * Looks-up for the {@link Local} object that is for the specified variable @code name}.
     *
     * @param name
     *         the name of the variable or the parameter.
     *
     * @return the {@link Local} object if it was present otherwise {@code null}.
     */
    public Local lookup(String name) {
        return lookupMap.get(name);
    }

    /**
     * Gets the parameters list of the specified {@link StackType}. If the parameter list was not present, a new one
     * will list
     *
     * @param stackType
     *         the stack type.
     *
     * @return the parameters {@link List list} object.
     */
    public List<Local> getParametersList(StackType stackType) {
        var list = parameters.get(stackType);
        if (list == null) {
            parameters.put(stackType, list = new ArrayList<>());
        }
        return list;
    }

    /**
     * Gets the variables list of the specified {@link StackType}. If the variables list was not present, a new one will
     * be created and cached.
     *
     * @param stackType
     *         the stack type.
     *
     * @return the variables {@link List list} object.
     */
    public List<Local> getVariablesList(StackType stackType) {
        var list = variables.get(stackType);
        if (list == null) {
            variables.put(stackType, list = new ArrayList<>());
        }
        return list;
    }

    /**
     * Resets the state of this local map.
     */
    public void reset() {
        parameters.clear();
        variables.clear();
        lookupMap.clear();
    }
}

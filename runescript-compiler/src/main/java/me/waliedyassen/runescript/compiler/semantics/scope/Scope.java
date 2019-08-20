/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.semantics.scope;

import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.symbol.impl.ArrayInfo;
import me.waliedyassen.runescript.compiler.symbol.impl.variable.VariableDomain;
import me.waliedyassen.runescript.compiler.symbol.impl.variable.VariableInfo;
import me.waliedyassen.runescript.compiler.type.Type;
import me.waliedyassen.runescript.compiler.type.primitive.PrimitiveType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a source code block scope, it holds the variables that was declared within that scope as well as other
 * useful information.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class Scope {

    /**
     * The parent scope of this scope.
     */
    private final Scope parent;

    /**
     * The declared variables within this scope.
     */
    private final Map<String, VariableInfo> variables = new LinkedHashMap<>();

    /**
     * The declared arrays within this scope.
     */
    private final Map<String, ArrayInfo> arrays = new LinkedHashMap<>();

    /**
     * Declares a new local variable with the specified {@code name} and {@code type} in this scope.
     *
     * @param name
     *         the name of the variable to declare.
     * @param type
     *         the type of the variable to declare.
     *
     * @return the declared variable information.
     */
    public VariableInfo declareLocalVariable(String name, Type type) {
        var info = new VariableInfo(VariableDomain.LOCAL, name, type);
        variables.put(name, info);
        return info;
    }

    /**
     * Gets the declared variable with the specified {@code name}.
     *
     * @param name
     *         the name of the variable.
     *
     * @return the {@link VariableInfo} object if the variable could be accessed otherwise {@code null}.
     */
    public VariableInfo getLocalVariable(String name) {
        var variable = variables.get(name);
        if (variable == null && parent != null) {
            variable = parent.getLocalVariable(name);
        }
        return variable;
    }

    /**
     * Declares a new array with the specified {@code name} and {@code type} in this scope.
     *
     * @param name
     *         the name of the array to declare.
     * @param type
     *         the type of the array to declare.
     *
     * @return the declared array information.
     */
    public ArrayInfo declareArray(String name, PrimitiveType type) {
        if (getArrayCount() >= 5) {
            throw new IllegalStateException("You cannot have more than 5 arrays in the same scope");
        }
        var info = new ArrayInfo(arrays.size(), name, type);
        arrays.put(name, info);
        return info;
    }

    /**
     * Gets the declared array with the specified {@code name}.
     *
     * @param name
     *         the name of the array.
     *
     * @return the {@link ArrayInfo} object if the array could be accessed otherwise {@code null}.
     */
    public ArrayInfo getArray(String name) {
        var array = arrays.get(name);
        if (array == null && parent != null) {
            array = parent.getArray(name);
        }
        return array;
    }

    /**
     * Gets the current declared arrays count.
     *
     * @return the current declared arrays count.
     */
    public int getArrayCount() {
        var scope = this;
        var count = 0;
        do {
            count += scope.arrays.size();
        } while ((scope = scope.parent) != null);
        return count;
    }

    /**
     * Creates a child {@link Scope} object.
     *
     * @return the created {@link Scope} object.
     */
    public Scope createChild() {
        return new Scope(this);
    }
}

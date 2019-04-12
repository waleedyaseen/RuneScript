/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.semantics.scope;

import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.type.Type;

import java.util.HashMap;
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
        var info = new VariableInfo(name, type);
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
     * Creates a child {@link Scope} object.
     *
     * @return the created {@link Scope} object.
     */
    public Scope createChild() {
        return new Scope(this);
    }
}

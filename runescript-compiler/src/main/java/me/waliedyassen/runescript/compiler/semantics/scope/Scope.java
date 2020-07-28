/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.semantics.scope;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.compiler.codegen.local.Local;
import me.waliedyassen.runescript.compiler.symbol.impl.ArrayInfo;
import me.waliedyassen.runescript.compiler.symbol.impl.variable.VariableInfo;
import me.waliedyassen.runescript.type.PrimitiveType;
import me.waliedyassen.runescript.type.Type;

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
    @Getter
    private final Scope parent;

    /**
     * The declared variables within this scope.
     */
    private final Map<String, Local> variables = new LinkedHashMap<>();

    /**
     * The declared arrays within this scope.
     */
    private final ArrayTable arrays;

    /**
     * Constructs a new {@link Scope} type object instance.
     *
     * @param parent
     *         the parent scope object of the scope.
     */
    public Scope(Scope parent) {
        this.parent = parent;
        arrays = new ArrayTable(parent == null);
    }

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
    public Local declareLocalVariable(String name, Type type) {
        var info = new Local(name, type);
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
    public Local getLocalVariable(String name) {
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
        return declareArray(getFreeArray(), name, type);
    }

    /**
     * Declares a new array with the specified {@code name} and {@code type} in this scope.
     *
     * @param index
     *         the index of the array to declare.
     * @param name
     *         the name of the array to declare.
     * @param type
     *         the type of the array to declare.
     *
     * @return the declared arrayt information.
     */
    public ArrayInfo declareArray(int index, String name, PrimitiveType type) {
        if (getArrayCount() >= ArrayTable.MAX_ARRAY_COUNT) {
            throw new IllegalStateException("You cannot have more than " + ArrayTable.MAX_ARRAY_COUNT + " arrays in the same scope");
        }
        var root = getRoot().arrays;
        // For faster performance we store teh arrays only in the root scope.
        if (root.entries[index] != null) {
            throw new IllegalStateException("The array with the same index was already registered in the table.");
        }
        var info = new ArrayInfo(index, name, type);
        arrays.put(info);
        root.entries[index] = info;
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
        var array = arrays.lookup.get(name);
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
    int getArrayCount() {
        return getRoot().arrays.getArrayCount();
    }

    /**
     * Creates a child {@link Scope} object.
     *
     * @return the created {@link Scope} object.
     */
    public Scope createChild() {
        return new Scope(this);
    }

    /**
     * Gets the root {@link Scope} object of this scope.
     *
     * @return the root {@link Scope} object.
     */
    private Scope getRoot() {
        var root = this;
        while (root.parent != null) {
            root = root.parent;
        }
        return root;
    }

    /**
     * Calculates the next free index for an array in this table.
     *
     * @return the next free index if it was present otherwise {@code -1}.
     */
    int getFreeArray() {
        return getRoot().arrays.getFreeIndex();
    }

    /**
     * Represents an array table of the scope.
     *
     * @author Walied K. Yassen
     */
    final class ArrayTable {

        /**
         * The maximum array count allowed at once in a single script execution.
         */
        static final int MAX_ARRAY_COUNT = 5;

        /**
         * The look-up map of the table.
         */
        final Map<String, ArrayInfo> lookup = new HashMap<>();

        /**
         * The registered arrays of the scope, however, this currently is only usable through the root scope which
         * contains all of the arrays in child scopes.
         */
        final ArrayInfo[] entries;

        /**
         * Constructs a new {@link ArrayTable} type object instance.
         *
         * @param root
         *         whether or not the table is for the root scope.
         */
        ArrayTable(boolean root) {
            entries = root ? new ArrayInfo[MAX_ARRAY_COUNT] : null;
        }

        /**
         * Puts a new {@link ArrayInfo} object into this table.
         *
         * @param info
         *         the array info object to put into the table.
         */
        void put(ArrayInfo info) {
            lookup.put(info.getName(), info);
        }

        /**
         * Calculates how many arrays are currently registered in this table.
         * <p>
         * This can only be used through the root scope.
         * </p>
         *
         * @return the amount of arrays that are currently registered.
         */
        int getArrayCount() {
            var count = 0;
            for (var entry : entries) {
                if (entry != null) {
                    count++;
                }
            }
            return count;
        }

        /**
         * Calculates the next free index for an array in this table.
         * <p>
         * This can only be used through the root scope.
         * </p>
         *
         * @return the next free index if it was present otherwise {@code -1}.
         */
        int getFreeIndex() {
            for (var index = 0; index < entries.length; index++) {
                var entry = entries[index];
                if (entry == null) {
                    return index;
                }
            }
            return -1;
        }
    }
}

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.symbol.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.symbol.Symbol;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the symbol information for an interface type.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class InterfaceInfo extends Symbol {

    /**
     * The components of the interface.
     */
    private final Map<String, Integer> components = new HashMap<>();

    /**
     * The name of the interface.
     */
    @Getter
    private final String name;

    /**
     * The id of the interface.
     */
    @Getter
    private final int id;

    /**
     * Defines a new component in the interface.
     *
     * @param name the name of the component.
     * @param id   the id of the component.
     */
    public void defineComponent(String name, int id) {
        if (components.containsKey(name)) {
            throw new IllegalArgumentException("The component '" + name + "' is already defined.");
        }
        components.put(name, id);
    }

    /**
     * Returns the id of the component with the specified {@code name}.
     *
     * @param name the name of the component.
     * @return the id of the component if it was present otherwise {@code null}.
     */
    public Integer lookupComponent(String name) {
        return components.get(name);
    }
}

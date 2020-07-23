/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.var;

import lombok.var;
import me.waliedyassen.runescript.config.var.rule.ConfigRule;
import me.waliedyassen.runescript.type.PrimitiveType;

import java.util.Collections;
import java.util.List;

/**
 * The base class for all of the properties.
 *
 * @author Walied K. Yassen
 */
public interface ConfigProperty {

    /**
     * An empty array of rules.
     */
    @SuppressWarnings("unchecked")
    List<ConfigRule>[] NO_RULES = new List[0];

    /**
     * Returns the name of the property.
     *
     * @return the name of the property.
     */
    String getName();

    /**
     * Returns the components that make up the property.
     *
     * @return the components that make up the property.
     */
    PrimitiveType[] getComponents();

    /**
     * Whether or not the property is required to be present in all of the configurations.
     *
     * @return <code>true</code> if it is required otherwise <code>false</code>.
     */
    default boolean isRequired() {
        return false;
    }

    /**
     * Checks whether or not this configuration property allow duplicates of the same property.
     *
     * @return <code>true</code> if it allows otherwise <code>false</code>.
     */
    default boolean isAllowDuplicates() {
        return false;
    }

    /**
     * Returns a list of all the rules that apply to this property.
     *
     * @return a list of all the  rules that apply to this property.
     */
    default List<ConfigRule>[] getRules() {
        return NO_RULES;
    }

    /**
     * Returns the list of {@link ConfigRule} of the value at the specified {@code index}.
     *
     * @param index
     *         the index the value is at.
     *
     * @return a {@link List} object containing the {@link ConfigRule rules}.
     */
    default List<ConfigRule> getRules(int index) {
        var array = getRules();
        if (index < 0 || index >= array.length) {
            return Collections.emptyList();
        }
        var rules = array[index];
        return rules == null ? Collections.emptyList() : rules;
    }
}

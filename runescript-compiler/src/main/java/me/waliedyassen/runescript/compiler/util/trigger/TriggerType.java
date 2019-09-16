/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.util.trigger;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a script trigger type.
 *
 * @author Walied K. Yassen
 */
public enum TriggerType implements TriggerProperties {

    /**
     * The clientscript trigger type.
     */
    CLIENTSCRIPT("clientscript"),

    /**
     * The procedure trigger type.
     */
    PROC("proc", PROPERTY_INVOKE, PROPERTY_RETURN);


    /**
     * The textual representation of the trigger type.
     */
    @Getter
    private final String representation;

    /**
     * Whether or not this trigger type supports returning a value.
     */
    @Getter
    private final int properties;

    /**
     * Constructs a new {@link TriggerType} enum constant.
     *
     * @param representation
     *         the textual representation of the trigger.
     * @param properties
     *         the properties of the trigger.
     */
    TriggerType(String representation, int... properties) {
        this.representation = representation;
        var _properties = 0;
        for (var property : properties) _properties |= property;
        this.properties = _properties;
    }

    /**
     * Checks whether or not this {@link TriggerType} has the specified {@code property}.
     *
     * @param property
     *         the property to check.
     *
     * @return <code>true</code> if it does otherwise <code>false</code>.
     */
    public boolean hasProperty(int property) {
        return (properties & property) == property;
    }

    /**
     * The look-up by representation map of the trigger types.
     */
    private static final Map<String, TriggerType> lookupMap = Arrays.stream(values()).collect(Collectors.toMap(TriggerType::getRepresentation, type -> type));

    /**
     * Gets the {@link TriggerType} with the specified code {@code representation}.
     *
     * @param representation
     *         the code representation of the trigger type.
     *
     * @return the {@link TriggerType} if it was present otherwise {@code null}.
     */
    public static TriggerType forRepresentation(String representation) {
        return lookupMap.get(representation);
    }
}

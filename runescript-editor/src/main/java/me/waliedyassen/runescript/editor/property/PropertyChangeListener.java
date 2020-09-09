/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.property;

/**
 * A listener that listens to changes in a specific property or properties value.
 *
 * @param <T>
 *         the type of value the property or properties hold.
 */
@FunctionalInterface
public interface PropertyChangeListener<T> {

    /**
     * Invokes the listener. This will get called when a value is set and is different than the old value in a specific
     * property.
     *
     * @param property
     *         the property which it's the value was changed.
     * @param oldValue
     *         the old value of the property.
     * @param newValue
     *         the new value of the property.
     */
    void propertyChanged(Property<T> property, T oldValue, T newValue);
}

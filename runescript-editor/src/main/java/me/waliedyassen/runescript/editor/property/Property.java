/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.property;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A property is basically value that can be listened to it's changes.
 *
 * @param <T>
 *         the type of of the value the property will hold.
 *
 * @author Walied K. Yassen
 */
@AllArgsConstructor
public abstract class Property<T> {

    /**
     * The listeners which are currently listening for changes to this property.
     */
    private final List<PropertyListener<T>> listeners = new ArrayList<>();

    /**
     * The current value of the property.
     */
    private T value;

    /**
     * Returns the current value of the property.
     *
     * @return an object of type {@link T}.
     */
    public T get() {
        return value;
    }

    /**
     * Updates the value of the property, if the value is the same as the current value, nothing will happen otherwise
     * all the listeners will be fired.
     *
     * @param newValue
     *         the new value to set for the property.
     */
    public void set(T newValue) {
        if (value == newValue) {
            return;
        }
        fireListeners(value, value = newValue);
    }

    /**
     * Checks whether or not the property currently holds a null value.
     *
     * @return <code>true</code> if it does otherwise <code>false</code>.
     */
    public boolean isEmpty() {
        return value == null;
    }

    /**
     * Fires all of the listeners.
     *
     * @param oldValue
     *         the old value of the property.
     * @param newValue
     *         the new value of the property.
     */
    private void fireListeners(T oldValue, T newValue) {
        if (listeners.isEmpty()) {
            return;
        }
        listeners.forEach(listener -> listener.invoke(this, oldValue, newValue));
    }

    /**
     * Registers a new listener to this property. This is same as calling {@link #addListener(Consumer)} except that
     * this method calls the specified {@link Consumer listener} with the current value before registering it.
     *
     * @param listener
     *         the listener to call and register.
     */
    public void bind(Consumer<T> listener) {
        listener.accept(value);
        addListener(((property, oldValue, newValue) -> listener.accept(newValue)));
    }

    /**
     * Registers a new listener to this property.
     *
     * @param listener
     *         the {@link Consumer} which will be called with the new value upon a change in the property value.
     */
    public void addListener(Consumer<T> listener) {
        addListener(((property, oldValue, newValue) -> listener.accept(newValue)));
    }

    /**
     * Registers a new {@link PropertyListener listener} to this property.
     *
     * @param listener
     *         the listener that we want to register to this property.
     *
     * @throws IllegalArgumentException
     *         if the specified listener is already registered to this property.
     */
    public void addListener(PropertyListener<T> listener) {
        if (listeners.contains(listener)) {
            throw new IllegalArgumentException("The specified listener is already registered to this property.");
        }
        listeners.add(listener);
    }
}

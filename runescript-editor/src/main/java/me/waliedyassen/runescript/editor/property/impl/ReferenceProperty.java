/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.property.impl;

import me.waliedyassen.runescript.editor.property.Property;

/**
 * A {@link Property} implementation that holds a reference value of type {@link T}.
 *
 * @author Walied K. Yassen
 */
public final class ReferenceProperty<T> extends Property<T> {

    /**
     * Constructs a new {@link ReferenceProperty} type object instance.
     */
    public ReferenceProperty() {
        this(null);
    }

    /**
     * Constructs a new {@link ReferenceProperty} type object instance.
     *
     * @param value
     *         the initial value of the reference.
     */
    public ReferenceProperty(T value) {
        super(value);
    }
}

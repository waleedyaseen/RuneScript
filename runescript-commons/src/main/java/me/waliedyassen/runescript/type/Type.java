/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.type;

import me.waliedyassen.runescript.type.stack.StackType;

/**
 * Represents the main interface for our type system.
 *
 * @author Walied K. Yassen
 */
public interface Type {

    /**
     * Returns the representation of the type.
     *
     * @return the type textual representation.
     */
    String getRepresentation();

    /**
     * Returns the stack type of the type.
     *
     * @return the stack type.
     */
    StackType getStackType();

    /**
     * Returns the default value of the type.
     *
     * @return the default value of this type.
     */
    Object getDefaultValue();

    /**
     * Returns the signature char code of the type.
     *
     * @return the signature char code of the type.
     */
    char getCode();
}

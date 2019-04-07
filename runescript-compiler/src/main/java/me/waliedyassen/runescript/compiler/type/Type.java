/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.type;

import me.waliedyassen.runescript.compiler.stack.StackType;

/**
 * Represents the main interface for our type system.
 *
 * @author Walied K. Yassen
 */
public interface Type {

    /**
     * Gets the type textual representation.
     *
     * @return the type textual representation.
     */
    String getRepresentation();

    /**
     * Gets the stack type.
     *
     * @return the stack type.
     */
    StackType getStackType();
}

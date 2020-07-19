/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.type;

import me.waliedyassen.runescript.compiler.symbol.SymbolTable;
import me.waliedyassen.runescript.type.PrimitiveType;

/**
 * Represents a configuration variable type in the RuneScript type system.
 *
 * @author Walied K. Yassen
 */
public interface ConfigVarType {

    /**
     * Returns the native Java type of the configuration type.
     *
     * @return the native Java type as {@link Class} object.
     */
    Class<?> getNativeType();

    /**
     * Returns an array of all the primitive components that make up one entity of this type.
     *
     * @return an array of all the primitive components.
     */
    PrimitiveType[] getComponents();
}

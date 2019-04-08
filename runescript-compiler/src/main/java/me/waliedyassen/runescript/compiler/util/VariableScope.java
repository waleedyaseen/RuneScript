/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.util;

/**
 * Represents a variable scope, it tells from where the variable can be accessed.
 *
 * @author Walied K. Yassen
 */
public enum VariableScope {

    /**
     * The variable is only available within the script its declared in.
     */
    LOCAL,

    /**
     * The variable is available in any script.
     */
    GLOBAL,
}

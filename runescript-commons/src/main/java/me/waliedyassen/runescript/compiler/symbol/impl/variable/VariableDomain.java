/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.symbol.impl.variable;

/**
 * Represents a variable domain.
 *
 * @author Walied K. Yassen
 */
public enum VariableDomain {

    /**
     * The local variables domain.
     */
    LOCAL,

    /**
     * The player variables domain.
     */
    PLAYER,

    /**
     * The player bit variables domain.
     */
    PLAYER_BIT,

    /**
     * The client integer variables domain.
     */
    CLIENT_INT,

    /**
     * The client string variables domain.
     */
    CLIENT_STRING,
}

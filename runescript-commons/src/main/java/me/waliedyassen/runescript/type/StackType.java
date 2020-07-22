/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.type;

/**
 * Represents the stack value push/pop type, there are three main types within our frame stack system, which are: an an
 * {@code int}, a {@code string} or a {@code long} value.
 *
 * @author Walied K. Yassen
 */
public enum StackType {

    /**
     * The integer stack type.
     */
    INT,

    /**
     * The string stack type.
     */
    STRING,

    /**
     * The long stack type.
     */
    LONG
}

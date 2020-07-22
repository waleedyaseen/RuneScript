/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.runtime.script;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Hashtable;

/**
 * A runtime script, holds all the data and the information we need to execute the script.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class Script {

    /**
     * The name of the script.
     */
    @Getter
    private final String name;

    /**
     * The instructions of the script.
     */
    @Getter
    private final int[] instructions;

    /**
     * The integer operands of the script.
     */
    @Getter
    private final Object[] operands;

    /**
     * The amount of integer local fields in the script.
     */
    @Getter
    private final int numIntLocals;

    /**
     * The amount of string local fields in the script.
     */
    @Getter
    private final int numStringLocals;

    /**
     * The amount of long local fields in the script.
     */
    @Getter
    private final int numLongLocals;

    /**
     * The amount of int arguments in the script.
     */
    @Getter
    private final int numIntArguments;

    /**
     * The amount of string arguments in the script.
     */
    @Getter
    private final int numStringArguments;

    /**
     * The amount of long arguments in the script.
     */
    @Getter
    private final int numLongArguments;

    /**
     * The table for all the switch jumps in the script.
     */
    @Getter
    private final Hashtable<Integer, Integer>[] switchTable;
}

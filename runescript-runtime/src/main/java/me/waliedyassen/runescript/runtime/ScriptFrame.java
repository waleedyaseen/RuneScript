/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package me.waliedyassen.runescript.runtime;

import lombok.Getter;
import me.waliedyassen.runescript.runtime.ScriptRuntime;
import me.waliedyassen.runescript.runtime.script.Script;

import java.util.Arrays;

/**
 * @author Walied K. Yassen
 */
public final class ScriptFrame {


    /**
     * The script of the script frame.
     */
    @Getter
    private Script script;

    /**
     * The execution address of the script frame.
     */
    @Getter
    private int address;

    /**
     * The integer local fields of the script frame.
     */
    @Getter
    private final int[] intLocals = new int[ScriptRuntime.MAX_LOCALS];

    /**
     * The string local fields of the script frame.
     */
    @Getter
    private final String[] stringLocals = new String[ScriptRuntime.MAX_LOCALS];

    /**
     * The long local fields of the script frame.
     */
    @Getter
    private final long[] longLocals = new long[ScriptRuntime.MAX_LOCALS];

    /**
     * Sets the content of the script frame based on the specified {@link ScriptRuntime}.
     *
     * @param runtime the runtime which we want to populate the data from.
     */
    public void set(ScriptRuntime runtime) {
        System.arraycopy(runtime.getIntLocals(), 0, intLocals, 0, ScriptRuntime.MAX_LOCALS);
        System.arraycopy(runtime.getStringLocals(), 0, stringLocals, 0, ScriptRuntime.MAX_LOCALS);
        System.arraycopy(runtime.getLongLocals(), 0, longLocals, 0, ScriptRuntime.MAX_LOCALS);
        script = runtime.getScript();
        address = runtime.getAddress();
    }
}

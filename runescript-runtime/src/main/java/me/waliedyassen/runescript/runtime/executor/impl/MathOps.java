/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.runtime.executor.impl;

import me.waliedyassen.runescript.runtime.ScriptRuntime;
import me.waliedyassen.runescript.runtime.executor.instruction.InstructionExecutor;

/**
 * Contains all of the common math operations.
 *
 * @author Walied K. Yassen
 */
public interface MathOps {

    /**
     * A math operation which pops two ints from the stack, adds them, then pushes the result onto the stack.
     */
    InstructionExecutor<? extends ScriptRuntime> ADD = runtime -> runtime.pushInt(runtime.popInt() + runtime.popInt());

    /**
     * A math operation which pops two ints from the stack, subtracts them, then pushes the result onto the stack.
     */
    InstructionExecutor<? extends ScriptRuntime> SUB = runtime -> runtime.pushInt(runtime.popInt() - runtime.popInt());

    /**
     * A math operation which pops two ints from the stack, multiplies them, then pushes the result onto the stack.
     */
    InstructionExecutor<? extends ScriptRuntime> MUL = runtime -> runtime.pushInt(runtime.popInt() * runtime.popInt());

    /**
     * A math operation which pops two ints from the stack, divides them, then pushes the result onto the stack.
     */
    InstructionExecutor<? extends ScriptRuntime> DIV = runtime -> runtime.pushInt(runtime.popInt() / runtime.popInt());

    /**
     * A math operation which pops an int from the stack, then pushes a random number between 0 and the value.
     */
    InstructionExecutor<? extends ScriptRuntime> RANDOM = runtime -> runtime.pushInt((int) (Math.random() * runtime.popInt()));

    /**
     * A math operation which pops an int from the stack, then pushes a random number between 0 and the value inclusively.
     */
    InstructionExecutor<? extends ScriptRuntime> RANDOMINC = runtime -> runtime.pushInt((int) (Math.random() * (runtime.popInt()) + 1));

    /**
     * A math operation which pops five ints from the stack and pushes one value onto the stack.
     */
    InstructionExecutor<? extends ScriptRuntime> INTERPOLATE = runtime -> {
        int base = runtime.popInt();
        int original_from = runtime.popInt();
        int original_to = runtime.popInt();
        int scale_from = runtime.popInt();
        int scale_to = runtime.popInt();
        runtime.pushInt(base + (scale_to - original_to) * (original_from - base) / (scale_from - original_to));
    };
}

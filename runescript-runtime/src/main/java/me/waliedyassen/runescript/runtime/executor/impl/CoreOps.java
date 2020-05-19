/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.runtime.executor.impl;

import me.waliedyassen.runescript.runtime.executor.instruction.InstructionExecutor;

/**
 * Contains all of the core RuneScript operations.
 *
 * @author Walied K. Yasssen
 */
public interface CoreOps {

    /**
     * Pushes a constant integer value to the stack.
     */
    InstructionExecutor PUSH_CONSTANT_INT = (runtime, opcode) -> runtime.pushInt(runtime.intOperand());

    /**
     * Pushes a constant string value to the stack.
     */
    InstructionExecutor PUSH_CONSTANT_STRING = (runtime, opcode) -> runtime.pushString(runtime.stringOperand());

    /**
     * Pushes a long string value to the stack.
     */
    InstructionExecutor PUSH_CONSTANT_LONG = (runtime, opcode) -> runtime.pushLong(runtime.longOperand());

    /**
     * Pushes the value of an integer local field to the stack.
     */
    InstructionExecutor PUSH_INT_LOCAL = (runtime, opcode) -> runtime.pushInt(runtime.getIntLocals()[runtime.intOperand()]);

    /**
     * Updates the value of a local field from the stack.
     */
    InstructionExecutor POP_INT_LOCAL = (runtime, opcode) -> runtime.getIntLocals()[runtime.intOperand()] = runtime.popInt();

    /**
     * Pushes the value of a string local field to the stack.
     */
    InstructionExecutor PUSH_STRING_LOCAL = (runtime, opcode) -> runtime.pushString(runtime.getStringLocals()[runtime.intOperand()]);

    /**
     * Updates the value of a string local field from the stack.
     */
    InstructionExecutor POP_STRING_LOCAL = (runtime, opcode) -> runtime.getStringLocals()[runtime.intOperand()] = runtime.popString();

    /**
     * Pushes the value of an long local field to the stack.
     */
    InstructionExecutor PUSH_LONG_LOCAL = (runtime, opcode) -> runtime.pushLong(runtime.getLongLocals()[runtime.intOperand()]);

    /**
     * Updates the value of a long local field from the stack.
     */
    InstructionExecutor POP_LONG_LOCAL = (runtime, opcode) -> runtime.getLongLocals()[runtime.intOperand()] = runtime.popLong();
}

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
}

/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.runtime.executor.instruction;

import me.waliedyassen.runescript.runtime.ScriptRuntime;
import me.waliedyassen.runescript.runtime.executor.ExecutionException;

/**
 * An instruction executor, it is responsible for executing a specific instruction(s) in a runtime.
 *
 * @author Walied K. Yassen
 */
@FunctionalInterface
public interface InstructionExecutor {

    /**
     * Executes the instruction in the specified runtime.
     *
     * @param runtime
     *         the runtime we are executing the instruction in.
     * @param opcode
     *         the opcode of the instruction which we are executing.
     *
     * @throws ExecutionException
     */
    void execute(ScriptRuntime runtime, int opcode) throws ExecutionException;
}

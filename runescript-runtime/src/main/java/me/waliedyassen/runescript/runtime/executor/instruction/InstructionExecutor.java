/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
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
public interface InstructionExecutor<R extends ScriptRuntime> {

    /**
     * Executes the instruction in the specified runtime.
     *
     * @param runtime the runtime we are executing the instruction in.
     * @throws ExecutionException if anything occurs during the execution of the instruction.
     */
    void execute(R runtime) throws ExecutionException;
}

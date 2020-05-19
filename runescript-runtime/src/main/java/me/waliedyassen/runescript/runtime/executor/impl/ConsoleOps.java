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
 * Contains all of the common console operations.
 *
 * @author Walied K. Yassen
 */
public interface ConsoleOps {

    /**
     * An instruction which writes to the console of the host VM.
     */
    InstructionExecutor WRITECONSOLE = (runtime, opcode) -> System.out.println(runtime.popString());
}

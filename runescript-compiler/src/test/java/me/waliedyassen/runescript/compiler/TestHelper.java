/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler;

import me.waliedyassen.runescript.compiler.codegen.Instruction;
import me.waliedyassen.runescript.compiler.codegen.InstructionMap;
import me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode;

public final class TestHelper {

    public static Instruction dummyInstruction() {
        return new Instruction(dummyOpcode(), 0);
    }

    public static InstructionMap.MappedOpcode dummyOpcode() {
        return new InstructionMap.MappedOpcode(CoreOpcode.POP_INT_DISCARD, 0, true);
    }

    private TestHelper() {
        // NOOP
    }
}

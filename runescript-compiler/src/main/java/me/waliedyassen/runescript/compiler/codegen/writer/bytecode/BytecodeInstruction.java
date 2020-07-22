/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.writer.bytecode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents a byte code instruction.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class BytecodeInstruction {

    /**
     * The opcode number of the instruction.
     */
    @Getter
    private final int opcode;

    /**
     * Whether or not this instruction uses a large integer operand.
     */
    @Getter
    private final boolean large;

    /**
     * The operand of this instruction.
     */
    @Getter
    private final Object operand;
}


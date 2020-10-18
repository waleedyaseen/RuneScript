/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen;

import lombok.*;
import me.waliedyassen.runescript.compiler.codegen.block.Block;
import me.waliedyassen.runescript.compiler.codegen.opcode.Opcode;

/**
 * Represents a single code instruction in our code generator.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
@ToString
public final class Instruction {

    /**
     * The opcode of this instruction.
     */
    @Getter
    @Setter
    @NonNull
    private Opcode opcode;

    /**
     * The operand of the instruction.
     */
    @Getter
    @Setter
    @NonNull
    private Object operand;

    /**
     * The owner block of this instruction.
     */
    @Getter
    @Setter
    @ToString.Exclude
    protected Block owner;

    /**
     * Returns the operand casted to an integer value.
     *
     * @return the integer value of the operand.
     */
    public int intOperand() {
        return ((Number) operand).intValue();
    }
}

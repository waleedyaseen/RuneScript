/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.asm;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a block of instructions.
 *
 * @author Walied K. Yassen
 */
public final class Block {
    
    /**
     * The instructions of the block.
     */
    private final List<Instruction> instructions = new ArrayList<>();

    /**
     * Adds the specified {@link Instruction instruction} to this block.
     *
     * @param instruction
     *         the instruction to add to this block.
     *
     * @throws IllegalArgumentException
     *         if the instruction is a a child of another block.
     */
    public void add(Instruction instruction) {
        if (instruction.owner != null) {
            throw new IllegalArgumentException("The specified Instruction is already a child of another block..");
        }
        instructions.add(instruction);
        instruction.owner = this;
    }

    /**
     * Removes the specified {@link Instruction} from the block.
     *
     * @param instruction
     *         the instruction to remove.
     *
     * @throws IllegalArgumentException
     *         if the instruction is not a child of this block.
     */
    public void remove(Instruction instruction) {
        if (instruction.owner != this) {
            throw new IllegalArgumentException("The specified Instruction is not a child of this block..");
        }
        instructions.remove(instruction);
        instruction.owner = null;
    }
}

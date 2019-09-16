/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.block;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.codegen.Instruction;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a block of instructions.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class Block {

    /**
     * The label of the block.
     */
    @Getter
    private final Label label;

    /**
     * The instructions of the block.
     */
    @Getter
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
        if (instruction.getOwner() != null) {
            throw new IllegalArgumentException("The specified Instruction is already a child of another block..");
        }
        instructions.add(instruction);
        instruction.setOwner(this);
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
        if (instruction.getOwner() != this) {
            throw new IllegalArgumentException("The specified Instruction is not a child of this block..");
        }
        instructions.remove(instruction);
        instruction.setOwner(null);
    }

    /**
     * Gets the last {@link Instruction} within this block.
     *
     * @return the last {@link Instruction} object.
     */
    public Instruction last() {
        return instructions.get(instructions.size() - 1);
    }

    /**
     * Gets the previous instruction to the specified {@link Instruction}.
     *
     * @param instruction
     *         the instruction to get the previous instruction to.
     *
     * @return the {@link Instruction} previous to the specified one if it as present otherwise {@code null}.
     */
    public Instruction previous(Instruction instruction) {
        var index = instructions.indexOf(instruction);
        if (index > 0) {
            return instructions.get(index - 1);
        }
        return null;
    }
}

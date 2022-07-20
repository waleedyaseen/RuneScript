/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.optimizer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.codegen.Instruction;
import me.waliedyassen.runescript.compiler.codegen.InstructionMap;
import me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode;
import me.waliedyassen.runescript.compiler.codegen.script.BinaryScript;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the optimization phase main class, it contains the registered optimizations and is responsible for running
 * them on scripts, as well holds some helpful utility functions.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class Optimizer {

    /**
     * An array that holds all of the opcodes that modifies our execution flow.
     */
    private static final CoreOpcode[] FLOW_OPCODES = {
            CoreOpcode.BRANCH,
            CoreOpcode.BRANCH_NOT,
            CoreOpcode.BRANCH_GREATER_THAN,
            CoreOpcode.BRANCH_LESS_THAN,
            CoreOpcode.BRANCH_GREATER_THAN_OR_EQUALS,
            CoreOpcode.BRANCH_LESS_THAN_OR_EQUALS,
            CoreOpcode.LONG_BRANCH_NOT,
            CoreOpcode.LONG_BRANCH_GREATER_THAN,
            CoreOpcode.LONG_BRANCH_LESS_THAN,
            CoreOpcode.LONG_BRANCH_GREATER_THAN_OR_EQUALS,
            CoreOpcode.LONG_BRANCH_LESS_THAN_OR_EQUALS,
            CoreOpcode.BRANCH_IF_TRUE,
            CoreOpcode.BRANCH_IF_FALSE,
            CoreOpcode.RETURN,
            CoreOpcode.SWITCH};
    /**
     * A list {@link Optimization} object that this optimizer will run.
     */
    private final List<Optimization> optimizations = new ArrayList<>();

    /**
     * The instruction map which this optimizer will use to compare instructions.
     */
    private final InstructionMap instructionMap;

    /**
     * Runs all of the registered optimizations on thje specified {@link BinaryScript script}. This will keep running the
     * optimizations until there is nothing left to to optimize, the amount of times it will re-run is not known or
     * predictable.
     *
     * @param script the script to run the optimizations on.
     */
    public void run(BinaryScript script) {
        var count = 0;
        do {
            count = 0;
            for (var optimization : optimizations) {
                count += optimization.run(this, script);
                optimization.clean(this, script);
            }
        } while (count > 0);
    }

    /**
     * Registers the specified {@link Optimization optimization} into this optimizer.
     *
     * @param optimization the optimization to register.
     */
    public void register(@NonNull Optimization optimization) {
        optimizations.add(optimization);
    }

    /**
     * Transforms the specified {@link Instruction} which is basically changing the opcode and the operand
     * of the specified instruction to different ones.
     *
     * @param instruction the instruction that we want to transform.
     * @param opcode      the new opcode to set for the instruction.
     * @param operand     the new operand to set for the instruction.
     */
    public void transform(Instruction instruction, CoreOpcode opcode, Object operand) {
        @NonNull var mapped = instructionMap.lookup(opcode);
        instruction.setOpcode(mapped);
        instruction.setOperand(operand);
    }

    /**
     * Checks whether or not if the given {@link Instruction instruction } has the specified {@link CoreOpcode opcode}.
     *
     * @param instruction the instruction to check if it has the opcode.
     * @param opcode      the opcode to check against the instruction.
     * @return <code>true</code> if the instruction's opcode and the given opcode matches otherwise <code>false</code>.
     */
    public boolean is(@NonNull Instruction instruction, @NonNull CoreOpcode opcode) {
        @NonNull var mapped = instructionMap.lookup(opcode);
        return instruction.getOpcode() == mapped;
    }

    /**
     * Checks whether or not the specified {@link Instruction} modifies the execution flow.
     *
     * @param instruction the instruction to check if it modifies.
     * @return <code>true</code> if the specified instruction does otherwise <code>false</code>.
     */
    public boolean isFlow(@NonNull Instruction instruction) {
        for (var opcode : FLOW_OPCODES) {
            if (is(instruction, opcode)) {
                return true;
            }
        }
        return false;
    }
}

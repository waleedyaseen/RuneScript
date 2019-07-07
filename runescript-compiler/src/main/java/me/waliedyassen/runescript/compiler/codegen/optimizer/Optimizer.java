/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
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
import me.waliedyassen.runescript.compiler.codegen.script.Script;

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
     * A list {@Link Optimization} object that this optimizer will run.
     */
    private final List<Optimization> optimizations = new ArrayList<>();

    /**
     * The instruction map which this optimizer will use to compare instructions.
     */
    private final InstructionMap instructionMap;

    /**
     * Runs all of the registered optimizations on thje specified {@link Script script}. This will keep running the
     * optimizations until there is nothing left to to optimize, the amount of times it will re-run is not known or
     * predictable.
     *
     * @param script
     *         the script to run the optimizations on.
     */
    public void run(Script script) {
        var count = 0;
        do {
            count = 0;
            for (var optimization : optimizations) {
                count += optimization.run(this, script);
            }
        } while (count > 0);
    }

    /**
     * Registers the specified {@link Optimization optimization} into this optimizer.
     *
     * @param optimization
     *         the optimization to register.
     */
    public void register(@NonNull Optimization optimization) {
        optimizations.add(optimization);
    }

    /**
     * Checks whether or not if the given {@link Instruction instruction } has the specified {@link CoreOpcode opcode}.
     *
     * @param instruction
     *         the instruction to check if it has the opcode.
     * @param opcode
     *         the opcode to check against the instruction.
     *
     * @return <code>true</code> if the instruction's opcode and the given opcode matches otherwise <code>false</code>.
     */
    public boolean is(@NonNull Instruction instruction, @NonNull CoreOpcode opcode) {
        @NonNull var mapped = instructionMap.lookup(opcode);
        return instruction.getOpcode() == mapped;

    }
}

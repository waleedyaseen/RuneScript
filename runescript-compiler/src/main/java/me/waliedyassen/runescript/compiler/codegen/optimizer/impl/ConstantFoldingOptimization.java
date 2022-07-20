/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.optimizer.impl;

import me.waliedyassen.runescript.compiler.codegen.block.Block;
import me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode;
import me.waliedyassen.runescript.compiler.codegen.optimizer.BlockOptimization;
import me.waliedyassen.runescript.compiler.codegen.optimizer.Optimizer;
import me.waliedyassen.runescript.compiler.codegen.script.BinaryScript;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * A constant folding operations, currently only applicable for binary arithmetic operations.
 *
 * @author Walied K. Yassen
 */
public final class ConstantFoldingOptimization extends BlockOptimization {

    /**
     * A map of all the possible arithmetic folding operations.
     */
    private static final Map<CoreOpcode, BiFunction<Integer, Integer, Integer>> arithmetics = new HashMap<>();

    static {
        //noinspection Convert2MethodRef
        arithmetics.put(CoreOpcode.ADD, (lhs, rhs) -> lhs + rhs);
        arithmetics.put(CoreOpcode.SUB, (lhs, rhs) -> lhs - rhs);
        arithmetics.put(CoreOpcode.MUL, (lhs, rhs) -> lhs * rhs);
        arithmetics.put(CoreOpcode.DIV, (lhs, rhs) -> lhs / rhs);
        arithmetics.put(CoreOpcode.MOD, (lhs, rhs) -> lhs % rhs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int run(Optimizer optimizer, BinaryScript script, Block block) {
        final var opcodes = arithmetics.keySet();
        var optimized = 0;
        var instructions = block.getInstructions();
        for (var ip = 0; ip < instructions.size(); ip++) {
            var instruction = instructions.get(ip);
            for (var opcode : opcodes) {
                if (!optimizer.is(instruction, opcode)) {
                    continue;
                }
                var lhs = instructions.get(ip - 2);
                var rhs = instructions.get(ip - 1);
                if (optimizer.is(lhs, CoreOpcode.PUSH_INT_CONSTANT) && optimizer.is(rhs, CoreOpcode.PUSH_INT_CONSTANT)) {
                    var result = arithmetics.get(opcode).apply(lhs.intOperand(), rhs.intOperand());
                    instructions.remove(lhs);
                    instructions.remove(rhs);
                    optimizer.transform(instruction, CoreOpcode.PUSH_INT_CONSTANT, result);
                    optimized++;
                }
                break;
            }
        }
        return optimized;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clean(Optimizer optimizer, BinaryScript script) {
        // NOOP
    }
}

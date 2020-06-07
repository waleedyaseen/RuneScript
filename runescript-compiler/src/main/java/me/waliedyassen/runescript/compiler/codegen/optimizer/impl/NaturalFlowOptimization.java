/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.optimizer.impl;

import lombok.var;
import me.waliedyassen.runescript.compiler.codegen.block.Block;
import me.waliedyassen.runescript.compiler.codegen.block.Label;
import me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode;
import me.waliedyassen.runescript.compiler.codegen.optimizer.BlockOptimization;
import me.waliedyassen.runescript.compiler.codegen.optimizer.Optimizer;
import me.waliedyassen.runescript.compiler.codegen.script.Script;

/**
 * Represents the natural flow redudant jumps removal optimizations.
 *
 * @author Walied K. Yassen
 */
public final class NaturalFlowOptimization extends BlockOptimization {

    /**
     * {@inheritDoc}
     */
    @Override
    public int run(Optimizer optimizer, Script script, Block block) {
        var instruction = block.last();
        if (instruction != null && optimizer.is(instruction, CoreOpcode.BRANCH)) {
            var label = (Label) instruction.getOperand();
            if (script.isNextTo(block.getLabel(), label)) {
                block.remove(instruction);
                return 1;
            }
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clean(Optimizer optimizer, Script script) {
        // NOOP
    }
}

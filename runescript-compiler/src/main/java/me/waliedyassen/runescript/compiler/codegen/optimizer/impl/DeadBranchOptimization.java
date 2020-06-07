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
import me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode;
import me.waliedyassen.runescript.compiler.codegen.optimizer.BlockOptimization;
import me.waliedyassen.runescript.compiler.codegen.optimizer.Optimizer;
import me.waliedyassen.runescript.compiler.codegen.script.Script;

/**
 * Represents a dead branch optimization.
 *
 * @author Walied K. Y assen
 */
public final class DeadBranchOptimization extends BlockOptimization {

    /**
     * {@inheritDoc}
     */
    @Override
    public int run(Optimizer optimizer, Script script, Block block) {
        var instruction = block.last();
        if (instruction != null && optimizer.is(instruction, CoreOpcode.BRANCH)) {
            // We currently define dead branch if it's after a return
            // in the distant future, we may want to change that.
            var previous = block.previous(instruction);
            if (previous == null) {
                return 0;
            }
            if (optimizer.is(previous, CoreOpcode.RETURN)) {
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

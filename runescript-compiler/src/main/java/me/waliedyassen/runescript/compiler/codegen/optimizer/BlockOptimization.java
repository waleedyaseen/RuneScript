/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.optimizer;

import lombok.var;
import me.waliedyassen.runescript.compiler.codegen.script.BinaryScript;
import me.waliedyassen.runescript.compiler.codegen.block.Block;

/**
 * Represents an {@link Optimization} that is ran on a block level instead of script..
 *
 * @author Walied K. Yassen
 */
public abstract class BlockOptimization extends Optimization {

    /**
     * {@inheritDoc}
     */
    @Override
    public int run(Optimizer optimizer, BinaryScript script) {
        var units = 0;
        // TODO: We might run into concurrent modification issues if we try to modify
        // the script whilst we are still running a block optimisation.
        for (var label : script.getBlocks().keySet()) {
            units += run(optimizer, script, script.getBlocks().get(label));
        }
        return units;
    }

    /**
     * Runs the optimization on the specified {@link Block}.
     *
     * @param optimizer
     *         the optimizer which is running this optimization.
     * @param script
     *         the script which the block is located in.
     * @param block
     *         the block to run on.
     *
     * @return the amount of blocks that has been optimised.
     */
    public abstract int run(Optimizer optimizer, BinaryScript script, Block block);
}

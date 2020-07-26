/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.optimizer.impl;

import lombok.var;
import me.waliedyassen.runescript.compiler.codegen.block.Block;
import me.waliedyassen.runescript.compiler.codegen.block.Label;
import me.waliedyassen.runescript.compiler.codegen.optimizer.BlockOptimization;
import me.waliedyassen.runescript.compiler.codegen.optimizer.Optimizer;
import me.waliedyassen.runescript.compiler.codegen.script.BinaryScript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a dead (unused) block removal optimizations.
 *
 * @author Walied K. Yassen
 */
public final class DeadBlockOptimization extends BlockOptimization {

    /**
     * A list of labels that refer to the blocks we are goign to remove later on.
     */
    private final Map<Label, Integer> jumps = new HashMap<>();

    /**
     * The blocks that are goign to be removed in the current round to avoid copying.
     */
    private final List<Label> removing = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public int run(Optimizer optimizer, BinaryScript script) {
        for (var switchTable : script.getSwitchTables()) {
            for (var switchCase : switchTable.getCases()) {
                jumps.put(switchCase.getLabel(), jumps.getOrDefault(switchCase.getLabel(), 0) + 1);
            }
        }
        super.run(optimizer, script);
        for (var label : script.getBlocks().keySet()) {
            var count = jumps.getOrDefault(label, 0);
            if (count < 1 && !label.isEntryLabel()) {
                removing.add(label);
            }
        }
        removing.forEach(script.getBlocks()::remove);
        return removing.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int run(Optimizer optimizer, BinaryScript script, Block block) {
        for (var instruction : block.getInstructions()) {
            var operand = instruction.getOperand();
            if (operand instanceof Label) {
                var label = (Label) operand;
                jumps.put(label, jumps.getOrDefault(label, 0) + 1);
            }
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clean(Optimizer optimizer, BinaryScript script) {
        removing.clear();
        jumps.clear();
    }
}

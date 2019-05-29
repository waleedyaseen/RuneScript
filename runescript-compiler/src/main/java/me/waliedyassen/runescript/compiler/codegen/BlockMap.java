/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen;

import me.waliedyassen.runescript.compiler.codegen.asm.Block;
import me.waliedyassen.runescript.compiler.codegen.asm.Label;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the generator blocks map, stores all the generated blocks and used to generate new ones.
 *
 * @author Walied K. Yassen
 */
public final class BlockMap {

    /**
     * A list of all the generated blocks ordered by generation order.
     */
    private final List<Block> blocks = new ArrayList<>();

    /**
     * Generates a new {@link Block} object and stores it in this map.
     *
     * @param label
     *         the {@link Label label} of the block.
     *
     * @return the created {@link Block} object instance.
     */
    public Block generate(Label label) {
        var block = new Block(label);
        blocks.add(block);
        return block;
    }

    /**
     * Resets state of this block map, removes all the stored blocks.
     */
    public void reset() {
        blocks.clear();
    }
}

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.block;

import lombok.Getter;

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
    @Getter
    private final List<Block> blocks = new ArrayList<>();

    /**
     * Generates a new {@link Block} object and stores it in this map.
     *
     * @param label
     *         the label of the block.
     *
     * @return the created {@link Block} object instance.
     * @see #newBlock(Label)
     */
    public Block generate(Label label) {
        var block = newBlock(label);
        register(block);
        return block;
    }

    /**
     * Creates a new {@link Block} object without storing it in this map.
     *
     * @param label
     *         the label of the block.
     *
     * @return the created {@link Block} object instance.
     */
    public Block newBlock(Label label) {
        return new Block(label);
    }

    /**
     * Registers the specified {@link Block block} into this map.
     *
     * @param block
     *         the block to register.
     */
    public void register(Block block) {
        blocks.add(block);
    }

    /**
     * Resets state of this block map, removes all the stored blocks.
     */
    public void reset() {
        blocks.clear();
    }
}

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Holds a list of {@link Block} objects and other useful functions to modify or retrieve blocks from the list.
 *
 * @author Walied K. Yassen
 */
public final class BlockList {

    /**
     * A list of all the {@link Block} within this list.
     */
    private final List<Block> blocks = new ArrayList<>();

    /**
     * An array o
     */
    private final List<Label> labels = new ArrayList<>();

    /**
     * Adds the specified {@link Block} to this block list.
     *
     * @param block the block to add to the block list.
     */
    public void add(Block block) {
        blocks.add(block);
        labels.add(block.getLabel());
    }

    /**
     * Removes the specified {@link Block} from the blocks list.
     *
     * @param block the block that we want to remove.
     */
    public void remove(Block block) {
        int index = blocks.indexOf(block);
        if (index == -1) {
            return;
        }
        remove(index);
    }

    /**
     * Removes the block with the specified {@link Label} from the blocks list.
     *
     * @param label the label which we want to remove it's owner block.
     */
    public void remove(Label label) {
        int index = labels.indexOf(label);
        if (index == -1) {
            return;
        }
        remove(index);
    }

    /**
     * Removes the {@link Block} object that is at the specified {@code index} in the list.
     *
     * @param index the index of which the block is located at in the list.
     */
    private void remove(int index) {
        blocks.remove(index);
        labels.remove(index);
    }

    /**
     * Returns the index of which the owner block of the specified {@link Label} is located at in the list.
     *
     * @param label the label which we want it's owner block index in the list.
     * @return the index of the owner block in the list if it was present otherwise {@code -1}.
     */
    public int indexOf(Label label) {
        return labels.indexOf(label);
    }

    /**
     * Returns the {@link Label} that is next to the specified {@link Label} in the l ist.
     *
     * @param label the label that we the label next to it.
     * @return the {@link Label} object if it was present otherwise {@code null}.
     */
    public Label getNext(Label label) {
        int index = indexOf(label);
        if (index == -1 || index + 1 >= labels.size()) {
            return null;
        }
        return labels.get(index + 1);
    }

    /**
     * Checks whether or not the the {@link Label other} label is next to (after) the specified {@link Label label}.
     *
     * @param label the label which we want to check against.
     * @param other the other label which we want to check if it's after the label.
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public boolean isNextTo(Label label, Label other) {
        return Objects.equals(getNext(label), other);
    }

    /**
     * Returns the {@link Block} object that is associated with the specified {@link Label label}.
     *
     * @param label the label which we want it's associated {@link Block} object.
     * @return the {@link Block} object if it was present otherwise {@code null}.
     */
    public Block getBlock(Label label) {
        int index = indexOf(label);
        if (index == -1) {
            return null;
        }
        return blocks.get(index);
    }

    /**
     * Returns a copied {@link List} of all the {@link Block} objects within this lists.
     *
     * @return the copied {@link List} object.
     */
    public List<Block> getBlocks() {
        return new ArrayList<>(blocks);
    }

    /**
     * Returns a copied {@link List} of all the {@link Label} objects within this lists.
     *
     * @return the copied {@link List} object.
     */
    public List<Label> getLabels() {
        return new ArrayList<>(labels);
    }
}

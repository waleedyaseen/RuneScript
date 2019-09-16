/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.block;

import org.junit.jupiter.api.Test;

import static me.waliedyassen.runescript.compiler.TestHelper.dummyInstruction;
import static org.junit.jupiter.api.Assertions.*;

class BlockTest {

    @Test
    void testAdd() {
        var block = new Block(new Label(0, "block"));
        block.add(dummyInstruction());
        block.add(dummyInstruction());
        assertEquals(2, block.getInstructions().size());
    }

    @Test
    void testRemove() {
        var block = new Block(new Label(0, "block"));
        var instruction = dummyInstruction();
        block.add(instruction);
        assertEquals(1, block.getInstructions().size());
        block.remove(instruction);
        assertEquals(0, block.getInstructions().size());
    }

    @Test
    void testOwnership() {
        var first = new Block(new Label(0, "first"));
        var second = new Block(new Label(1, "second"));
        var shared = dummyInstruction();
        first.add(shared);
        first.add(dummyInstruction());
        second.add(dummyInstruction());
        assertThrows(IllegalArgumentException.class, () -> second.add(shared));
        assertThrows(IllegalArgumentException.class, () -> second.remove(shared));
        assertThrows(IllegalArgumentException.class, () -> first.remove(dummyInstruction()));
    }

    @Test
    void testLast() {
        var block = new Block(new Label(0, "block"));
        var instruction = dummyInstruction();
        block.add(instruction);
        assertEquals(instruction, block.last());
    }

    @Test
    void testPrevious() {
        var block = new Block(new Label(0, "block"));
        var first = dummyInstruction();
        var middle = dummyInstruction();
        var last = dummyInstruction();
        block.add(first);
        block.add(middle);
        block.add(last);
        assertEquals(first, block.previous(middle));
        assertEquals(middle, block.previous(last));
        assertNull(block.previous(first));
    }
}
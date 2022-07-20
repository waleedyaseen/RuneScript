/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *  
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen;

import me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InstructionMapTest {

    @Test
    void testReady() {
        var map = new InstructionMap();
        assertFalse(map.isReady());
        for (var opcode : CoreOpcode.values()) {
            map.registerCore(opcode, opcode.ordinal(), opcode.isLargeOperand());
        }
        assertTrue(map.isReady());
    }

    @Test
    void testLookup() {
        var map = new InstructionMap();
        for (var opcode : CoreOpcode.values()) {
            map.registerCore(opcode, opcode.ordinal(), opcode.isLargeOperand());
        }
        for (var opcode : CoreOpcode.values()) {
            var mapped = map.lookup(opcode);
            assertNotNull(mapped);
            assertEquals(opcode, mapped.getOpcode());
            assertEquals(opcode.ordinal(), mapped.getCode());
            assertEquals(opcode.isLargeOperand(), mapped.isLarge());
        }
    }
}
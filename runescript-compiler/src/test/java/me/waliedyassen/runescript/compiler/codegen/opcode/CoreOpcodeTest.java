/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *  
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.opcode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoreOpcodeTest {

    @Test
    void testLargeOperand() {
        for (var opcode : CoreOpcode.values()) {
            switch (opcode){
                case RETURN:
                case POP_INT_DISCARD:
                case POP_STRING_DISCARD:
                case ADD:
                case SUB:
                case MUL:
                case DIV:
                case MOD:
                    assertFalse(opcode.isLargeOperand());
                    break;
                default:
                    assertTrue(opcode.isLargeOperand());
                    break;
            }
        }
    }
}
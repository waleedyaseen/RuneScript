/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *  
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OperatorTest {

    @Test
    void testEquality() {
        for (var operator : Operator.values()) {
            if (operator == Operator.EQUAL || operator == Operator.NOT_EQUAL) {
                assertTrue(operator.isEquality());
            } else {
                assertFalse(operator.isEquality());
            }
        }
    }

    @Test
    void testRelational() {
        for (var operator : Operator.values()) {
            if (operator == Operator.LESS_THAN || operator == Operator.LESS_THAN_OR_EQUALS || operator == Operator.GREATER_THAN || operator == Operator.GREATER_THAN_OR_EQUALS) {
                assertTrue(operator.isRelational());
            } else {
                assertFalse(operator.isRelational());
            }
        }
    }

    @Test
    void testLogical() {
        for (var operator : Operator.values()) {
            if (operator == Operator.LOGICAL_OR || operator == Operator.LOGICAL_AND) {
                assertTrue(operator.isLogical());
            } else {
                assertFalse(operator.isLogical());
            }
        }
    }

    @Test
    void testLookup() {
        for (var operator : Operator.values()) {
            if (operator.getRepresentation()!=null) {
                assertEquals(operator, Operator.lookup(operator.getKind()));
            }
        }
    }
}
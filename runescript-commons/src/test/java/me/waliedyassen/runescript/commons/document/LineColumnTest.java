/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.commons.document;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LineColumnTest {

    private static final LineColumn ZERO_ZERO = new LineColumn(0, 0);
    private static final LineColumn ZERO_ONE = new LineColumn(0, 1);

    @Test
    void testLesserThan() {
        assertTrue(LineColumn.MIN.isLesserThan(LineColumn.MAX));
        assertFalse(LineColumn.MAX.isLesserThan(LineColumn.MIN));
        assertTrue(ZERO_ZERO.isLesserThan(ZERO_ONE));
        assertFalse(ZERO_ONE.isLesserThan(ZERO_ZERO));
    }

    @Test
    void testGreaterThan() {
        assertTrue(LineColumn.MAX.isGreaterThan(LineColumn.MIN));
        assertFalse(LineColumn.MIN.isGreaterThan(LineColumn.MAX));
        assertTrue(ZERO_ONE.isGreaterThan(ZERO_ZERO));
        assertFalse(ZERO_ZERO.isGreaterThan(ZERO_ONE));
    }

    @Test
    void testClone() {
        assertTrue(LineColumn.MIN.clone().equals(LineColumn.MIN));
        assertFalse(LineColumn.MIN.clone().equals(LineColumn.MAX));
    }
}
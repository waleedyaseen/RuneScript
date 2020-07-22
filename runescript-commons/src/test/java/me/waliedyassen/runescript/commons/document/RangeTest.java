/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package me.waliedyassen.runescript.commons.document;

import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RangeTest {

    Range range;

    @BeforeEach
    void prepareRange() {
        range = new Range();
        range.add(new LineColumn(1, 0));
        range.add(new LineColumn(5, 5));
    }

    @Test
    void testAdd() {
        var lowRange = new Range(new LineColumn(0, 0), range.getEnd());
        var highRange = new Range(range.getStart(), new LineColumn(6, 0));
        range.add(lowRange, highRange);
        range.equals(new Range(lowRange.getStart(), highRange.getEnd()));
    }

    @Test
    void testContains() {
        assertTrue(range.contains(new LineColumn(1, 3)));
        assertFalse(range.contains(new LineColumn(0, 0)));
        assertFalse(range.contains(new LineColumn(5, 6)));
    }

    @Test
    void testClone() {
        assertEquals(range.clone(), range);
    }
}
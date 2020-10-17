/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.commons.document;

import lombok.var;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LineMapTest {

    @Test
    void testCreate() {
        assertNotNull(LineMap.create(new char[0]));
    }

    @Test
    void testLineOffsetEmpty() {
        var lineMap = LineMap.create(new char[0]);
        assertEquals(0, lineMap.getLineOffset(1));
    }

    @Test
    void testLineOffsetCr() {
        var lineMap = LineMap.create("a\rbb\rccc\rdddd".toCharArray());
        assertEquals(0, lineMap.getLineOffset(1));
        assertEquals(2, lineMap.getLineOffset(2));
        assertEquals(5, lineMap.getLineOffset(3));
        assertEquals(9, lineMap.getLineOffset(4));
    }

    @Test
    void testLineOffsetLf() {
        var lineMap = LineMap.create("a\nbb\nccc\ndddd".toCharArray());
        assertEquals(0, lineMap.getLineOffset(1));
        assertEquals(2, lineMap.getLineOffset(2));
        assertEquals(5, lineMap.getLineOffset(3));
        assertEquals(9, lineMap.getLineOffset(4));
    }

    @Test
    void testLineOffsetCrLf() {
        var lineMap = LineMap.create("a\r\nbb\r\nccc\r\ndddd".toCharArray());
        assertEquals(0, lineMap.getLineOffset(1));
        assertEquals(3, lineMap.getLineOffset(2));
        assertEquals(7, lineMap.getLineOffset(3));
        assertEquals(12, lineMap.getLineOffset(4));
    }

    @Test
    void testLineOffsetOutOfRangeCr() {
        var lineMap = LineMap.create("a\rb".toCharArray());
        assertThrows(IndexOutOfBoundsException.class, () -> lineMap.getLineOffset(0));
        assertThrows(IndexOutOfBoundsException.class, () -> lineMap.getLineOffset(3));
    }

    @Test
    void testLineOffsetOutOfRangeLf() {
        var lineMap = LineMap.create("a\nb".toCharArray());
        assertThrows(IndexOutOfBoundsException.class, () -> lineMap.getLineOffset(0));
        assertThrows(IndexOutOfBoundsException.class, () -> lineMap.getLineOffset(3));
    }

    @Test
    void testLineOffsetOutOfRangeCrLf() {
        var lineMap = LineMap.create("a\r\nb".toCharArray());
        assertThrows(IndexOutOfBoundsException.class, () -> lineMap.getLineOffset(0));
        assertThrows(IndexOutOfBoundsException.class, () -> lineMap.getLineOffset(3));
    }

    @Test
    void testLineNumberEmpty() {
        var lineMap = LineMap.create(new char[0]);
        assertEquals(1, lineMap.getLineNumber(0));
    }

    @Test
    void testLineNumberCr() {
        var lineMap = LineMap.create("abc\rdef\rghi".toCharArray());
        for (int i = 0; i < 4; i++) {
            assertEquals(1, lineMap.getLineNumber(i));
        }
        for (int i = 0; i < 4; i++) {
            assertEquals(2, lineMap.getLineNumber(4 + i));
        }
        for (int i = 0; i < 3; i++) {
            assertEquals(3, lineMap.getLineNumber(8 + i));
        }
    }

    @Test
    void testLineNumberLf() {
        var lineMap = LineMap.create("abc\ndef\nghi".toCharArray());
        for (int i = 0; i < 4; i++) {
            assertEquals(1, lineMap.getLineNumber(i));
        }
        for (int i = 0; i < 4; i++) {
            assertEquals(2, lineMap.getLineNumber(4 + i));
        }
        for (int i = 0; i < 3; i++) {
            assertEquals(3, lineMap.getLineNumber(8 + i));
        }
    }

    @Test
    void testLineNumberCrLf() {
        var lineMap = LineMap.create("abc\r\ndef\r\nghi".toCharArray());
        for (int i = 0; i < 4; i++) {
            assertEquals(1, lineMap.getLineNumber(i));
        }
        for (int i = 0; i < 4; i++) {
            assertEquals(2, lineMap.getLineNumber(5 + i));
        }
        for (int i = 0; i < 3; i++) {
            assertEquals(3, lineMap.getLineNumber(10 + i));
        }
    }
}
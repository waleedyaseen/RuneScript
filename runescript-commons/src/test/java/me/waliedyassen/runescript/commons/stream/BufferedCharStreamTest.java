/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.commons.stream;

import lombok.var;
import me.waliedyassen.runescript.commons.document.LineColumn;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BufferedCharStreamTest {

    @Test
    void testTake() throws IOException {
        var buffer = "abcdef";
        var stream = new BufferedCharStream(new ByteArrayInputStream(buffer.getBytes()));
        for (var ch : buffer.toCharArray()) {
            assertEquals(ch, stream.take());
        }
        assertEquals(CharStream.NULL, stream.take());
    }

    @Test
    void testPeek() throws IOException {
        var buffer = "abcdef";
        var stream = new BufferedCharStream(new ByteArrayInputStream(buffer.getBytes()));
        for (var ch : buffer.toCharArray()) {
            assertEquals(ch, stream.peek());
            stream.take();
        }
        assertEquals(CharStream.NULL, stream.peek());
    }

    @Test
    void testLineFeed() throws IOException {
        var stream = new BufferedCharStream(new ByteArrayInputStream("\r\n\r".getBytes()));
        assertEquals('\r', stream.take());
        assertEquals('\n', stream.take());
        assertEquals('\r', stream.take());
        assertEquals(CharStream.NULL, stream.take());
    }

    @Test
    void testMarkReset() throws IOException {
        var stream = new BufferedCharStream(new ByteArrayInputStream("abcdef".getBytes()));
        stream.mark();
        for (int index = 0; index < 3; index++) {
            stream.take();
        }
        assertEquals('d', stream.take());
        stream.reset();
        assertEquals('a', stream.take());
        assertThrows(IllegalStateException.class, stream::reset);
    }

    @Test
    void testRollback() throws IOException {
        var stream = new BufferedCharStream(new ByteArrayInputStream("abcdef\t\r\nabcdef".getBytes()));
        assertEquals('a', stream.peek());
        assertEquals(0, stream.position());
        for (int index = 0; index < 3; index++) {
            stream.take();
        }
        assertEquals(3, stream.position());
        stream.rollback(3);
        assertEquals(0, stream.position());
        assertEquals('a', stream.peek());
        for (int index = 0; index < 14; index++) {
            stream.take();
        }
        assertEquals(14, stream.position());
        stream.rollback(7);
        assertEquals(7, stream.position());
        stream = new BufferedCharStream(new ByteArrayInputStream("\r\na\r\n\tb".getBytes()));
        for (int index = 0; index < 5; index++) {
            stream.take();
        }
        assertEquals(5, stream.position());
        stream.rollback(3);
        assertEquals(2, stream.position());
    }

    @Test
    void testRemaining() throws IOException {
        var stream = new BufferedCharStream(new ByteArrayInputStream("a".getBytes()));
        assertTrue(stream.hasRemaining());
        stream.take();
        assertFalse(stream.hasRemaining());
    }

    @Test
    void testPosition() throws IOException {
        var stream = new BufferedCharStream(new ByteArrayInputStream("abc\n\ta".getBytes()));
        assertEquals(0, stream.position());
        stream.take();
        assertEquals(1, stream.position());
        stream.take();
        assertEquals(2, stream.position());
        stream.take();
        assertEquals(3, stream.position());
        stream.take();
        assertEquals(4, stream.position());
        stream.take();
        assertEquals(5, stream.position());
        stream.take();
        assertEquals(6, stream.position());
    }
}
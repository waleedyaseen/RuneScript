/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.commons.stream;

import lombok.var;
import me.waliedyassen.runescript.commons.document.LineMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Represents a buffered character stream, it reads all the data from {@link InputStream} and then caches the data into
 * a {@code char[]} object.
 *
 * @author Walied K. Yassen
 */
public final class BufferedCharStream implements CharStream {

    /**
     * A map of all the positions mapped to line numbers.
     */
    private LineMap lineMap; // Lazily initialized

    /**
     * The characters buffer data.
     */
    private final char[] buffer;

    /**
     * The current position.
     */
    private int pos;

    /**
     * The marked position within the buffer.
     */
    private int mark;

    /**
     * Constructs a new {@link BufferedCharStream} type object instance.
     *
     * @param stream the stream which we are going to retrieve the char data from.
     * @throws IOException if anything occurs while retrieving the char data from the specified stream.
     */
    public BufferedCharStream(InputStream stream) throws IOException {
        this(readAllChars(stream));
    }

    /**
     * Constructs a new {@link BufferedCharStream} type object instance.
     *
     * @param buffer the characters buffer we are going to be taking from.
     */
    public BufferedCharStream(char[] buffer) {
        this.buffer = buffer;
        mark = -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public char take() {
        if (pos >= buffer.length) {
            return NULL;
        }
        return buffer[pos++];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public char peek() {
        if (pos >= buffer.length) {
            return NULL;
        }
        return buffer[pos];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mark() {
        mark = pos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        if (mark == -1) {
            throw new IllegalStateException("The stream has no marker set");
        }
        pos = mark;
        mark = -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rollback(int count) {
        pos -= count;
        if (pos < 0) {
            pos = 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasRemaining() {
        return pos < buffer.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int position() {
        return pos;
    }

    /**
     * Returns the current line number the stream is at.
     *
     * @return the current line number the stream is at.
     */
    public int line() {
        return lineMap.getLineNumber(pos);
    }

    /**
     * Returns the {@link LineMap} if cached, or create new one if not cached.
     *
     * @return the {@link LineMap} object.
     */
    public LineMap getLineMap() {
        if (lineMap == null) {
            lineMap = LineMap.create(buffer);
        }
        return lineMap;
    }

    /**
     * Reads all of the content of the specified {@link InputStream stream} into a character array.
     *
     * @param stream the stream to read all of it's content.
     * @return the characters array that we read.
     * @throws IOException if anything occurs while reading from the input stream.
     */
    private static char[] readAllChars(InputStream stream) throws IOException {
        var buffer = new char[stream.available()];
        try (var reader = new InputStreamReader(stream)) {
            if (reader.read(buffer) != buffer.length) {
                // TODO: Change to another solution, but this should work fine for now.
                throw new IllegalStateException("Failed to read the input stream fully");
            }
        }
        return buffer;
    }
}
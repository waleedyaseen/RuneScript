/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.commons.stream;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a buffered character stream, it reads all the data from {@link InputStream} and then caches the data into
 * a {@code char[]} object.
 *
 * @author Walied K. Yassen
 */
public final class BufferedCharStream implements CharStream {

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
        this(stream, 0);
    }

    /**
     * Constructs a new {@link BufferedCharStream} type object instance.
     *
     * @param stream the stream which we are going to retrieve the char data from.
     * @param pos    the initial position within the buffer.
     * @throws IOException if anything occurs while retrieving the char data from the specified stream.
     */
    private BufferedCharStream(InputStream stream, int pos) throws IOException {
        this.pos = pos;
        buffer = new char[stream.available()];
        for (int index = 0; index < buffer.length; index++) {
            buffer[index] = (char) stream.read();
        }
    }

    // TODO: Add support for passing char[] to the constructor

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
}
/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.commons.document;


import java.util.Arrays;

/**
 * Responsible for generating a line number table for a character sequence and holds functions for
 * retrieving data from the line table.
 *
 * @author Walied K. Yassen
 */
public final class LineMap {

    /**
     * A table which holds the start positions of each line.
     */
    private final int[] table;

    /**
     * Constructs a new {@link LineMap} type object instance.
     *
     * @param text the document text as {@code char} array.
     */
    private LineMap(char[] text) {
        if (text.length == 0) {
            table = new int[1];
        } else {
            var buffer = new int[text.length];
            var line = 0;
            buffer[line++] = 0;
            for (var pos = 0; pos < text.length; pos++) {
                var ch = text[pos];
                if (ch == '\r' || ch == '\n') {
                    if (ch == '\r' && pos + 1 < text.length && text[pos + 1] == '\n') {
                        pos++;
                    }
                    buffer[line++] = pos + 1;
                }

            }
            table = Arrays.copyOf(buffer, line);
        }
    }

    /**
     * Returns the offset of the specified {@code line} number, line numbers start from {@code 1}.
     * <p>
     * An offset represents a single character in any type of document, whether the character is printable or not
     * does not affect it being counted.
     * </p>
     *
     * @param line the line number which we want to retrieve it's offset.
     * @return the offset of the specified {@code line} number.
     */
    public int getLineOffset(int line) {
        if (line < 1 || line > table.length) {
            throw new IndexOutOfBoundsException("Line number out of range: " + line + " expected [1-" + (table.length - 1) + "]");
        }
        return table[line - 1];
    }

    /**
     * Returns the line number of the character at the specified {@code offset}.
     *
     * @param offset the offset which we want to retrieve the line number from.
     * @return the line number starting from {@code 1}.
     */
    public int getLineNumber(int offset) {
        var line = 0;
        for (; line < table.length; line++) {
            if (offset < table[line]) {
                break;
            }
        }
        return line;
    }

    /**
     * Creates a {@link LineMap} object for the specified source {@code text}.
     *
     * @param text the source text as a {@code char} array.
     * @return the created {@link LineMap} object.
     */
    public static LineMap create(char[] text) {
        return new LineMap(text);
    }
}

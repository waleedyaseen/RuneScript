/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.commons.document;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.var;

/**
 * Represents a position range within a document.
 *
 * @author Walied K. Yassen
 */
@EqualsAndHashCode
@AllArgsConstructor
public final class Range {

    /**
     * The start position of the range in a document.
     */
    @Getter
    private int start;

    /**
     * The width of the range in characters.
     */
    @Getter
    private int width;

    /**
     * Constructs a new {@link Range} type object instance.
     */
    public Range() {
        this(Integer.MAX_VALUE, 0);
    }

    /**
     * Constructs a new {@link Range} type object instance.
     *
     * @param ranges the ranges which will be combined into one range.
     */
    public Range(Range... ranges) {
        add(ranges);
    }

    /**
     * Performs {@link #add(Range)} for each of the given {@code ranges}.
     *
     * @param ranges the ranges to perform for.
     */
    public void add(Range... ranges) {
        for (var range : ranges) {
            add(range);
        }
    }

    /**
     * Updates this position {@link Range} to include the specified {@linkplain Range range}.
     *
     * @param range the range which we wil update this {@link Range} object to include.
     */
    public void add(Range range) {
        if (range.width == 0) {
            return;
        }
        if (width == 0) {
            start = range.start;
            width = range.width;
        } else {
            int _start = Math.min(start, range.start);
            width = Math.max(start + width, range.start + range.width) - _start;
            start = _start;
        }
    }

    /**
     * Checks whether or not the specified {@linkplain LineColumn position} is within this position {@link Range} or
     * not.
     *
     * @param position the position to check whether is it within this position range or not.
     * @return <code>true</code> if the specified position is within this range otherwise <code>false</code>.
     */
    public boolean contains(int position) {
        return position >= start && position < start + width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Range clone() {
        return new Range(start, width);
    }
}

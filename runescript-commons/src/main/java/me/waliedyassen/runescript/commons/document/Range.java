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
import lombok.ToString;
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
     * The range start position.
     */
    @Getter
    private LineColumn start;

    /**
     * The range end position.
     */
    @Getter
    private LineColumn end;

    /**
     * Constructs a new {@link Range} type object instance.
     */
    public Range() {
        this(LineColumn.MAX, LineColumn.MIN);
    }

    /**
     * Updates this position {@link Range} to include the specified {@linkplain LineColumn position}.
     *
     * @param position
     *         the position which we will update this {@link Range} object to include.
     */
    public void add(LineColumn position) {
        if (position.isLesserThan(start)) {
            start = position;
        } else if (position.isGreaterThan(end)) {
            end = position;
        }
    }

    /**
     * Performs {@link #add(Range)} for each of the given {@code ranges}.
     *
     * @param ranges
     *         the ranges to perform for.
     */
    public void add(Range... ranges) {
        for (var range : ranges) {
            add(range);
        }
    }

    /**
     * Updates this position {@link Range} to include the specified {@linkplain Range range}.
     *
     * @param range
     *         the range which we wil update this {@link Range} object to include.
     */
    public void add(Range range) {
        add(range.getStart());
        add(range.getEnd());
    }

    /**
     * Checks whether or not the specified {@linkplain LineColumn position} is within this position {@link Range} or
     * not.
     *
     * @param position
     *         the position to check whether is it within this position range or not.
     *
     * @return <code>true</code> if the specified position is within this range otherwise <code>false</code>.
     */
    public boolean contains(LineColumn position) {
        return position.isGreaterThan(start) && position.isLesserThan(end);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Range clone() {
        return new Range(start.clone(), end.clone());
    }
}

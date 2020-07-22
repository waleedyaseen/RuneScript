/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.commons.document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents a ("line and column") structure with additional utility methods.
 *
 * @author Walied K. Yassen
 */
@Data
public final class LineColumn {

    /**
     * The {@link LineColumn} object with the minimum position.
     */
    public static final LineColumn MIN = new LineColumn(0, 0);

    /**
     * The {@link LineColumn} object with the maximum position.
     */
    public static final LineColumn MAX = new LineColumn(Short.MAX_VALUE, Short.MAX_VALUE);

    /**
     * The line number within the document.
     */
    @Getter
    private final int line;

    /**
     * The column number within the line.
     */
    @Getter
    private final int column;

    /**
     * Checks whether or not the given {@linkplain LineColumn position} is lesser than this position.
     *
     * @param other
     *         the other position to check whether it is lesser or not.
     *
     * @return <code>true</code> if the given position is lesser than this position otherwise <code>false</code>.
     */
    public boolean isLesserThan(LineColumn other) {
        if (line == other.line) {
            return column < other.column;
        } else if (line < other.line) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether or not the given {@linkplain LineColumn position} is greater than this position.
     *
     * @param other
     *         the other position to check whether it is greater or not.
     *
     * @return <code>true</code> if the given position is greater than this position otherwise <code>false</code>.
     */
    public boolean isGreaterThan(LineColumn other) {
        if (line == other.line) {
            return column > other.column;
        } else if (line > other.line) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LineColumn clone() {
        return new LineColumn(line, column);
    }
}

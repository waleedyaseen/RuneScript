/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler;

import me.waliedyassen.runescript.commons.document.Range;

/**
 * Represents an error that has occurred during any of the compiler phases.
 *
 * @author Walied K. Yassen
 */
public class CompilerError extends RuntimeException {

    /**
     * The serialisation key of the {@link CompilerError} type.
     */
    private static final long serialVersionUID = 5818510240712800528L;

    /**
     * The error source code range.
     */
    private final Range range;

    /**
     * Constructs a new {@link CompilerError} type object instance.
     *
     * @param range
     *         the error source code range.
     * @param message
     *         a message describing why the error has occurred.
     */
    public CompilerError(Range range, String message) {
        super(message);
        this.range = range;
    }

    /**
     * Gets the error source code range.
     *
     * @return the source code range.
     */
    public Range getRange() {
        return range;
    }
}
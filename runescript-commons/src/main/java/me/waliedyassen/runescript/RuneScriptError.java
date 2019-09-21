/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript;

/**
 * Represents the base class for all of the RuneScript language errors.
 *
 * @author Walied K. Yassen
 */
public abstract class RuneScriptError extends RuntimeException {

    /**
     * The serialisation version of the {@link RuneScriptError} type.
     */
    private static final long serialVersionUID = -1;

    /**
     * Constructs a new {@link RuneScriptError} type object instance.
     *
     * @param message
     *         the error message text content.
     */
    RuneScriptError(String message) {
        super(message);
    }

    /**
     * Constructs a new {@link RuneScriptError} type object instance.
     *
     * @param message
     *         the error message text content.
     * @param cause
     *         the root cause of this error.
     */
    RuneScriptError(String message, Throwable cause) {
        super(message, cause);
    }
}

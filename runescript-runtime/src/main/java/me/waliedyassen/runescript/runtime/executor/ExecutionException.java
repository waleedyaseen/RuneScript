/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.runtime.executor;

/**
 * A {@link RuntimeException} implementation that is raised when a problem occurs during the execution of a script of an
 * instruction.
 *
 * @author Walied K. Yassen
 */
public final class ExecutionException extends RuntimeException {

    /**
     * Constructs a {@link ExecutionException} type object instance.
     *
     * @param message
     *         the error message of the exception
     */
    public ExecutionException(String message) {
        super(message);
    }

    /**
     * Constructs a {@link ExecutionException} type object instance.
     *
     * @param message
     *         the error message of the exception
     * @param cause
     *         the parent cause of the exception.
     */
    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}

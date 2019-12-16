/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.project;

/**
 * A {@link RuntimeException} implementation for exceptions or errors that happen during the execution of the {@link
 * ProjectManager} functions.
 *
 * @author Walied K. Yassen
 */
public final class ProjectException extends RuntimeException {

    /**
     * Constructs a new {@link ProjectException} type object instance.
     *
     * @param message
     *         the error message of the exception.
     */
    public ProjectException(String message) {
        super(message);
    }


    /**
     * Constructs a new {@link ProjectException} type object instance.
     *
     * @param message
     *         the error message of the exception.
     * @param cause
     *         the original cause of the exception.
     */
    public ProjectException(String message, Throwable cause) {
        super(message, cause);
    }
}

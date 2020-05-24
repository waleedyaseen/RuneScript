/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.project.dependency;

/**
 * a {@link RuntimeException} implementation which is raised when a circular dependency was found in a dependency
 * graph.
 *
 * @author Walied K. Yassen
 */
public final class CircularDependencyException extends RuntimeException {

    /**
     * Constructs a new {@link CircularDependencyException} type object instance.
     *
     * @param message the message of the exception.
     */
    public CircularDependencyException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@link CircularDependencyException} type object instance.
     *
     * @param message the message of the exception.
     * @param cause   the cause of the exception.
     */
    public CircularDependencyException(String message, Throwable cause) {
        super(message, cause);
    }
}

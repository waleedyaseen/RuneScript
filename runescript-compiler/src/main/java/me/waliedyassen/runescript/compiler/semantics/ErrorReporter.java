/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.semantics;

/**
 * A functional interface that is responsible for reporting errors in a specific way.
 *
 * @author Walied K. Yassen
 */
@FunctionalInterface
public interface ErrorReporter {

    /**
     * Reports the specified {@link SemanticError} using this error reporter.
     *
     * @param error the error that want to report using this report.
     */
    void reportError(SemanticError error);
}

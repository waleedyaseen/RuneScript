/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.error;

import lombok.Getter;
import me.waliedyassen.runescript.compiler.CompilerError;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a list of all the errors that are reported by one of the compilation process.
 *
 * @author Walied K. Yassen
 */
public class ErrorReporter {

    /**
     * A list of all the errors that has been reported.
     */
    @Getter
    private final List<CompilerError> errors = new ArrayList<>();

    /**
     * Adds a new error to the errors list of the reporter.
     *
     * @param error the error to add to the errors list.
     */
    public void addError(CompilerError error) {
        errors.add(error);
    }
}

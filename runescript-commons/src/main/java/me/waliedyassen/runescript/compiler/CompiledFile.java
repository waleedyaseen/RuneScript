/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * A compiled file. Contains all the units and errors that were produced by the compiler.
 *
 * @param <U>
 *         the type of units that will be contained in the compiled file.
 *
 * @author Walied K. Yassen
 */
public final class CompiledFile<U> {

    /**
     * The errors that are in this compiled error.
     */
    @Getter
    private final List<CompilerError> errors = new ArrayList<>();

    /**
     * The units that are in this compiled file.
     */
    @Getter
    private final List<U> units = new ArrayList<>();

    /**
     * Adds the specified {@link U unit} to this compiled file.
     *
     * @param unit
     *         the unit that we want to add to this compiled file.
     */
    public void addUnit(U unit) {
        units.add(unit);
    }

    /**
     * Adds the specified {@link CompilerError error} to this compiled file.
     *
     * @param error
     *         the error that we want to add to this compiled file.
     */
    public void addError(CompilerError error) {
        errors.add(error);
    }
}

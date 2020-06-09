/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.compiler.util.Pair;

import java.util.List;

/**
 * The result of a compile call in the compiler, contains the errors and scripts the compile call produced.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class CompileResult {

    /**
     * The scripts that were compiled successfully.
     */
    @Getter
    private final Pair<Object, CompiledScript>[] scripts;

    /**
     * The errors that were produced while compiling the scripts.
     */
    @Getter
    private final Pair<Object, CompilerError>[] errors;

    /**
     * Whether or not the compilation was completely successful.
     */
    @Getter
    private final boolean successful;

    /**
     * Returns na array of all the errors in this result.
     *
     * @return an array object instance that contains all of the errors.
     */
    public CompilerError[] getErrorsValue() {
        var errors = new CompilerError[this.errors.length];
        for (var index = 0; index < this.errors.length; index++) {
            errors[index] = this.errors[index].getValue();
        }
        return errors;
    }

    /**
     * Returns na array of all the scripts in this result.
     *
     * @return an array object instance that contains all of the scripts.
     */
    public CompiledScript[] getScriptsValue() {
        var scripts = new CompiledScript[this.scripts.length];
        for (var index = 0; index < this.scripts.length; index++) {
            scripts[index] = this.scripts[index].getValue();
        }
        return scripts;
    }

    /**
     * Creates a {@link CompileResult} object from the specified {@code scripts} and {@code errors} lists.
     *
     * @param scripts the scripts that should be added to the result.
     * @param errors  the errors that should be added to the result.
     * @return the created {@link CompileResult} object instance.
     */
    @SuppressWarnings("unchecked")
    public static CompileResult of(List<Pair<Object, CompiledScript>> scripts, List<Pair<Object, CompilerError>> errors) {
        return new CompileResult(scripts.toArray(new Pair[0]), errors.toArray(new Pair[0]), errors.isEmpty());
    }
}

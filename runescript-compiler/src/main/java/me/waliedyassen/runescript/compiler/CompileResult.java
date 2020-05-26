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
import me.waliedyassen.runescript.CompilerError;

import java.util.ArrayList;
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
    private final CompiledScript[] scripts;

    /**
     * The errors that were produced while compiling the scripts.
     */
    @Getter
    private final CompilerError[] errors;

    /**
     * Whether or not the compilation was completely successful.
     */
    @Getter
    private final boolean successful;

    /**
     * Creates a {@link CompileResult} object from the specified {@code scripts} and {@code errors} lists.
     *
     * @param scripts the scripts that should be added to the result.
     * @param errors  the errors that should be added to the result.
     * @return the created {@link CompileResult} object instance.
     */
    public static CompileResult of(List<CompiledScript> scripts, List<CompilerError> errors) {
        return new CompileResult(scripts.toArray(CompiledScript[]::new), errors.toArray(CompilerError[]::new), errors.isEmpty());
    }
}

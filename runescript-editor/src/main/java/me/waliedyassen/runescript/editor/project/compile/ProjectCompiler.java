/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.project.compile;

import me.waliedyassen.runescript.compiler.CompiledUnit;
import me.waliedyassen.runescript.compiler.Input;
import me.waliedyassen.runescript.compiler.Output;
import me.waliedyassen.runescript.compiler.syntax.SyntaxBase;
import me.waliedyassen.runescript.editor.project.cache.unit.CacheUnit;

import java.io.IOException;

/**
 * Represents a bridge between the project and the compiler of a specific type of files.
 *
 * @param <S> the type of Syntax Tree node this compiler produces.
 * @param <U> the type of the compilation unit this compiler produces.
 * @author Walied K. Yassen
 */
public interface ProjectCompiler<S extends SyntaxBase, U extends CompiledUnit<S>> {

    /**
     * Performs a compile call using the underlying compiler.
     *
     * @param input the input of the compiler.
     * @return the output of the compiler.
     */
    Output<S, U> compile(Input input) throws IOException;

    /**
     * Creates a new {@link CacheUnit} implementation object.
     *
     * @param path     the file path of the cache unit.
     * @param fileName the file name of the cache unit.
     * @return the created {@link CacheUnit} object.
     */
    CacheUnit<?> createUnit(String path, String fileName);
}

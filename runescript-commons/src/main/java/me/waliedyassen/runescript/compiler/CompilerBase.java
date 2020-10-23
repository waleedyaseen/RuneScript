/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler;

import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.idmapping.IDManager;
import me.waliedyassen.runescript.compiler.syntax.SyntaxBase;

import java.io.IOException;

/**
 * The base class for all the compiler implementations we use for RuneScript.
 *
 * @param <S> the type of the Syntax Tree object this compiler will produce.
 * @param <U> the type of the compilation unit this compiler will produce.
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public abstract class CompilerBase<S extends SyntaxBase, U extends CompiledUnit<S>> {

    /**
     * The ID manager for the compiled units.
     */
    protected final IDManager idManager;

    /**
     * Attempts to compile all of the source code specified in the {@link Input} object
     * and produce a {@link Output output} object which contains the compiled form of the object
     * and the associated errors produced during that compilation process.
     *
     * @param input the input object which contains the all of the source code that we want to compile.
     * @return the {@link Output} object instance.
     * @throws IOException if somehow a problem occurred while writing or reading from the temporary streams.
     */
    public abstract Output<S, U> compile(Input input) throws IOException;

}

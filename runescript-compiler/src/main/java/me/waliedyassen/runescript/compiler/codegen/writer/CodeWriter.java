/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.writer;

import me.waliedyassen.runescript.compiler.codegen.script.Script;

/**
 * Represents a script code writer, it takes {@link Script} object and converts (write) it to a different form which is
 * specified by the implementation of this type.
 *
 * @param <R>
 *         the return type for {@link #write(Script)} method.
 *
 * @author Walied K. Yassen
 */
public abstract class CodeWriter<R> {

    /**
     * Performs the code writing for the specified {@link Script} object.
     *
     * @param script
     *         the script to perform the code writing for.
     *
     * @return the output of the code writing operation, defined by the implementation of this type.
     */
    public abstract R write(Script script);
}

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler;

import me.waliedyassen.runescript.compiler.syntax.SyntaxBase;

/**
 * The base class for all of the compilation units.
 *
 * @param <S> the Syntax Tree node type of the compiled unit.
 */
public abstract class CompiledUnit<S extends SyntaxBase> {

    /**
     * Returns the Syntax Tree node object.
     *
     * @return the Syntax Tree node object.
     */
    public abstract S getSyntax();
}

/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.lexer;

import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.CompilerError;

/**
 * Represens any error that has occured during the lexical phase (parsing) of the compilation process.
 *
 * @author Walied K. Yassen
 */
public class LexicalError extends CompilerError {

    /**
     * The serialisation key of the {@link LexicalError} type.
     */
    private static final long serialVersionUID = -7355707302290328841L;

    /**
     * Constructs a new {@link LexicalError} type object instance.
     *
     * @param range
     *         the error source code range.
     * @param message
     *         a message describing why the error has occurred.
     */
    public LexicalError(Range range, String message) {
        super(range, message + " at line: " + range.getStart().getLine() + ", column: " + range.getStart().getColumn());
    }
}

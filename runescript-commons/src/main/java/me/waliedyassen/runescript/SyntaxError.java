/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;

/**
 * Represents a syntax error. The syntax error occurs when the input text sequence does not match the grammar rule or is
 * in the wrong context
 *
 * @author Walied K. Yassen
 */
public final class SyntaxError extends CompilerError {

    /**
     * The serialisation key of the {@link SyntaxError} type.
     */
    private static final long serialVersionUID = 7930378044181873967L;

    /**
     * Constructs a new {@link SyntaxError} type object instance.
     *
     * @param range
     *         the source code range which the error occurred within.
     * @param message
     *         the error message explaining why the error has occurred.
     */
    public SyntaxError(Range range, String message) {
        super(range, message);
    }
}

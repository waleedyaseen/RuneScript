/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.parser;

import lombok.Getter;
import me.waliedyassen.runescript.compiler.lexer.token.Token;

/**
 * Represents a syntax error. The syntax error occurs when the input text sequence does not match the grammar rule or is
 * in the wrong context
 *
 * @author Walied K. Yassen
 */
public class SyntaxError extends RuntimeException {

    /**
     * The serialisation key of the {@link SyntaxError} type.
     */
    private static final long serialVersionUID = 7930378044181873967L;

    /**
     * The token which the error has occurred at.
     */
    @Getter
    private final Token token;

    /**
     * Constructs a new {@link SyntaxError} type object instance.
     *
     * @param token
     *         the token which the error has occurred at.
     * @param message
     *         the error message explaining why the error has occurred.
     */
    public SyntaxError(Token token, String message) {
        super(message);
        this.token = token;
    }
}

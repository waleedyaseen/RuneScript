/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.lexer.token;

import me.waliedyassen.runescript.commons.document.Span;

/**
 * A factory class for creating new {@link Token} objects.
 *
 * @param <K> the "kind" type the token will hold.
 * @param <T> the token objects type.
 */
public interface TokenFactory<K, T extends Token<K>> {

    /**
     * Creates a new {@link T token} object with the specified data.
     *
     * @param span  the range of the token in the source document.
     * @param kind   the kind of the token that we are creating.
     * @param lexeme the source code representation of the token.
     * @return the created {@link T} object.
     */
    T createToken(Span span, K kind, String lexeme);

    /**
     * Creates a new erroneous {@link T token} object with the specified data.
     *
     * @param span  the range of the token in the source document.
     * @param kind   the kind of the token that we are creating.
     * @param lexeme the source code representation of the token.
     * @return the created {@link T} object.
     */
    T createErrorToken(Span span, K kind, String lexeme);
}


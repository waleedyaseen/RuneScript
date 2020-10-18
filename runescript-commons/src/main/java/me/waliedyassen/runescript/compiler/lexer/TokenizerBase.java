/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.lexer;

import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.error.ErrorReporter;
import me.waliedyassen.runescript.compiler.lexer.token.Token;
import me.waliedyassen.runescript.compiler.lexer.token.TokenFactory;

/**
 * Represents a lexical parser tokenizer base class.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public abstract class TokenizerBase<K, T extends Token<K>> {

    /**
     * The error reporter we will use to report erroneous input.
     */
    protected final ErrorReporter errorReporter;

    /**
     * A factory class that is responsible for creating new {@link T} objects.
     */
    protected final TokenFactory<K, T> tokenFactory;

    /**
     * Tokenizes the next sequence of characters into a meaningful {@link T} object.
     *
     * @return the {@link T} object or {@code null} if none could be tokenized.
     */
    public abstract T parse();

    /**
     * Checks whether or not the specified character can be used as the identifier's starting character.
     *
     * @param ch
     *         the character to check.
     *
     * @return <code>true</code> if it can otherwise <code>false</code>.
     */
    public static boolean isIdentifierStart(char ch) {
        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch == '_';
    }

    /**
     * Checks whether or not the specified character can be used as an identifier's character.
     *
     * @param ch
     *         the character to check.
     *
     * @return <code>true</code> if it can otherwise <code>false</code>.
     */
    public static boolean isIdentifierPart(char ch) {
        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9' || ch == '_' || ch == ':' || ch == '+';
    }
}

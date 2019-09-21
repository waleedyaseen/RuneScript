/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.lexer;

import me.waliedyassen.runescript.lexer.token.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the base class for all of our lexical parsers.
 *
 * @param <K>
 *         the tokenizer token type.
 *
 * @author Walied K. Yassen
 */
public abstract class LexerBase<K> {

    /**
     * The list of the {@linkplain Token}s that are availabe to this lexer.
     */
    protected final List<Token<K>> tokens = new ArrayList<>();

    /**
     * The current pointer index value.
     */
    protected int index;

    /**
     * Gets the {@link Token} object at the current pointer index and then increment the pointer index.
     *
     * @return the {@link Token} object if it was present otherwise {@code null}.
     */
    public Token<K> take() {
        if (index >= tokens.size()) {
            return null;
        }
        return tokens.get(index++);
    }

    /**
     * Gets the {@link Token} object at the current pointer index without incrementing the pointer index.
     *
     * @return the {@link Token} object if it was present otherwise {@code null}.
     */
    public Token<K> peek() {
        if (index >= tokens.size()) {
            return null;
        }
        return tokens.get(index);
    }

    /**
     * Gets the previous {@link Token token} to the current token.
     *
     * @return the previous {@link Token} object.
     */
    public Token<K> previous() {
        return tokens.get(index - 1);
    }

    /**
     * Getes the token at is located at {@code n} steps from the current index.
     *
     * @param n
     *         the distance which the token is located at from the current index.
     *
     * @return the {@link Token} if it was present otherwise {@code null}.
     */
    public Token<K> lookahead(int n) {
        if (index + n >= tokens.size()) {
            return null;
        }
        return tokens.get(index + n);
    }

    /**
     * Gets the amount of the remaining tokens.
     *
     * @return the amount of the remaining tokens.
     */
    public int remaining() {
        return tokens.size() - index;
    }
}

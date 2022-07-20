/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.lexer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.lexer.token.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the base class for all of our lexical parsers.
 *
 * @param <K> the tokenizer token type.
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public abstract class LexerBase<K, T extends Token<K>> {

    /**
     * The list of the {@linkplain Token}s that are availabe to this lexer.
     */
    protected final List<T> tokens = new ArrayList<>();

    /**
     * The start range of the lexer.
     */
    @Getter
    protected final Range startRange;

    /**
     * The current pointer index value.
     */
    protected int index;

    /**
     * Gets the {@link Token} object at the current pointer index and then increment the pointer index.
     *
     * @return the {@link Token} object if it was present otherwise {@code null}.
     */
    public T take() {
        if (index >= tokens.size()) {
            return null;
        }
        return tokens.get(index++);
    }

    /**
     * Gets the {@link T} object at the current pointer index without incrementing the pointer index.
     *
     * @return the {@link T} object if it was present otherwise {@code null}.
     */
    public T peek() {
        if (index >= tokens.size()) {
            return null;
        }
        return tokens.get(index);
    }

    /**
     * Gets the previous {@link T token} to the current token.
     *
     * @return the previous {@link T} object.
     */
    public T previous() {
        return tokens.get(index - 1);
    }

    /**
     * Gets the token at is located at {@code n} steps from the current index.
     *
     * @param n the distance which the token is located at from the current index.
     * @return the {@link Token} if it was present otherwise {@code null}.
     */
    public T lookahead(int n) {
        if (index + n >= tokens.size()) {
            return null;
        }
        return tokens.get(index + n);
    }

    /**
     * Gets the last {@link Token} in the tokens list.
     *
     * @return the last {@link Token}.
     */
    public T last() {
        if (tokens.isEmpty()) {
            return null;
        }
        return tokens.get(tokens.size() - 1);
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

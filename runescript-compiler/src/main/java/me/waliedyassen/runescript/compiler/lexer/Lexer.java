/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.lexer;

import me.waliedyassen.runescript.compiler.lexer.token.Token;
import me.waliedyassen.runescript.compiler.lexer.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the main lexical phase interface, it is responsible for collecting all the {@link Token} objects from a
 * {@link Tokenizer} object and to provide the extra utilities we require in the lexical phase.
 *
 * @author Walied K. Yassen
 */
public final class Lexer {

    /**
     * The list of the {@linkplain Token}s that are availabe to this lexer.
     */
    private final List<Token> tokens = new ArrayList<Token>();

    /**
     * The current pointer index value.
     */
    private int index;

    /**
     * Constructs a new {@link Lexer} type object instance.
     *
     * @param tokenizer
     *         the tokenizer which we will take all the {@link Token} objects from.
     */
    public Lexer(Tokenizer tokenizer) {
        tokens:
        do {
            var token = tokenizer.parse();
            switch (token.getKind()) {
                case EOF:
                    break tokens;
                case COMMENT:
                    continue tokens;
                default:
                    tokens.add(token);
            }
        } while (true);
    }

    /**
     * Gets the {@link Token} object at the current pointer index and then increment the pointer index.
     *
     * @return the {@link Token} object if it was present otherwise {@code null}.
     */
    public Token take() {
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
    public Token peek() {
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
    public Token previous() {
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
    public Token lookahead(int n) {
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
    public int reminaing() {
        return tokens.size() - index;
    }
}

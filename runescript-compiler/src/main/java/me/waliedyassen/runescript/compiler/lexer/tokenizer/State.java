/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.lexer.tokenizer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.commons.document.LineColumn;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.lexer.token.Token;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Represents the {@link Tokenizer} state, it holds the current parsing state variables for that tokenizer.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
final class State {
    /**
     * The current lexeme builder, it should be reset after each token.
     */
    final StringBuilder builder = new StringBuilder();

    /**
     * The fallback tokens, they are prioritised over parsing new ones when we are calling parse().
     */
    final Deque<Token<Kind>> fallback = new ArrayDeque<>();

    /**
     * The kind of this state, defines what this state is mainly for.
     */
    final StateKind kind;

    /**
     * The current character position within the document
     */
    @NonNull
    int position;

    /**
     * The current parsing mode, tells what we are currently parsing.
     */
    Mode mode = Mode.NONE;

    /**
     * A helper {@link List} of {@link String}, mainly used for multi-line comments.
     */
    List<String> lines;

    /**
     * Creates an empty {@link State} object instance.
     *
     * @param kind     the kind of this state.
     * @param position the inital position of the state.
     * @return the created {@link State} object instance.
     */
    static State emptyState(StateKind kind, int position) {
        return new State(kind, position);
    }

    /**
     * Represents the state kind, which defines what this state is for.
     *
     * @author Walied K. Yassen
     */
    public enum StateKind {
        /**
         * The regular state kind, used for regular parsing.
         */
        REGULAR,

        /**
         * The string interpolation state kind, used to parse the content between the string interpolation tags.
         */
        INTERPOLATION,
    }
}
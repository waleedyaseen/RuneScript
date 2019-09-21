/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.parser;

import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.commons.document.Element;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.lexer.Lexer;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.lexer.token.Token;

import java.util.Stack;

/**
 * Represents the base class for every parser implementation.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public abstract class ParserBase {

    /**
     * The {@link Range} object stack. It is used to calculate the nested {@link Range}s.
     */
    private final Stack<Range> ranges = new Stack<>();

    /**
     * The lexical phase result object.
     */
    protected final Lexer lexer;

    /**
     * Takes the next {@link Token} object and checks whether or not its {@linkplain Kind kind} matches the specified
     * {@linkplain Kind kind}.
     *
     * @param expected
     *         the expected token kind.
     *
     * @return the expected {@link Token} object.
     * @throws SyntaxError
     *         if the next token does not match the expected token.
     */
    protected Token<Kind> consume(Kind expected) {
        var token = consume();
        var kind = token == null ? Kind.EOF : token.getKind();
        if (kind != expected) {
            throwError(token, "Unexpected rule: " + kind + ", expected: " + expected);
        }
        return token;
    }

    /**
     * Takes the next {@link Token} object and checks whether or not it's {@linkplain Kind kind} matches the specified
     * {@linkplain Kind kind}.
     *
     * @param expected
     *         the expected token kind.
     *
     * @return <code>true</code> if the token
     * @throws SyntaxError
     *         if the next token does not match the expected token.
     */
    protected boolean consumeIf(Kind expected) {
        var token = peek();
        var kind = token == null ? Kind.EOF : token.getKind();
        if (kind == expected) {
            consume();
            return true;
        }
        return false;
    }

    /**
     * Takes the next {@link Token} object from the lexer.
     *
     * @return the next {@link Token} object or {@code null}.
     * @see Lexer#take()
     */
    protected Token<Kind> consume() {
        var token = lexer.take();
        appendRange(token);
        return token;
    }

    /**
     * Takes the next {@link Token} object from the lexer and return it's kind if it was present or {@link Kind#EOF}.
     *
     * @return the token {@link Kind} or {@link Kind#EOF} if it was not present.
     */
    protected Kind kind() {
        var token = consume();
        if (token == null) {
            return Kind.EOF;
        }
        return token.getKind();
    }

    /**
     * Takes the next {@link Token} object without advancing the lexer cursor.
     *
     * @return the next {@link Token} object or {@code null}.
     * @see Lexer#peek()
     */
    protected Token<Kind> peek() {
        return lexer.peek();
    }

    /**
     * Gets the next token {@link Kind} from the lexer without advancing the lexer cursor.
     *
     * @return the next {@link Kind} or {@link Kind#EOF} if there is no more tokens.
     */
    protected Kind peekKind() {
        return peekKind(0);
    }

    /**
     * Attempts to get the token {@link Kind kind} that is placed after a specific amount of tokens defined by {@code
     * n}.
     *
     * @return the token {@link Kind kind } if it was present otherwise returns {@link Kind#EOF}.
     */
    protected Kind peekKind(int n) {
        var token = lexer.lookahead(n);
        if (token == null) {
            return Kind.EOF;
        }
        return token.getKind();
    }

    /**
     * Pushes a new {@link Range} into the {@link #ranges} stack. Calls to this method should be followed by {@link
     * #popRange()} to remove the pushed {@link Range} object from the stack.
     */
    protected void pushRange() {
        ranges.push(new Range());
    }

    /**
     * Appends the specified {@link Element} range into the last {@link Range} in the {@link #ranges} stack. If the
     * element is null or there is no {@link Range} object available into the stack, the method will have no effect.
     *
     * @param element
     *         the element to append it's range.
     */
    protected void appendRange(Element element) {
        if (ranges.isEmpty() || element == null) {
            return;
        }
        ranges.lastElement().add(element.getRange());
    }

    /**
     * Pops the last pushed {@link Range} object from the stack. If the stack is empty.
     *
     * @return the popped {@link Range} object.
     */
    protected Range popRange() {
        var range = ranges.pop();
        if (!ranges.isEmpty()) {
            ranges.lastElement().add(range);
        }
        return range;
    }

    /**
     * Throws a syntax error indicating a mismatched grammar rule.
     *
     * @param token
     *         the token which the error has occurred at.
     * @param message
     *         the error message describing why the error has occurred.
     */
    protected void throwError(Token<Kind> token, String message) {
        throw createError(token, message);
    }

    /**
     * Creates a syntax error indicating a mismatched grammar rule.
     *
     * @param range
     *         the source code range in which the error has occurred.
     * @param message
     *         the error message describing why the error has occurred
     *
     * @return the created {@link SyntaxError} object.
     */
    protected SyntaxError createError(Range range, String message) {
        return new SyntaxError(range, message);
    }

    /**
     * Creates a syntax error indicating a mismatched grammar rule.
     *
     * @param token
     *         the token which the error has occurred at.
     * @param message
     *         the error message describing why the error has occurred
     *
     * @return the created {@link SyntaxError} object.
     */
    protected SyntaxError createError(Token<Kind> token, String message) {
        return new SyntaxError(token == null ? null : token.getRange(), message);
    }
}

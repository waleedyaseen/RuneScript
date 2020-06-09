/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.parser;

import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.commons.document.Element;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.lexer.LexerBase;
import me.waliedyassen.runescript.compiler.lexer.token.Token;

import java.util.Arrays;
import java.util.Stack;

/**
 * Represents the base class for our parser.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public abstract class ParserBase<K> {

    /**
     * The {@link Range} object stack. It is used to calculate the nested {@link Range}s.
     */
    private final Stack<Range> ranges = new Stack<>();

    /**
     * A stack wh8ich contains all of the sub-lexer objects.
     */
    protected final Stack<LexerBase<K>> lexerStack = new Stack<>();

    /**
     * The main lexer which we are using to parse tokens for the grammar.
     */
    private final LexerBase<K> lexer;

    /**
     * The End of File token kind (only used for returning EOF when no token is present).
     */
    protected final K eofKind;

    /**
     * Takes the next {@link Token} object and checks whether or not its {@linkplain K kind} matches the specified
     * {@linkplain K kind}.
     *
     * @param expected the expected token kind.
     * @return the expected {@link Token} object.
     * @throws SyntaxError if the next token does not match the expected token.
     */
    protected Token<K> consume(K expected) {
        var token = consume();
        var kind = token == null ? eofKind : token.getKind();
        var range = token != null && token.getRange() != null ? token.getRange() : lexer.getStartRange();
        if (kind != expected) {
            throw createError(range, "Unexpected rule: " + kind + ", expected: " + expected);
        }
        return token;
    }

    /**
     * Takes the next {@link Token} object and checks whether or not it's {@linkplain K kind} matches the specified
     * {@linkplain K kind}.
     *
     * @param expected the expected token kind.
     * @return <code>true</code> if the token
     * @throws SyntaxError if the next token does not match the expected token.
     */
    protected boolean consumeIf(K expected) {
        var token = peek();
        var kind = token == null ? eofKind : token.getKind();
        if (kind == expected) {
            consume();
            return true;
        }
        return false;
    }

    /**
     * Takes the next {@link Token} object from the lexer and return it's kind.
     *
     * @return the token {@link K kind}
     */
    protected K kind() {
        var token = consume();
        if (token == null) {
            return eofKind;
        }
        return token.getKind();
    }

    /**
     * Takes the next {@link Token} object without advancing the lexer cursor.
     *
     * @return the next {@link Token} object or {@code null}.
     * @see LexerBase#peek()
     */
    protected Token<K> peek() {
        return lexer().peek();
    }

    /**
     * Gets the next token {@link K kind} from the lexer without advancing the lexer cursor.
     *
     * @return the next {@link K kind}.
     */
    protected K peekKind() {
        return peekKind(0);
    }

    /**
     * Attempts to get the token {@link K kind} that is placed after a specific amount of tokens defined by {@code n}.
     *
     * @return the token {@link K kind}.
     */
    protected K peekKind(int n) {
        var token = lexer().lookahead(n);
        if (token == null) {
            return eofKind;
        }
        return token.getKind();
    }

    /**
     * Takes the next {@link Token} object from the lexer.
     *
     * @return the next {@link Token} object or {@code null}.
     * @see LexerBase#take()
     */
    protected Token<K> consume() {
        var token = lexer().take();
        appendRange(token);
        return token;
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
     * @param element the element to append it's range.
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
     * @param token   the token which the error has occurred at.
     * @param message the error message describing why the error has occurred.
     */
    protected void throwError(Token<K> token, String message) {
        var error = createError(token, message);
        var stackTrace = error.getStackTrace();
        error.setStackTrace(Arrays.copyOfRange(stackTrace, 2, stackTrace.length));
        throw error;
    }

    /**
     * Creates a syntax error indicating a mismatched grammar rule.
     *
     * @param range   the source code range in which the error has occurred.
     * @param message the error message describing why the error has occurred
     * @return the created {@link SyntaxError} object.
     */
    protected SyntaxError createError(Range range, String message) {
        return new SyntaxError(range, message);
    }

    /**
     * Creates a syntax error indicating a mismatched grammar rule.
     *
     * @param token   the token which the error has occurred at.
     * @param message the error message describing why the error has occurred
     * @return the created {@link SyntaxError} object.
     */
    protected SyntaxError createError(Token<K> token, String message) {
        return new SyntaxError(token == null ? null : token.getRange(), message);
    }

    /**
     * Pushes one {@link LexerBase} object into the lexer stack.
     *
     * @param lexer the lexer object that we want to push into the lexer stack.
     */
    protected void pushLexer(LexerBase<K> lexer) {
        lexerStack.push(lexer);
    }

    /**
     * Pops one lexer object from the lexer stack.
     */
    protected void popLexer() {
        lexerStack.pop();
    }

    /**
     * Returns the {@link LexerBase} object we are currently using.
     *
     * @return the {@link LexerBase} object we are currently using.
     */
    protected LexerBase<K> lexer() {
        return lexerStack.isEmpty() ? lexer : lexerStack.peek();
    }
}

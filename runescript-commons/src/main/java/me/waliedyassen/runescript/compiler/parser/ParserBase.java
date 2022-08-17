/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.parser;

import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.commons.document.Element;
import me.waliedyassen.runescript.commons.document.Span;
import me.waliedyassen.runescript.compiler.error.ErrorReporter;
import me.waliedyassen.runescript.compiler.lexer.LexerBase;
import me.waliedyassen.runescript.compiler.lexer.token.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Represents the base class for our parser.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public abstract class ParserBase<K, T extends Token<K>> {

    /**
     * The {@link Span} object stack. It is used to calculate the nested {@link Span}s.
     */
    private final Stack<List<Span>> spans = new Stack<>();

    /**
     * A stack wh8ich contains all of the sub-lexer objects.
     */
    protected final Stack<LexerBase<K, T>> lexerStack = new Stack<>();

    /**
     * The error reporter of the parser.
     */
    protected final ErrorReporter errorReporter;

    /**
     * The main lexer which we are using to parse tokens for the grammar.
     */
    private final LexerBase<K, T> lexer;

    /**
     * The End of File token kind (only used for returning EOF when no token is present).
     */
    // TODO: Move this to TokenFactory instead
    protected final K eofKind;

    /**
     * Creates an empty {@link Span} object at the current parsing position.
     *
     * @return the created {@link Span} object.
     */
    protected Span emptyRange() {
        pushRange();
        return popRange();
    }

    /**
     * Takes the next {@link T} object and checks whether or not its {@linkplain K kind} matches the specified
     * {@linkplain K kind}.
     *
     * @param expected the expected token kind.
     * @return the expected {@link T} object.
     * @throws SyntaxError if the next token does not match the expected token.
     */
    protected T consume(K expected) {
        var token = peek();
        var kind = token == null ? eofKind : token.getKind();
        var range = token != null && token.getSpan() != null ? token.getSpan() : lexer.getStartSpan();
        if (kind == expected) {
            consume();
            return token;
        }
        var error = createError(range, "Unexpected rule: " + kind + ", expected: " + expected);
        errorReporter.addError(error);
        throw error; // TODO: Should never throw errors like this, should report and continue.
    }

    /**
     * Takes the next {@link T} object and checks whether or not it's {@linkplain K kind} matches the specified
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
     * Takes the next {@link T} object from the lexer and return it's kind.
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
     * Takes the next {@link T} object without advancing the lexer cursor.
     *
     * @return the next {@link T} object or {@code null}.
     * @see LexerBase#peek()
     */
    protected T peek() {
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
     * Takes the next {@link T} object from the lexer.
     *
     * @return the next {@link T} object or {@code null}.
     * @see LexerBase#take()
     */
    protected T consume() {
        var token = lexer().take();
        appendRange(token);
        return token;
    }

    /**
     * Pushes a new {@link Span} into the {@link #spans} stack. Calls to this method should be followed by {@link
     * #popRange()} to remove the pushed {@link Span} object from the stack.
     */
    protected void pushRange() {
        spans.push(new ArrayList<>());
    }

    /**
     * Appends the specified {@link Element} range into the last {@link Span} in the {@link #spans} stack. If the
     * element is null or there is no {@link Span} object available into the stack, the method will have no effect.
     *
     * @param element the element to append it's range.
     */
    protected void appendRange(Element element) {
        if (spans.isEmpty() || element == null) {
            return;
        }
        spans.lastElement().add(element.getSpan());
    }

    /**
     * Pops the last pushed {@link Span} object from the stack. If the stack is empty.
     *
     * @return the popped {@link Span} object.
     */
    protected Span popRange() {
        var spans = this.spans.pop();
        if (!this.spans.isEmpty()) {
            this.spans.lastElement().addAll(spans);
        }
        if (spans.isEmpty()) {
            return new Span(lexer.getIndex(), lexer.getIndex());
        }
        return new Span(spans);
    }

    /**
     * Throws a syntax error indicating a mismatched grammar rule.
     *
     * @param token   the token which the error has occurred at.
     * @param message the error message describing why the error has occurred.
     */
    protected void addError(T token, String message) {
        errorReporter.addError(createError(token, message));
    }

    /**
     * Creates a syntax error indicating a mismatched grammar rule.
     *
     * @param span   the source code range in which the error has occurred.
     * @param message the error message describing why the error has occurred
     * @return the created {@link SyntaxError} object.
     */
    protected SyntaxError createError(Span span, String message) {
        return new SyntaxError(span, message);
    }

    /**
     * Creates a syntax error indicating a mismatched grammar rule.
     *
     * @param token   the token which the error has occurred at.
     * @param message the error message describing why the error has occurred
     * @return the created {@link SyntaxError} object.
     */
    protected SyntaxError createError(T token, String message) {
        return new SyntaxError(token == null ? emptyRange() : token.getSpan(), message);
    }

    /**
     * Pushes one {@link LexerBase} object into the lexer stack.
     *
     * @param lexer the lexer object that we want to push into the lexer stack.
     */
    protected void pushLexer(LexerBase<K, T> lexer) {
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
    protected LexerBase<K, T> lexer() {
        return lexerStack.isEmpty() ? lexer : lexerStack.peek();
    }
}

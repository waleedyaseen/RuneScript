/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.lexer.tokenizer;

import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.commons.stream.CharStream;
import me.waliedyassen.runescript.compiler.lexer.LexicalError;
import me.waliedyassen.runescript.compiler.lexer.table.LexicalTable;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.lexer.token.Token;

import java.util.ArrayList;
import java.util.Stack;

import static me.waliedyassen.runescript.commons.stream.CharStream.NULL;
import static me.waliedyassen.runescript.compiler.lexer.token.Kind.*;

/**
 * Represents the tokenizer tool, takes {@link CharStream} object then turns it's content into {@link Token} objects.
 *
 * @author Walied K. Yassen
 */
public final class Tokenizer {

    // TODO: Interpolated strings proper range creation.

    /**
     * The current states
     */
    private final Stack<State> stack = new Stack<>();

    /**
     * The lexical symbol table.
     */
    private final LexicalTable table;

    /**
     * The characters stream of the source.
     */
    private final CharStream stream;

    /**
     * The current state
     */
    private State state = State.emptyState(State.StateKind.REGULAR);

    /**
     * Constructs a new {@link Tokenizer} type object instance.
     *
     * @param table
     *         the lexical symbol table.
     * @param stream
     *         the source code input characters stream.
     */
    public Tokenizer(LexicalTable table, CharStream stream) {
        this.table = table;
        this.stream = stream;
    }

    /**
     * Tokenizes the next sequence of characters into some meaningful {@link Token} object.
     *
     * @return the {@link Token} object or {@code null} if none could be tokenized.
     */
    public Token parse() {
        // check whether or not we have any fallback tokens.
        if (!state.fallback.isEmpty()) {
            return state.fallback.removeFirst();
        }
        // grab some vars from the state for ease of access.
        final var builder = state.builder;
        final var state_kind = state.kind;
        // the character queue of the parser.
        char current, next;
        // keep parsing until we have a something to return.
        while (true) {
            // mark the current position if we have no state yet.
            if (state.mode == Mode.NONE) {
                mark();
            }
            // take the current and next characters from the stream.
            current = stream.take();
            next = stream.peek();
            // parse the current character depending on the current state.
            switch (state.mode) {
                case NONE:
                    if (Character.isWhitespace(current)) {
                        continue;
                    } else {
                        resetBuilder();
                        if (current == NULL) {
                            return createToken(EOF);
                        } else if (isIdentifierStart(current)) {
                            builder.append(current);
                            stream.mark();
                            state.mode = Mode.IDENTIFIER;
                        } else if (current == '\"') {
                            state.mode = Mode.STRING_LITERAL;
                        } else if (Character.isDigit(current) || (current == '-' || current == '+') && Character.isDigit(next)) {
                            builder.append(current);
                            state.mode = Mode.NUMBER_LITERAL;
                        } else if (current == '/' && next == '/') {
                            stream.take();
                            state.mode = Mode.LINE_COMMENT;
                        } else if (current == '/' && next == '*') {
                            stream.take();
                            state.lines = new ArrayList<>();
                            state.mode = Mode.MULTI_COMMENT;
                        } else if (table.isSeparator(current)) {
                            return createToken(table.lookupSeparator(current), Character.toString(current));
                        } else {
                            if (state_kind == State.StateKind.INTERPOLATION && current == '>') {
                                popState();
                                state.mode = Mode.ISTRING_LITERAL;
                                continue;
                            } else if (table.isOperatorStart(current)) {
                                builder.append(current);
                                for (var index = 1; index < table.getOperatorSize(); index++) {
                                    // TODO: update the behaviour of peek() to skip the CR character, the same thing
                                    // take() does.
                                    if (!stream.hasRemaining() || stream.peek() == '\r' && stream.peek() == '\n') {
                                        break;
                                    }
                                    builder.append(stream.take());
                                }
                                while (builder.length() > 0) {
                                    var sequence = builder.toString();
                                    if (table.isOperator(sequence)) {
                                        return createToken(table.lookupOperator(sequence), sequence);
                                    }
                                    builder.setLength(builder.length() - 1);
                                    stream.rollback(1);
                                }
                            }
                            throwError("Unexpected character: " + current);
                        }
                    }
                    break;
                case IDENTIFIER:
                    if (isIdentifierPart(current)) {
                        builder.append(current);
                        stream.mark();
                    } else {
                        stream.reset();
                        var word = builder.toString();
                        return createToken(table.isKeyword(word) ? table.lookupKeyword(word) : IDENTIFIER, builder.toString());
                    }
                    break;
                case STRING_LITERAL:
                case ISTRING_LITERAL:
                    if (current == NULL || current == '\n') {
                        throwError("String literal is not properly closed by a double-quote");
                    } else if (current == '\\') {
                        stream.take();
                        switch (next) {
                            case 'b':
                                builder.append('\b');
                                break;
                            case 't':
                                builder.append('\t');
                                break;
                            case 'n':
                                builder.append('\n');
                                break;
                            case 'f':
                                builder.append('\f');
                                break;
                            case '"':
                                builder.append('\"');
                                break;
                            case '\\':
                                builder.append('\\');
                                break;
                            case '<':
                                builder.append('<');
                                break;
                            case '>':
                                builder.append('>');
                                break;
                            default:
                                throwError("Invalid escape sequence (valid ones are  \\b  \\t  \\n  \\f  \\r  \\\"  \\'  \\\\ \\< \\>)");
                                break;
                        }
                    } else if (current == '\"') {
                        if (state.mode == Mode.ISTRING_LITERAL) {
                            if (builder.length() > 0) {
                                feed(createToken(CONCATE));
                            } else {
                                return createToken(CONCATE);
                            }
                        }
                        return createToken(STRING, builder.toString());
                    } else if (current == '<') {
                        if (state.mode == Mode.ISTRING_LITERAL) {
                            pushState(State.StateKind.INTERPOLATION);
                            // we are inside an interpolated string already, no
                            // need to feed a concatenation begin token.
                            return createToken(STRING, builder.toString());
                        } else {
                            pushState(State.StateKind.INTERPOLATION);
                            // we were in a regular string and we now identified
                            // the string to be an interpolated string.
                            feed(createToken(STRING, builder.toString()));
                            return createToken(CONCATB);
                        }
                    } else {
                        builder.append(current);
                    }
                    break;
                case NUMBER_LITERAL:
                    if (Character.isDigit(current)) {
                        builder.append(current);
                    } else {
                        var kind = INTEGER;
                        if (current == 'L' || current == 'l') {
                            kind = LONG;
                        } else if (current != NULL) {
                            stream.rollback(1);
                        }
                        return createToken(kind, builder.toString());
                    }
                    break;
                case LINE_COMMENT:
                    if (current == NULL || current == '\n') {
                        return createToken(COMMENT, trimComment(builder.toString(), false));
                    } else {
                        builder.append(current);
                    }
                    break;
                case MULTI_COMMENT:
                    if (current == NULL) {
                        throwError("Unexpected end of comment");
                    } else if (current == '\n') {
                        var line = trimComment(builder.toString(), true);
                        // ignores the header line if it was empty.
                        if (state.lines.size() != 0 || line.length() != 0) {
                            state.lines.add(line);
                        }
                        resetBuilder();
                    } else if (current == '*' && next == '/') {
                        stream.take();
                        return createToken(COMMENT, String.join("\n", state.lines));
                    } else {
                        state.builder.append(current);
                    }
                    break;
            }
        }
    }

    /**
     * Creates a new {@link Token} object with the specified {@code kind}.
     *
     * @param kind
     *         the kind of the token.
     *
     * @return the created {@link Token} object instance.
     * @see #createToken(Kind, String)
     */
    private Token createToken(Kind kind) {
        return createToken(kind, "");
    }

    /**
     * Creates a new {@link Token} object with the specified {@code kind} and {@code lexeme}.
     * <p>
     * Upon calling this method, the {@link State#mode} of the parser will be reset to it's default which is {@link
     * Mode#NONE}.
     *
     * @param kind
     *         the kind of the token.
     * @param lexeme
     *         the lexeme of the token.
     *
     * @return the created {@link Token} object instance.
     */
    private Token createToken(Kind kind, String lexeme) {
        state.mode = Mode.NONE;
        return new Token(kind, range(), lexeme);
    }

    /**
     * Creates a new {@link Token} object with the specified {@code kind}, {@code range}, and {@code lexeme}.
     * <p>
     * Upon calling this method, the {@link State#mode} of the parser will be reset to it's default which is {@link
     * Mode#NONE}.
     *
     * @param kind
     *         the kind of the token.
     * @param range
     *         the range of the token.
     * @param lexeme
     *         the lexeme of the token.
     *
     * @return the created {@link Token} object instance.
     */
    private Token createToken(Kind kind, Range range, String lexeme) {
        state.mode = Mode.NONE;
        return new Token(kind, range, lexeme);
    }

    /**
     * Adds the specified {@link Token} object to the end of the current state {@link State#fallback fallback} deque.
     *
     * @param token
     *         the {@link Token token} object to add.
     */
    private void feed(Token token) {
        state.fallback.addLast(token);
    }

    /**
     * Resets the lexeme builder state.
     */
    private void resetBuilder() {
        if (state.builder.length() > 0) {
            state.builder.setLength(0);
        }
    }

    /**
     * Marks the current position as the token start position.
     */
    private void mark() {
        state.position = stream.position();
    }

    /**
     * Creates a {@link Range} object starting at the current marked position and ending at the current position.
     *
     * @return the created {@link Range} object.
     * @see #mark()
     */
    private Range range() {
        return new Range(state.position, stream.position());
    }

    /**
     * Pushes a new empty state to the stack.
     *
     * @param kind
     *         the kind of the state that will be pushed as a new state.
     */
    private void pushState(State.StateKind kind) {
        var previous = state;
        if (previous == null) {
            throw new IllegalStateException("There is currently no State object bound.");
        }
        stack.push(previous);
        state = State.emptyState(kind);
        state.position = previous.position;
    }

    /**
     * Pops the next state from the stack and binds it as the current active state.
     * <p>
     * This will completely get rid of the currently bound state.
     */
    private void popState() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("There is currently no State objects pushed to the stack.");
        }
        state = stack.pop();
    }

    /**
     * Creates and throws a parser error ranging from the marked position to the current position.
     *
     * @param message
     *         the error message of why the error has occurred.
     */
    private void throwError(String message) {
        throw new LexicalError(range(), message);
    }

    /**
     * Trims the comment from any decoration they have, whether it was the line start decoration character or it was a
     * redundant whitespace.
     *
     * @param line
     *         the comment line.
     * @param trimStar
     *         whether to trim the decorative star or not.
     *
     * @return the trimmed comment line content.
     */
    private static String trimComment(String line, boolean trimStar) {
        int start = -1;
        for (int chpos = 0; chpos < line.length(); chpos++) {
            if (!Character.isWhitespace(line.charAt(chpos))) {
                start = chpos;
                break;
            }
        }
        if (start == -1) {
            return "";
        }
        if (trimStar && line.charAt(start) == '*') {
            return trimComment(line.substring(start + 1), false);
        }
        int end = -1;
        for (int chpos = line.length() - 1; chpos >= start; chpos--) {
            if (!Character.isWhitespace(line.charAt(chpos))) {
                end = chpos + 1;
                break;
            }
        }
        return line.substring(start, end);
    }

    /**
     * Checks whether or not the specified character can be used as the identifier's starting character.
     *
     * @param ch
     *         the character to check.
     *
     * @return <code>true</code> if it can otherwise <code>false</code>.
     */
    private static boolean isIdentifierStart(char ch) {
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
    private static boolean isIdentifierPart(char ch) {
        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9' || ch == '_';
    }
}
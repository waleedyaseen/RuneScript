/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.lexer;

import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.LexicalError;
import me.waliedyassen.runescript.commons.document.LineColumn;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.commons.stream.CharStream;
import me.waliedyassen.runescript.config.lexer.token.Kind;
import me.waliedyassen.runescript.lexer.TokenizerBase;
import me.waliedyassen.runescript.lexer.table.LexicalTable;
import me.waliedyassen.runescript.lexer.token.Token;

import java.util.ArrayList;
import java.util.List;

import static me.waliedyassen.runescript.commons.stream.CharStream.NULL;
import static me.waliedyassen.runescript.config.lexer.token.Kind.*;

/**
 * Represents the RuneScript configuration lexical parser tokenizer.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class Tokenizer extends TokenizerBase {

    /**
     * The lexical symbols table.
     */
    private final LexicalTable<Kind> table;

    /**
     * The characters stream this lexer will take data from.
     */
    private final CharStream stream;

    /**
     * The current lexeme content builder.
     */
    private final StringBuilder builder = new StringBuilder();

    /**
     * A helper {@link List} of {@link String}, mainly used for multi-line comments.
     */
    private final List<String> lines = new ArrayList<>();

    /**
     * The current mode of the lexer.
     */
    private Mode mode = Mode.NONE;

    /**
     * The current character position.
     */
    private LineColumn position;

    /**
     * Attempts to parse the next rule {@link Token} from the characters stream.
     *
     * @return the parsed {@link Token} object.
     */
    public Token<Kind> parse() {
        // the character queue of the parser.
        char current, next;
        // keep parsing until we have a something to return.
        while (true) {
            // mark the current position if we have no state yet.
            if (mode == Mode.NONE) {
                mark();
            }
            // take the current and next characters from the stream.
            current = stream.take();
            next = stream.peek();
            switch (mode) {
                case NONE:
                    if (Character.isWhitespace(current)) {
                        continue;
                    } else {
                        resetBuilder();
                    }
                    if (current == NULL) {
                        return createToken(EOF);
                    } else if (isIdentifierStart(current)) {
                        builder.append(current);
                        stream.mark();
                        mode = Mode.IDENTIFIER;
                    } else if (current == '\"') {
                        mode = Mode.STRING_LITERAL;
                    } else if (Character.isDigit(current) || (current == '-' || current == '+') && Character.isDigit(next)) {
                        builder.append(current);
                        stream.mark();
                        mode = Mode.NUMBER_LITERAL;
                    } else if (current == '/' && next == '/') {
                        stream.take();
                        mode = Mode.LINE_COMMENT;
                    } else if (current == '/' && next == '*') {
                        stream.take();
                        lines.clear();
                        mode = Mode.MULTI_COMMENT;
                    } else if (table.isSeparator(current)) {
                        return createToken(table.lookupSeparator(current), Character.toString(current));
                    } else {
                        throwError("Unexpected character: " + current);
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
                            case '\\':
                                builder.append('\\');
                                break;
                            case '\"':
                                builder.append('"');
                                break;
                            case '<':
                                builder.append('<');
                                break;
                            case '>':
                                builder.append('>');
                                break;
                            default:
                                throwError("Invalid escape sequence (valid ones are  \\b  \\t  \\n  \\f  \\r  \\\"  \\\\ \\< \\>)");
                        }
                    } else if (current == '\"') {
                        return createToken(STRING, builder.toString());
                    } else {
                        builder.append(current);
                    }
                    break;
                case NUMBER_LITERAL:
                    if (Character.isDigit(current)) {
                        builder.append(current);
                        stream.mark();
                    } else if (Character.toLowerCase(current) != 'l' && isIdentifierPart(current)) {
                        builder.append(current);
                        mode = Mode.IDENTIFIER;
                        stream.mark();
                    } else {
                        var kind = INTEGER;
                        if (Character.toLowerCase(current) == 'l') {
                            kind = LONG;
                        } else if (current != NULL) {
                            stream.reset();
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
                        if (lines.size() != 0 || line.length() != 0) {
                            lines.add(line);
                        }
                        resetBuilder();
                    } else if (current == '*' && next == '/') {
                        var line = trimComment(builder.toString(), true);
                        if (line.length() > 0) {
                            lines.add(line);
                        }
                        resetBuilder();
                        stream.take();
                        return createToken(COMMENT, String.join("\n", lines));
                    } else {
                        builder.append(current);
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
     */
    private Token<Kind> createToken(Kind kind) {
        return createToken(kind, "");
    }

    /**
     * Creates a new {@link Token} object with the specified {@code kind} and {@code lexeme}.
     *
     * @param kind
     *         the kind of the token.
     * @param lexeme
     *         the lexeme of the token.
     *
     * @return the created {@link Token} object instance.
     */
    private Token<Kind> createToken(Kind kind, String lexeme) {
        mode = Mode.NONE;
        return new Token<>(kind, range(), lexeme);
    }

    /**
     * Resets the lexeme builder state.
     */
    private void resetBuilder() {
        if (builder.length() > 0) {
            builder.setLength(0);
        }
    }

    /**
     * Marks the current position as the token start position.
     */
    private void mark() {
        position = stream.position();
    }

    /**
     * Creates a {@link Range} object starting at the marked start position and ending at the current position.
     *
     * @return the created {@link Range} object.
     * @see #mark()
     */
    private Range range() {
        return new Range(position, stream.position());
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
        var start = 0;
        for (var chpos = 0; chpos < line.length(); chpos++) {
            if (!Character.isWhitespace(line.charAt(chpos))) {
                start = chpos;
                break;
            }
        }
        if (trimStar && line.charAt(start) == '*') {
            return trimComment(line.substring(start + 1), false);
        }
        var end = -1;
        for (var chpos = line.length() - 1; chpos >= start; chpos--) {
            if (!Character.isWhitespace(line.charAt(chpos))) {
                end = chpos + 1;
                break;
            }
        }
        return line.substring(start, end);
    }

    /**
     * Represents the lexical parser parsing mode.
     *
     * @author Walied K. Yassen
     */
    enum Mode {

        /**
         * We are currently in the default state, no mode defined.
         */
        NONE,

        /**
         * Indicates that the parser is currently parsing an identifier.
         */
        IDENTIFIER,

        /**
         * Indicates that the parser is currently parsing a string literal.
         */
        STRING_LITERAL,

        /**
         * Indicates that the parser is currently parsing a number literal.
         */
        NUMBER_LITERAL,

        /**
         * Indicates that the parser is currently parsing a line comment.
         */
        LINE_COMMENT,

        /**
         * Indicates that the parser is currently parsing a multi-line comment.
         */
        MULTI_COMMENT
    }
}

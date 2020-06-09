/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor.code.tokenMaker;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.symbol.SymbolTable;
import me.waliedyassen.runescript.compiler.lexer.TokenizerBase;
import me.waliedyassen.runescript.compiler.lexer.table.LexicalTable;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

import javax.swing.text.Segment;
import java.util.Stack;

import static me.waliedyassen.runescript.editor.ui.editor.code.tokenMaker.CodeTokens.*;

/**
 * Represents the RuneScript language code token maker.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class CodeTokenMaker extends AbstractTokenMaker {

    // TODO(Walied): Clean this up a bit.

    /**
     * The temporary context token stack.
     */
    private final Stack<TemporaryToken> temporaryTokens = new Stack<>();

    /**
     * The lexical table to use for keyword and separators.
     */
    private final LexicalTable<Kind> lexicalTable;

    /**
     * The symbol table to use for commands and potentially scripts.
     */
    private final SymbolTable symbolTable;

    /**
     * The tokens current offset.
     */
    private int offset;

    /**
     * {@inheritDoc}
     */
    @Override
    public Token getTokenList(Segment text, int initialTokenType, int startOffset) {
        resetTokenList();
        var chs = text.array;
        var pos = text.offset;
        var end = pos + text.count;
        temporaryTokens.clear();
        offset = startOffset - pos;
        pushToken(initialTokenType, pos);
        for (; pos < end; pos++) {
            var ch = chs[pos];
            var next = pos < end - 1 ? chs[pos + 1] : '\0';
            var tokenType = temporaryTokens.isEmpty() ? NULL : temporaryTokens.peek().tokenType;

            switch (tokenType) {
                case NULL:
                case STRING_INTERPOLATE:
                    if (tokenType == STRING_INTERPOLATE && ch == '>') {
                        changeTokenStart(pos);
                        popAddToken(text, pos);
                        changeTokenStart(pos + 1);
                    } else if (Character.isWhitespace(ch)) {
                        pushToken(WHITESPACE, pos);
                    } else if (ch == '[') {
                        pushToken(DECLARATION, pos);
                    } else if (ch == '\"') {
                        pushToken(STRING_LITERAL, pos);
                    } else if (ch == '0' && next == 'x') {
                        pushToken(HEX_LITERAL, pos++);
                    } else if (Character.isDigit(ch) || (ch == '-' || ch == '+') && Character.isDigit(next)) {
                        pushToken(NUMBER_LITERAL, pos);
                    } else if (ch == '$' && TokenizerBase.isIdentifierStart(next)) {
                        pushToken(LOCAL_VARIABLE, pos);
                    } else if (ch == '%' && TokenizerBase.isIdentifierStart(next)) {
                        pushToken(GLOBAL_VARIABLE, pos);
                    } else if (ch == '^' && TokenizerBase.isIdentifierStart(next)) {
                        pushToken(CONSTANT, pos);
                    } else if (ch == '/' && next == '/') {
                        pushToken(LINE_COMMENT, pos);
                    } else if (ch == '/' && next == '*') {
                        pushToken(MULTILINE_COMMENT, pos);
                    } else if (ch == '.') {
                        pushToken(COMMAND, pos);
                    } else if (TokenizerBase.isIdentifierStart(ch)) {
                        pushToken(IDENTIFIER, pos);
                    } else if (lexicalTable.isSeparator(ch)) {
                        addToken(text, pos, pos, SEPARATOR);
                    } else {
                        if (lexicalTable.isOperatorStart(ch)) {
                            var size = 1;
                            while (pos + size < end) {
                                var opText = new String(chs, pos, size + 1);
                                if (lexicalTable.isOperator(opText)) {
                                    size++;
                                } else {
                                    break;
                                }
                            }
                            size--;
                            addToken(text, pos, pos + size, OPERATOR);
                            pos += size;
                        } else {
                            addToken(text, pos, pos, UNDEFINED);
                        }
                    }
                    break;
                case WHITESPACE:
                    if (!Character.isWhitespace(ch)) {
                        pos--;
                        popAddToken(text, pos);
                    }
                    break;
                case DECLARATION:
                    if (ch == ']') {
                        popAddToken(text, pos);
                    }
                    break;
                case STRING_LITERAL:
                    if (ch == '\"') {
                        popAddToken(text, pos);
                    } else if (ch == '<') {
                        addToken(text, pos - 1);
                        pushToken(STRING_INTERPOLATE, pos);
                        addToken(text, pos);
                    }
                    break;
                case HEX_LITERAL:
                    if (!Character.isDigit(ch) && (ch < 'a' || ch > 'f') && (ch < 'A' || ch > 'F')) {
                        if (ch != 'L' && ch != 'l') {
                            pos--;
                        }
                        popAddToken(text, pos);
                    }
                    break;
                case NUMBER_LITERAL:
                case COORDGRID_LITERAL:
                    if (tokenType == NUMBER_LITERAL && ch == '_') {
                        changeTokenType(COORDGRID_LITERAL);
                    } else if (!Character.isDigit(ch) && (tokenType != COORDGRID_LITERAL || ch != '_')) {
                        if (ch != 'L' && ch != 'l') {
                            pos--;
                        }
                        popAddToken(text, pos);
                    }
                    break;
                case LOCAL_VARIABLE:
                case GLOBAL_VARIABLE:
                case CONSTANT:
                case COMMAND:
                    if (!TokenizerBase.isIdentifierPart(ch)) {
                        pos--;
                        popAddToken(text, pos);
                    }
                    break;
                case LINE_COMMENT:
                    // NOOP
                    break;
                case MULTILINE_COMMENT:
                    if (ch == '*' && next == '/') {
                        popAddToken(text, ++pos);
                    }
                    break;
                case IDENTIFIER:
                    if (!TokenizerBase.isIdentifierPart(ch) || next == '\0') {
                        if (!TokenizerBase.isIdentifierPart(ch)) {
                            pos--;
                        }
                        var currentToken = temporaryTokens.peek();
                        var identifierText = new String(chs, currentToken.start, pos - currentToken.start + 1);
                        if (symbolTable.lookupCommand(identifierText) != null) {
                            changeTokenType(COMMAND);
                        } else {
                            var keywordKind = lexicalTable.getKeywords().get(identifierText);
                            if (keywordKind != null) {
                                if (keywordKind == Kind.TYPE || keywordKind == Kind.DEFINE) {
                                    changeTokenType(TYPE_NAME);
                                } else {
                                    changeTokenType(KEYWORD);
                                }
                            }
                        }
                        popAddToken(text, pos);
                    }
                    break;
            }
        }
        var appendNullToken = true;
        if (!temporaryTokens.isEmpty()) {
            var token = temporaryTokens.peek();
            if (token.tokenType != STRING_INTERPOLATE) {
                popAddToken(text, pos - 1);
            }
            if (token.tokenType == MULTILINE_COMMENT) {
                appendNullToken = false;
            }
        }
        if (appendNullToken) {
            addNullToken();
        }
        return firstToken;
    }


    /**
     * Pushes a temporary token context into the temporary token contexts stack.
     *
     * @param type  the type of the token that we want to push.
     * @param start the start position of the token that we want to push.
     */
    private void pushToken(int type, int start) {
        temporaryTokens.push(new TemporaryToken(type, start));
    }

    /**
     * Changes the type of the token that is currently at the top of the temporary token contexts stack.
     *
     * @param type the new token type to set for the token.
     */
    private void changeTokenType(int type) {
        var tokenType = temporaryTokens.pop();
        pushToken(type, tokenType.start);
    }

    /**
     * Changes the start position of the token that is currently at the top of the temporary token contexts stack.
     *
     * @param start the new token start position to set for the token.
     */
    private void changeTokenStart(int start) {
        var tokenType = temporaryTokens.pop();
        pushToken(tokenType.tokenType, start);
    }

    /**
     * Adds a new token to the token map while removing the token context from the stack.
     *
     * @param text the text which we want to grab the token content from.
     * @param end  the end of the token text in the document.
     */
    public void popAddToken(Segment text, int end) {
        var temporaryToken = temporaryTokens.pop();
        addToken(text, temporaryToken.start, end, temporaryToken.tokenType, this.offset + temporaryToken.start);
    }

    /**
     * Adds a new token to the token map without removing the token context from the stack.
     *
     * @param text the text which we want to grab the token content from.
     * @param end  the end of the token text in the document.
     */
    public void addToken(Segment text, int end) {
        var temporaryToken = temporaryTokens.peek();
        addToken(text, temporaryToken.start, end, temporaryToken.tokenType, this.offset + temporaryToken.start);
    }

    /**
     * Adds a new token without altering the temporary token contexts stack.
     *
     * @param text       the the text which we want to grab the token content from.
     * @param tokenStart the start offset the token text.
     * @param tokenEnd   the end offset of the token text.
     * @param tokenType  the type of the token we want to add.
     */
    public void addToken(Segment text, int tokenStart, int tokenEnd, int tokenType) {
        addToken(text, tokenStart, tokenEnd, tokenType, this.offset + tokenStart);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TokenMap getWordsToHighlight() {
        return null;
    }

    /**
     * A data class to help parsing the complex code, it provides information about what token context
     * we are currently parsing.
     *
     * @author Walied K. Yassen
     */
    @Data
    private final static class TemporaryToken {

        /**
         * The context token type.
         */
        public final int tokenType;

        /**
         * The context token start offset.
         */
        public final int start;
    }
}

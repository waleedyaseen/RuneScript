/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor.code.tokenMaker;

import org.fife.ui.rsyntaxtextarea.TokenTypes;

/**
 * Holds all of the RuneScript code tokens that we are using for syntax highlighting and other stuff.
 *
 * @author Walied K. Yassen
 */
public interface CodeTokens {
    int NULL = TokenTypes.NULL;
    int WHITESPACE = TokenTypes.WHITESPACE;
    int STRING_LITERAL = TokenTypes.LITERAL_STRING_DOUBLE_QUOTE;
    int LINE_COMMENT = TokenTypes.COMMENT_EOL;
    int MULTILINE_COMMENT = TokenTypes.COMMENT_MULTILINE;
    int NUMBER_LITERAL = TokenTypes.LITERAL_NUMBER_DECIMAL_INT;
    int LOCAL_VARIABLE = TokenTypes.VARIABLE;
    int IDENTIFIER = TokenTypes.IDENTIFIER;
    int KEYWORD = TokenTypes.RESERVED_WORD;
    int TYPE_NAME = TokenTypes.RESERVED_WORD_2;
    int UNDEFINED = TokenTypes.DEFAULT_NUM_TOKEN_TYPES;
    int SEPARATOR = TokenTypes.SEPARATOR;
    int OPERATOR = TokenTypes.OPERATOR;
    int DECLARATION = UNDEFINED + 1;
    int STRING_INTERPOLATE = DECLARATION + 1;
    int COORDGRID_LITERAL = STRING_INTERPOLATE + 1;
    int GLOBAL_VARIABLE = COORDGRID_LITERAL + 1;
    int CONSTANT = GLOBAL_VARIABLE + 1;
    int COMMAND = CONSTANT + 1;
    int NUM_TOKENS = COMMAND + 1;
}

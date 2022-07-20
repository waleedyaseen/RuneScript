/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax;

import me.waliedyassen.runescript.commons.document.Span;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.lexer.token.Token;

/**
 * A token that is used in the Syntax Tree.
 *
 * @author Walied K. Yassen
 */
public final class SyntaxToken extends Token<Kind> {

    /**
     * Constructs a new {@link SyntaxToken} type object instance.
     *
     * @param kind   the kind of the token.
     * @param span  the source code range of the token.
     * @param lexeme the raw text representation of the token.
     */
    public SyntaxToken(Kind kind, Span span, String lexeme) {
        super(kind, span, lexeme);
    }
}

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.expr.literal;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Span;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.lexer.token.Token;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * The Syntax Tree element for the null literal syntax.
 *
 * @author Walied K. Yassen
 */
public final class LiteralNullSyntax extends LiteralExpressionSyntax<Object> {

    /**
     * The word token of the null keyword.
     */
    @Getter
    private final Token<Kind> wordToken;

    /**
     * Constructs a new {@link LiteralNullSyntax} type object instance.
     *
     * @param span     the expression source code range.
     * @param wordToken the word token of the null keyword.
     */
    public LiteralNullSyntax(Span span, Token<Kind> wordToken) {
        super(span, null);
        this.wordToken = wordToken;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

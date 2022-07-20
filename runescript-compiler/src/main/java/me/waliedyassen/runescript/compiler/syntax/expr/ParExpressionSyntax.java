/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.expr;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Span;
import me.waliedyassen.runescript.compiler.syntax.SyntaxToken;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * Represents an expression surrounded by parenthesis.
 *
 * @author Walied K. Yassen
 */
public final class ParExpressionSyntax extends ExpressionSyntax {

    /**
     * The token of the left parenthesis.
     */
    @Getter
    private final SyntaxToken leftParenToken;

    /**
     * The token of the right parenthesis.
     */
    @Getter
    private final SyntaxToken rightParenToken;

    /**
     * The expression within the parenthesis.
     */
    @Getter
    private final ExpressionSyntax expression;

    /**
     * Constructs a new {@link ParExpressionSyntax} type object instance.
     *
     * @param span           the node source code range.
     * @param leftParenToken  the token of the left parenthesis.
     * @param rightParenToken the token of the right parenthesis.
     * @param expression      the expression within the parenthesis.
     */
    public ParExpressionSyntax(Span span, SyntaxToken leftParenToken, SyntaxToken rightParenToken, ExpressionSyntax expression) {
        super(span);
        this.leftParenToken = leftParenToken;
        this.rightParenToken = rightParenToken;
        this.expression = addChild(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.stmt;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.syntax.expr.ExpressionSyntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * Represents an expression statement.
 *
 * @author Walied K. Yassen
 */
public final class ExpressionStatementSyntax extends StatementSyntax {

    /**
     * the expression of the statement.
     */
    @Getter
    private final ExpressionSyntax expression;

    /**
     * Construct a new {@link StatementSyntax} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param expression
     *         the statement expression.
     */
    public ExpressionStatementSyntax(Range range, ExpressionSyntax expression) {
        super(range);
        this.expression = addChild(expression);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <E, S> S accept(SyntaxVisitor<E, S> visitor) {
        return visitor.visit(this);
    }
}

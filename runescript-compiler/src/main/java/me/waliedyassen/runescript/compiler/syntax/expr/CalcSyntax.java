/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.expr;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * Represents an arithmetic calculate expression.
 *
 * @author Walied K. Yassen
 */
public final class CalcSyntax extends ExpressionSyntax {

    /**
     * The expression of the calc.
     */
    @Getter
    private final ExpressionSyntax expression;

    /**
     * Constructs a new {@link ExpressionSyntax} type object instance.
     *
     * @param range
     *         the expression source code range.
     */
    public CalcSyntax(Range range, ExpressionSyntax expression) {
        super(range);
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

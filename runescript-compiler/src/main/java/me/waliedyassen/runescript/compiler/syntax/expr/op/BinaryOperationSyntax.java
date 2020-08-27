/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.expr.op;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.syntax.expr.ExpressionSyntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;
import me.waliedyassen.runescript.compiler.util.Operator;

/**
 * Represents a binary operation expression.
 *
 * @author Walied K. Yassen
 */
public final class BinaryOperationSyntax extends ExpressionSyntax {

    /**
     * The operation left side expression.
     */
    @Getter
    private final ExpressionSyntax left;

    /**
     * The operation operator.
     */
    @Getter
    private final Operator operator;

    /**
     * The operation right side expression.
     */
    @Getter
    private final ExpressionSyntax right;

    /**
     * Constructs a new {@link ExpressionSyntax} type object instance.
     *
     * @param left
     *         the operation left side expression.
     * @param operator
     *         the operation binary operator type.
     * @param right
     *         the operation right side expression.
     */
    public BinaryOperationSyntax(ExpressionSyntax left, Operator operator, ExpressionSyntax right) {
        super(new Range(left.getRange().getStart(), right.getRange().getEnd()));
        this.left = addChild(left);
        this.operator = operator;
        this.right = addChild(right);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

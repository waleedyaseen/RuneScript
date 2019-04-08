/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.expr;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.util.Operator;

/**
 * Represents a binary operator expression.
 *
 * @author Walied K. Yassen
 */
public final class AstBinaryExpression extends AstExpression {

    /**
     * The left side expression.
     */
    @Getter
    private final AstExpression left;

    /**
     * The binary operator type.
     */
    @Getter
    private final Operator operator;

    /**
     * The right side expression.
     */
    @Getter
    private final AstExpression right;

    /**
     * Constructs a new {@link AstExpression} type object instance.
     *
     * @param left
     *         the left side expression.
     * @param operator
     *         the binary operator type.
     * @param right
     *         the right side expression.
     */
    public AstBinaryExpression(AstExpression left, Operator operator, AstExpression right) {
        super(new Range(left.getRange().getStart(), right.getRange().getEnd()));
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
}

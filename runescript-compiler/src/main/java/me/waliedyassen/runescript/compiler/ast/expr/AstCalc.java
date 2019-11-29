/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.expr;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;

/**
 * Represents an arithmetic calculate expression.
 *
 * @author Walied K. Yassen
 */
public final class AstCalc extends AstExpression {

    /**
     * The expression of the calc.
     */
    @Getter
    private final AstExpression expression;

    /**
     * Constructs a new {@link AstExpression} type object instance.
     *
     * @param range
     *         the expression source code range.
     */
    public AstCalc(Range range, AstExpression expression) {
        super(range);
        this.expression = addChild(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E, S> E accept(AstVisitor<E, S> visitor) {
        return visitor.visit(this);
    }
}

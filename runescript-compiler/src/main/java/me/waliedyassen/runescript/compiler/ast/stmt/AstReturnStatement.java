/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.stmt;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;
import me.waliedyassen.runescript.compiler.ast.expr.AstExpression;

/**
 * Represents a return expression statement.
 *
 * @author Walied K. Yassen
 */
public final class AstReturnStatement extends AstStatement {

    /**
     * The returned expressions.
     */
    @Getter
    private final AstExpression[] expressions;

    /**
     * Construct a new {@link AstReturnStatement} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param expressions
     *         the returned expressions.
     */
    public AstReturnStatement(Range range, AstExpression[] expressions) {
        super(range);
        this.expressions = expressions;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <E, S> S accept(AstVisitor<E, S> visitor) {
        return visitor.visit(this);
    }
}

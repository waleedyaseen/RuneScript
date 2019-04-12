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
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;

/**
 * Represents an interpolated string concatenation node.
 *
 * @author Walied K. Yassen
 */
public final class AstConcatenation extends AstExpression {

    /**
     * The expressions of the concatenation.
     */
    @Getter
    private final AstExpression[] expressions;

    /**
     * Constructs a new {@link AstConcatenation} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param expressions
     *         the expressions of the concatenation.
     */
    public AstConcatenation(Range range, AstExpression[] expressions) {
        super(range);
        this.expressions = expressions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

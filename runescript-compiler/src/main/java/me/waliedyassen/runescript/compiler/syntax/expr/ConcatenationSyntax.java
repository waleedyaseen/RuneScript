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
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * Represents an interpolated string concatenation node.
 *
 * @author Walied K. Yassen
 */
public final class ConcatenationSyntax extends ExpressionSyntax {

    /**
     * The expressions of the concatenation.
     */
    @Getter
    private final ExpressionSyntax[] expressions;

    /**
     * Constructs a new {@link ConcatenationSyntax} type object instance.
     *
     * @param span
     *         the node source code range.
     * @param expressions
     *         the expressions of the concatenation.
     */
    public ConcatenationSyntax(Span span, ExpressionSyntax[] expressions) {
        super(span);
        this.expressions = addChild(expressions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

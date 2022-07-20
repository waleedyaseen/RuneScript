/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.expr.literal;

import me.waliedyassen.runescript.commons.document.Span;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * Represents a boolean literal expression node.
 *
 * @author Walied K. Yassen
 */
public final class LiteralBooleanSyntax extends LiteralExpressionSyntax<Boolean> {

    /**
     * Construct a new {@link LiteralBooleanSyntax} type object instance.
     *
     * @param span the node source code range.
     * @param value the boolean literal value
     */
    public LiteralBooleanSyntax(Span span, Boolean value) {
        super(span, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

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
 * Represents a long integer literal expression node.
 *
 * @author Walied K. Yassen
 */
public final class LiteralLongSyntax extends LiteralNumberSyntax<Long> {

    /**
     * Constructs a new {@link LiteralLongSyntax} type object instance.
     *
     * @param span the node source code range.
     * @param value the value of the literal.
     */
    public LiteralLongSyntax(Span span, Long value) {
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

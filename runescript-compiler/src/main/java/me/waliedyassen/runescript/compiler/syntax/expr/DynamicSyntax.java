/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.expr;

import lombok.Getter;
import lombok.ToString;
import me.waliedyassen.runescript.commons.document.Span;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * Represents a dynamic expression that will be resolved at a latter phase in compiling.
 *
 * @author Walied K. Yassen
 */
@ToString
public final class DynamicSyntax extends ExpressionSyntax {

    /**
     * The dynamic name.
     */
    @Getter
    private final IdentifierSyntax name;

    /**
     * Constructs a new {@link ExpressionSyntax} type object instance.
     *
     * @param span
     *         the expression source code range.
     * @param name
     *         the dynamic name.
     */
    public DynamicSyntax(Span span, IdentifierSyntax name) {
        super(span);
        this.name = addChild(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

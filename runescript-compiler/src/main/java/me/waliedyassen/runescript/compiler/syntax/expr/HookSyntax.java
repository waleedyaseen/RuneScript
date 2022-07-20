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
 * Represents a hook expression.
 *
 * @author Walied K. Yassen
 */
public final class HookSyntax extends ExpressionSyntax {

    /**
     * The name of the hook.
     */
    @Getter
    private final IdentifierSyntax name;

    /**
     * The arguments of the hook.
     */
    @Getter
    private final ExpressionSyntax[] arguments;

    /**
     * The transmits of the hook.
     */
    @Getter
    private final ExpressionSyntax[] transmits;

    /**
     * Constructs a new {@link HookSyntax} type object instance.
     *
     * @param span
     *         the expression source code range.
     * @param name
     *         the name of the hook.
     * @param arguments
     *         the arguments of the hook.
     * @param transmits
     *         the transmits of the hook.
     */
    public HookSyntax(Span span, IdentifierSyntax name, ExpressionSyntax[] arguments, ExpressionSyntax[] transmits) {
        super(span);
        this.name = name != null ? addChild(name) : null;
        this.arguments = arguments != null ? addChild(arguments) : null;
        this.transmits = transmits != null ? addChild(transmits) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

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
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;
import me.waliedyassen.runescript.compiler.util.VariableScope;

/**
 * Represents an AST variable expression.
 *
 * @author Walied K. Yassen
 */
@ToString
public final class VariableExpressionSyntax extends ExpressionSyntax {

    /**
     * The scope of the variable.
     */
    @Getter
    private final VariableScope scope;

    /**
     * The name of the variable.
     */
    @Getter
    private final IdentifierSyntax name;

    /**
     * Constructs a new {@link VariableExpressionSyntax} type object instance.
     *
     * @param range
     *         the expression source code range.
     * @param scope
     *         the scope of the variable.
     * @param name
     *         the name of the variable.
     */
    public VariableExpressionSyntax(Range range, VariableScope scope, IdentifierSyntax name) {
        super(range);
        this.scope = scope;
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

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.stmt;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.syntax.expr.ExpressionSyntax;
import me.waliedyassen.runescript.compiler.syntax.expr.VariableSyntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * Represents a variable initialize statement.
 *
 * @author Walied K. Yassen
 */
public final class VariableInitializerSyntax extends StatementSyntax {

    /**
     * The scope of the variable.
     */
    @Getter
    private final VariableSyntax[] variables;

    /**
     * The initialise expression of the variable.
     */
    @Getter
    private final ExpressionSyntax[] expressions;

    /**
     * Constructs a new {@link VariableInitializerSyntax} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param variables
     *         the variables that are being initialized.
     * @param expressions
     *         the expressions the variables being initialized with.
     */
    public VariableInitializerSyntax(Range range, VariableSyntax[] variables, ExpressionSyntax[] expressions) {
        super(range);
        this.variables = addChild(variables);
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

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
import me.waliedyassen.runescript.compiler.syntax.expr.IdentifierSyntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;
import me.waliedyassen.runescript.type.Type;

/**
 * Represents a variable define (or declaration) statement.
 *
 * @author Walied K. Yassen
 */
public final class VariableDeclarationSyntax extends StatementSyntax {

    /**
     * The variable type.
     */
    @Getter
    private final Type type;

    /**
     * The variable name.
     */
    @Getter
    private final IdentifierSyntax name;

    /**
     * The variable initializer expression.
     */
    @Getter
    private final ExpressionSyntax expression;

    /**
     * Construct a new {@link VariableDeclarationSyntax} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param type
     *         the type of the variable.
     * @param name
     *         the name of the variable.
     * @param expression
     *         the initializer expression of the variable.
     */
    public VariableDeclarationSyntax(Range range, Type type, IdentifierSyntax name, ExpressionSyntax expression) {
        super(range);
        this.type = type;
        this.name = addChild(name);
        this.expression = expression != null ? addChild(expression) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E, S> S accept(SyntaxVisitor<E, S> visitor) {
        return visitor.visit(this);
    }
}

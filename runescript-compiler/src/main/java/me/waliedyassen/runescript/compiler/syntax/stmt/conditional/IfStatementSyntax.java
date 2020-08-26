/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.stmt.conditional;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.syntax.expr.ExpressionSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.StatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * Represents an if-statement in the Abstract Syntax Tree.
 *
 * @author Walied K. Yassen
 */
public final class IfStatementSyntax extends StatementSyntax {

    /**
     * The if statement condition expression.
     */
    @Getter
    private final ExpressionSyntax condition;

    /**
     * The if true code statement.
     */
    @Getter
    private final StatementSyntax trueStatement;

    /**
     * The if false code statement.
     */
    @Getter
    private final StatementSyntax falseStatement;

    /**
     * Construct a new {@link IfStatementSyntax} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param condition
     *         the condition of the if statement.
     * @param trueStatement
     *         the true code statement of the if statement.
     * @param falseStatement
     *         the false code statement of the if statement.
     */
    public IfStatementSyntax(Range range, ExpressionSyntax condition, StatementSyntax trueStatement, StatementSyntax falseStatement) {
        super(range);
        this.condition = addChild(condition);
        this.trueStatement = addChild(trueStatement);
        this.falseStatement = falseStatement != null ? addChild(falseStatement) : null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <E, S> S accept(SyntaxVisitor<E, S> visitor) {
        return visitor.visit(this);
    }
}

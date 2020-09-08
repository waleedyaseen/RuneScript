/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.stmt.loop;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.syntax.expr.ExpressionSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.StatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * The Syntax Tree element for the do-while loop statement syntax.
 *
 * @author Walied K. Yassen
 */
public final class DoWhileStatementSyntax extends StatementSyntax {

    /**
     * The code statement of the while loop.
     */
    @Getter
    private final StatementSyntax code;

    /**
     * The condition of the while loop.
     */
    @Getter
    private final ExpressionSyntax condition;

    /**
     * Construct a new {@link DoWhileStatementSyntax} type object instance.
     *
     * @param range     the node source code range.
     * @param code      the code of the  do while statement.
     * @param condition the condition of the do while statement.
     */
    public DoWhileStatementSyntax(Range range, StatementSyntax code, ExpressionSyntax condition) {
        super(range);
        this.code = addChild(code);
        this.condition = addChild(condition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

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
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;
import me.waliedyassen.runescript.type.PrimitiveType;

/**
 * Represents an AST switch statement.
 *
 * @author Walied K. Yassen
 */
public final class SwitchStatementSyntax extends StatementSyntax {

    /**
     * The type of the switch statement.
     */
    @Getter
    private final PrimitiveType type;

    /**
     * The condition of the switch statement.
     */
    @Getter
    private final ExpressionSyntax condition;

    /**
     * The member cases of the switch statement.
     */
    @Getter
    private final SwitchCaseSyntax[] cases;

    /**
     * The default case of the swich statement.
     */
    @Getter
    private final SwitchCaseSyntax defaultCase;

    /**
     * Constructs a new {@link SwitchStatementSyntax} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param type
     *         the switch statement type.
     * @param condition
     *         the switch statement condition.
     * @param cases
     *         the switch statement member cases
     * @param defaultCase
     *         the switch statement default case
     */
    public SwitchStatementSyntax(Range range, PrimitiveType type, ExpressionSyntax condition, SwitchCaseSyntax[] cases, SwitchCaseSyntax defaultCase) {
        super(range);
        this.type = type;
        this.condition = addChild(condition);
        this.cases = addChild(cases);
        this.defaultCase = defaultCase != null ? addChild(defaultCase) : null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }
}


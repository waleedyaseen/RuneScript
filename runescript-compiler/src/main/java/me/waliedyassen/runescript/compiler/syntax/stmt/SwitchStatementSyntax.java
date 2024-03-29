/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.stmt;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Span;
import me.waliedyassen.runescript.compiler.syntax.SyntaxToken;
import me.waliedyassen.runescript.compiler.syntax.expr.ExpressionSyntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * Represents an AST switch statement.
 *
 * @author Walied K. Yassen
 */
public final class SwitchStatementSyntax extends StatementSyntax {

    /**
     * The token of the switch keyword.
     */
    @Getter
    private final SyntaxToken switchToken;

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
     * The default case of the switch statement.
     */
    @Getter
    private final SwitchCaseSyntax defaultCase;

    /**
     * Constructs a new {@link SwitchStatementSyntax} type object instance.
     *
     * @param span       the node source code range.
     * @param switchToken the token of the "switch" keyword.
     * @param condition   the switch statement condition.
     * @param cases       the switch statement member cases
     * @param defaultCase the switch statement default case
     */
    public SwitchStatementSyntax(Span span, SyntaxToken switchToken, ExpressionSyntax condition, SwitchCaseSyntax[] cases, SwitchCaseSyntax defaultCase) {
        super(span);
        this.switchToken = switchToken;
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


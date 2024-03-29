/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.stmt.loop;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Span;
import me.waliedyassen.runescript.compiler.syntax.SyntaxToken;
import me.waliedyassen.runescript.compiler.syntax.expr.ExpressionSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.StatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * Represents a while-statement in the Abstract Syntax Tree.
 *
 * @author Walied K. Yassen
 */
public final class WhileStatementSyntax extends StatementSyntax {

    /**
     * The token of the "while" keyword.
     */
    @Getter
    private final SyntaxToken whileToken;

    /**
     * The token of the left parenthesis.
     */
    @Getter
    private final SyntaxToken leftParenToken;

    /**
     * The token of the right parenthesis.
     */
    @Getter
    private final SyntaxToken rightParenToken;

    /**
     * The condition of the while loop.
     */
    @Getter
    private final ExpressionSyntax condition;

    /**
     * The code statement of the while loop.
     */
    @Getter
    private final StatementSyntax code;

    /**
     * Construct a new {@link StatementSyntax} type object instance.
     *
     * @param span           the node source code range.
     * @param whileToken      the token of the "while" keyword.
     * @param leftParenToken  the token of the left parenthesis.
     * @param rightParenToken the token of the right parenthesis.
     * @param condition       the condition of the while loop.
     * @param code            the code statement of the while loop.
     */
    public WhileStatementSyntax(Span span, SyntaxToken whileToken, SyntaxToken leftParenToken, SyntaxToken rightParenToken, ExpressionSyntax condition, StatementSyntax code) {
        super(span);
        this.whileToken = whileToken;
        this.leftParenToken = leftParenToken;
        this.rightParenToken = rightParenToken;
        this.condition = addChild(condition);
        this.code = addChild(code);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

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
import me.waliedyassen.runescript.compiler.syntax.SyntaxToken;
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
     * The token of the "if" keyword.
     */
    @Getter
    private final SyntaxToken ifToken;

    /**
     * The token of the "else" keyword.
     */
    @Getter
    private final SyntaxToken elseToken;

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
     * @param range           the node source code range.
     * @param ifToken         if token of the "if" keyword.
     * @param leftParenToken  the token of the left parenthesis.
     * @param rightParenToken the token of the right parenthesis.
     * @param elseToken       the toke nof the "else" keyword.
     * @param condition       the condition of the if statement.
     * @param trueStatement   the true code statement of the if statement.
     * @param falseStatement  the false code statement of the if statement.
     */
    public IfStatementSyntax(Range range, SyntaxToken ifToken, SyntaxToken leftParenToken, SyntaxToken rightParenToken, SyntaxToken elseToken, ExpressionSyntax condition, StatementSyntax trueStatement, StatementSyntax falseStatement) {
        super(range);
        this.ifToken = ifToken;
        this.elseToken = elseToken;
        this.leftParenToken = leftParenToken;
        this.rightParenToken = rightParenToken;
        this.condition = addChild(condition);
        this.trueStatement = addChild(trueStatement);
        this.falseStatement = falseStatement != null ? addChild(falseStatement) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

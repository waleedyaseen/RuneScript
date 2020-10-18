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
import me.waliedyassen.runescript.compiler.syntax.SyntaxToken;
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
     * The toke nof the "do" keyword.
     */
    private final SyntaxToken doToken;

    /**
     * The token of the "while" keyword.
     */
    private final SyntaxToken whileToken;

    /**
     * The token of the semicolon.
     */
    private final SyntaxToken semicolonToken;

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
     * @param range          the node source code range.
     * @param doToken        the token of the "do" keyword.
     * @param whileToken     the token of the "while" keyword.
     * @param semicolonToken the token of the semicolon.
     * @param code           the code of the  do while statement.
     * @param condition      the condition of the do while statement.
     */
    public DoWhileStatementSyntax(Range range, SyntaxToken doToken, SyntaxToken whileToken, SyntaxToken semicolonToken, StatementSyntax code, ExpressionSyntax condition) {
        super(range);
        this.doToken = doToken;
        this.whileToken = whileToken;
        this.semicolonToken = semicolonToken;
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

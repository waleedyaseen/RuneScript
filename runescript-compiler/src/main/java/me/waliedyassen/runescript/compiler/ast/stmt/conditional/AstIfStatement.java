/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.stmt.conditional;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;
import me.waliedyassen.runescript.compiler.ast.expr.AstExpression;
import me.waliedyassen.runescript.compiler.ast.stmt.AstStatement;

/**
 * Represents an if-statement in the Abstract Syntax Tree.
 *
 * @author Walied K. Yassen
 */
public final class AstIfStatement extends AstStatement {

    /**
     * The if statement condition expression.
     */
    @Getter
    private final AstExpression condition;

    /**
     * The if true code statement.
     */
    @Getter
    private final AstStatement trueStatement;

    /**
     * The if false code statement.
     */
    @Getter
    private final AstStatement falseStatement;

    /**
     * Construct a new {@link AstIfStatement} type object instance.
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
    public AstIfStatement(Range range, AstExpression condition, AstStatement trueStatement, AstStatement falseStatement) {
        super(range);
        this.condition = condition;
        this.trueStatement = trueStatement;
        this.falseStatement = falseStatement;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(AstVisitor<?> visitor) {
        visitor.visit(this);
    }
}

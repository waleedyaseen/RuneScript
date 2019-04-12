/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
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
 * Represents a while-statement in the Abstract Syntax Tree.
 *
 * @author Walied K. Yassen
 */
public final class AstWhileStatement extends AstStatement {

    /**
     * The condition of the while loop.
     */
    @Getter
    private final AstExpression condition;

    /**
     * The code statement of the while loop.
     */
    @Getter
    private final AstStatement code;

    /**
     * Construct a new {@link AstStatement} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param condition
     *         the condition of the while loop.
     * @param code
     *         the code statement of the while loop.
     */
    public AstWhileStatement(Range range, AstExpression condition, AstStatement code) {
        super(range);
        this.condition = condition;
        this.code = code;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

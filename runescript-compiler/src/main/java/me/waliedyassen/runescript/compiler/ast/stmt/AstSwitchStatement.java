/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.stmt;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;
import me.waliedyassen.runescript.compiler.ast.expr.AstExpression;
import me.waliedyassen.runescript.type.PrimitiveType;

/**
 * Represents an AST switch statement.
 *
 * @author Walied K. Yassen
 */
public final class AstSwitchStatement extends AstStatement {

    /**
     * The type of the switch statement.
     */
    @Getter
    private final PrimitiveType type;

    /**
     * The condition of the switch statement.
     */
    @Getter
    private final AstExpression condition;

    /**
     * The member cases of the switch statement.
     */
    @Getter
    private final AstSwitchCase[] cases;

    /**
     * The default case of the swich statement.
     */
    @Getter
    private final AstSwitchCase defaultCase;

    /**
     * Constructs a new {@link AstSwitchStatement} type object instance.
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
    public AstSwitchStatement(Range range, PrimitiveType type, AstExpression condition, AstSwitchCase[] cases, AstSwitchCase defaultCase) {
        super(range);
        this.type = type;
        this.condition = condition;
        this.cases = cases;
        this.defaultCase = defaultCase;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <E, S> S accept(AstVisitor<E, S> visitor) {
        return visitor.visit(this);
    }
}


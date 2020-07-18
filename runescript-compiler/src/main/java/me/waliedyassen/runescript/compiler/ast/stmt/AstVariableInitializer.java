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
import me.waliedyassen.runescript.compiler.ast.expr.AstExpression;
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;

/**
 * Represents a variable initialize statement.
 *
 * @author Walied K. Yassen
 */
public final class AstVariableInitializer extends AstStatement {

    /**
     * The scope of the variable.
     */
    @Getter
    private final AstVariable[] variables;

    /**
     * The initialise expression of the variable.
     */
    @Getter
    private final AstExpression[] expressions;

    /**
     * Constructs a new {@link AstVariable} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param variables
     *         the variables we are initializing.
     * @param expressions
     *         the expressions we are initializing.
     */
    public AstVariableInitializer(Range range, AstVariable[] variables, AstExpression[] expressions) {
        super(range);
        this.variables = addChild(variables);
        this.expressions = addChild(expressions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E, S> S accept(AstVisitor<E, S> visitor) {
        return visitor.visit(this);
    }

}

/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
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
import me.waliedyassen.runescript.compiler.ast.expr.AstIdentifier;
import me.waliedyassen.runescript.compiler.util.VariableScope;

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
    private final VariableScope scope;

    /**
     * The name of the variable.
     */
    @Getter
    private final AstIdentifier name;

    /**
     * The initialise expression of the variable.
     */
    @Getter
    private final AstExpression expression;

    /**
     * Construct a new {@link AstVariableInitializer} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param scope
     *         the variable scope.
     * @param name
     *         the variable name.
     * @param expression
     *         the variable initialise expression.
     */
    public AstVariableInitializer(Range range, VariableScope scope, AstIdentifier name, AstExpression expression) {
        super(range);
        this.scope = scope;
        this.name = name;
        this.expression = expression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

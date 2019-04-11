/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.expr;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;
import me.waliedyassen.runescript.compiler.util.VariableScope;

/**
 * Represents an AST variable expression.
 *
 * @author Walied K. Yassen
 */
public final class AstVariable extends AstExpression {

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
     * Constructs a new {@link AstVariable} type object instance.
     *
     * @param range
     *         the expression source code range.
     * @param scope
     *         the scope of the variable.
     * @param name
     *         the name of the variable.
     */
    public AstVariable(Range range, VariableScope scope, AstIdentifier name) {
        super(range);
        this.scope = scope;
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(AstVisitor<?> visitor) {
        visitor.visit(this);
    }
}

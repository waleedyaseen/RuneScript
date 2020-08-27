/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.expr;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;
import me.waliedyassen.runescript.compiler.util.VariableScope;


/**
 * A scoped variable AST expression node.
 *
 * @author Walied K. Yassen
 */
public final class ScopedVariableSyntax extends VariableSyntax {

    /**
     * The scope of the variable.
     */
    @Getter
    private final VariableScope scope;

    /**
     * Constructs a new {@link ScopedVariableSyntax} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param scope
     *         the scope of the varaible.
     * @param name
     *         the name of the variable.
     */
    public ScopedVariableSyntax(Range range, VariableScope scope, IdentifierSyntax name) {
        super(range, name);
        this.scope = scope;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

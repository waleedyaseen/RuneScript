/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.expr;

import lombok.Getter;
import lombok.Setter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.symbol.impl.ArrayInfo;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * An array variable with an index AST expression node.
 *
 * @author Walied K. Yassen
 */
public final class ArrayVariableSyntax extends VariableSyntax {

    /**
     * The index expression of the array.
     */
    @Getter
    private final ExpressionSyntax index;

    /**
     * The array info which is resolved at type checking phase.
     */
    @Getter
    @Setter
    private ArrayInfo arrayInfo;

    /**
     * Constructs a new {@link ArrayVariableSyntax} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param name
     *         the name of the variable.
     * @param index
     *         the index of the array.
     */
    public ArrayVariableSyntax(Range range, IdentifierSyntax name, ExpressionSyntax index) {
        super(range, name);
        this.index = index;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

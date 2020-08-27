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
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;
import me.waliedyassen.runescript.compiler.symbol.impl.ArrayInfo;

/**
 * Represents an array expression.
 *
 * @author Walied K. Yassen
 */
public final class ArrayElementSyntax extends ExpressionSyntax {

    /**
     * The name of the array.
     */
    @Getter
    private final IdentifierSyntax name;

    /**
     * The the element index in the array.
     */
    @Getter
    private final ExpressionSyntax index;

    /**
     * The array symbol information.
     */
    @Getter
    @Setter
    private ArrayInfo array;

    /**
     * Constructs a new {@link ArrayElementSyntax} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param name
     *         the name of the array.
     * @param index
     *         the element index in the array.
     */
    public ArrayElementSyntax(Range range, IdentifierSyntax name, ExpressionSyntax index) {
        super(range);
        this.name = addChild(name);
        this.index = addChild(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

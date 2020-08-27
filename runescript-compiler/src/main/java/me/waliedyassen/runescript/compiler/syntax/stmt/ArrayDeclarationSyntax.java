/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.stmt;

import lombok.Getter;
import lombok.Setter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.syntax.expr.ExpressionSyntax;
import me.waliedyassen.runescript.compiler.syntax.expr.IdentifierSyntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;
import me.waliedyassen.runescript.compiler.symbol.impl.ArrayInfo;
import me.waliedyassen.runescript.type.PrimitiveType;


/**
 * Represents an array declaration AST statement.
 *
 * @author Walied K. Yassen
 */
public final class ArrayDeclarationSyntax extends StatementSyntax {

    /**
     * The type of the  array.
     */
    @Getter
    private final PrimitiveType type;

    /**
     * The name of the array.
     */
    @Getter
    private final IdentifierSyntax name;

    /**
     * The size of the array.
     */
    @Getter
    private final ExpressionSyntax size;

    /**
     * The array symbol information.
     */
    @Getter
    @Setter
    private ArrayInfo array;

    /**
     * Construct a new {@link StatementSyntax} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param type
     *         the type of the array.
     * @param name
     *         the name of the array.
     * @param size
     *         the size of the array.
     */
    public ArrayDeclarationSyntax(Range range, PrimitiveType type, IdentifierSyntax name, ExpressionSyntax size) {
        super(range);
        this.type = type;
        this.name = addChild(name);
        this.size = addChild(size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

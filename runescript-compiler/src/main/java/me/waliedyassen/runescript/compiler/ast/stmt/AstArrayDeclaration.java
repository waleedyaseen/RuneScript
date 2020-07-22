/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.stmt;

import lombok.Getter;
import lombok.Setter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.expr.AstExpression;
import me.waliedyassen.runescript.compiler.ast.expr.AstIdentifier;
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;
import me.waliedyassen.runescript.compiler.symbol.impl.ArrayInfo;
import me.waliedyassen.runescript.type.PrimitiveType;


/**
 * Represents an array declaration AST statement.
 *
 * @author Walied K. Yassen
 */
public final class AstArrayDeclaration extends AstStatement {

    /**
     * The type of the  array.
     */
    @Getter
    private final PrimitiveType type;

    /**
     * The name of the array.
     */
    @Getter
    private final AstIdentifier name;

    /**
     * The size of the array.
     */
    @Getter
    private final AstExpression size;

    /**
     * The array symbol information.
     */
    @Getter
    @Setter
    private ArrayInfo array;

    /**
     * Construct a new {@link AstStatement} type object instance.
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
    public AstArrayDeclaration(Range range, PrimitiveType type, AstIdentifier name, AstExpression size) {
        super(range);
        this.type = type;
        this.name = addChild(name);
        this.size = addChild(size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E, S> S accept(AstVisitor<E, S> visitor) {
        return visitor.visit(this);
    }
}

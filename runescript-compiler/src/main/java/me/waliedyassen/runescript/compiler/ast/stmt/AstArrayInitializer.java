/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
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

/**
 * Represents an array local variable initializer statement.
 *
 * @author Walied K. Yassen
 */
public final class AstArrayInitializer extends AstStatement {

    /**
     * The name of the array.
     */
    @Getter
    private final AstIdentifier name;

    /**
     * The index of the element to initialize.
     */
    @Getter
    private final AstExpression index;

    /**
     * The value initialize the element with.
     */
    @Getter
    private final AstExpression value;

    /**
     * The array we are initializing.
     */
    @Getter @Setter
    private ArrayInfo array;

    /**
     * Constructs a new {@link AstArrayInitializer} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param name
     *         the name of the array.
     * @param index
     *         the index of the element to initialize.
     * @param value
     *         the value to initialize the element with.
     */
    public AstArrayInitializer(Range range, AstIdentifier name, AstExpression index, AstExpression value) {
        super(range);
        this.name = name;
        this.index = index;
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E, S> S accept(AstVisitor<E, S> visitor) {
        return visitor.visit(this);
    }
}

/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.expr.literal;

import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;

/**
 * Represents a boolean literal expression node.
 *
 * @author Walied K. Yassen
 */
public final class AstLiteralBool extends AstLiteral {

    /**
     * The boolean literal value.
     */
    private final boolean value;

    /**
     * Construct a new {@link AstLiteralBool} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param value
     *         the boolean literal value
     */
    public AstLiteralBool(Range range, boolean value) {
        super(range);
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Gets this {@link AstLiteralBool} literal value.
     *
     * @return the boolean value of the literal.
     */
    public boolean getValue() {
        return value;
    }
}

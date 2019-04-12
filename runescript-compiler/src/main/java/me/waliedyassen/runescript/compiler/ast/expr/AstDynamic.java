/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.expr;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;

/**
 * Represents a dynamic expression that will be resolved at a latter phase in compiling.
 *
 * @author Walied K. Yassen
 */
public final class AstDynamic extends AstExpression {

    /**
     * The dynamic name.
     */
    @Getter
    private final AstIdentifier name;

    /**
     * Constructs a new {@link AstExpression} type object instance.
     *
     * @param range
     *         the expression source code range.
     * @param name
     *         the dynamic name.
     */
    public AstDynamic(Range range, AstIdentifier name) {
        super(range);
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

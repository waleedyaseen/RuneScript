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

/**
 * Represents a block of statements, basically a sequential list of code statement in the Abstract Syntax Tree.
 *
 * @author Walied K. Yassen
 */
public final class AstBlockStatement extends AstStatement {

    /**
     * The child statements in this block.
     */
    @Getter
    private final AstStatement[] statements;

    /**
     * Construct a new {@link AstBlockStatement} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param statements
     *         the array of statements.
     */
    public AstBlockStatement(Range range, AstStatement[] statements) {
        super(range);
        this.statements = statements;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

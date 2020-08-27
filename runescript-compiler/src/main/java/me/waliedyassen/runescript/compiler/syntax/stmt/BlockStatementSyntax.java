/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.stmt;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * Represents a block of statements, basically a sequential list of code statement in the Abstract Syntax Tree.
 *
 * @author Walied K. Yassen
 */
public final class BlockStatementSyntax extends StatementSyntax {

    /**
     * The child statements in this block.
     */
    @Getter
    private final StatementSyntax[] statements;

    /**
     * Construct a new {@link BlockStatementSyntax} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param statements
     *         the array of statements.
     */
    public BlockStatementSyntax(Range range, StatementSyntax... statements) {
        super(range);
        this.statements = addChild(statements);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

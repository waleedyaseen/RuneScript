/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.stmt;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Span;
import me.waliedyassen.runescript.compiler.syntax.expr.ExpressionSyntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * Represents an AST switch statement case.
 *
 * @author Walied K. Yassen
 */
public final class SwitchCaseSyntax extends StatementSyntax {

    /**
     * The keys of the switch case.
     */
    @Getter
    private final ExpressionSyntax[] keys;

    /**
     * The switch case block statement.
     */
    @Getter
    private final BlockStatementSyntax code;

    /**
     * Constructs a new {@link SwitchCaseSyntax} type object instance.
     *
     * @param span
     *         the node source code range.
     * @param keys
     *         the keys of the switch case.
     * @param code
     *         the switch case block statement.
     */
    public SwitchCaseSyntax(Span span, ExpressionSyntax[] keys, BlockStatementSyntax code) {
        super(span);
        this.keys = addChild(keys);
        this.code = addChild(code);
    }

    /**
     * Checks whether or not this switch case is the default case.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public boolean isDefault() {
        return keys.length == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }
}


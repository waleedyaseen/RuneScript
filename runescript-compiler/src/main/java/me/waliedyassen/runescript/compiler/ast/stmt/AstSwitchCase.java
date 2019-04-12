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
import me.waliedyassen.runescript.compiler.ast.expr.AstExpression;

/**
 * Represents an AST switch statement case.
 *
 * @author Walied K. Yassen
 */
public final class AstSwitchCase extends AstStatement {

    /**
     * The keys of the switch case.
     */
    @Getter
    private final AstExpression[] keys;

    /**
     * The switch case block statement.
     */
    @Getter
    private final AstBlockStatement code;

    /**
     * Constructs a new {@link AstSwitchCase} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param keys
     *         the keys of the switch case.
     * @param code
     *         the switch case block statement.
     */
    public AstSwitchCase(Range range, AstExpression[] keys, AstBlockStatement code) {
        super(range);
        this.keys = keys;
        this.code = code;
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
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}


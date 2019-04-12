/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
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
 * Represents a gosub expression.
 *
 * @author Walied K. Yassen
 */
public final class AstGosub extends AstExpression {

    /**
     * The target script name.
     */
    @Getter
    private final AstIdentifier name;

    /**
     * The arguments we are passing in the call.
     */
    @Getter
    private final AstExpression[] arguments;

    /**
     * Constructs a new {@link AstGosub} type object instance.
     *
     * @param range
     *         the expression source code range.
     * @param name
     *         the name of the target script.
     * @param arguments
     *         the arguments that will be passed to that script.
     */
    public AstGosub(Range range, AstIdentifier name, AstExpression[] arguments) {
        super(range);
        this.name = name;
        this.arguments = arguments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

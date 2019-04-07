/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.literal;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.expr.AstExpression;

/**
 * Represents an interpolated string concatenation  node.
 *
 * @author Walied K. Yassen
 */
public final class AstStringConcat extends AstLiteral {

    /**
     * The expressions of the concatenation.
     */
    @Getter
    private final AstExpression[] expressions;

    /**
     * Constructs a new {@link AstLiteral} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param expressions
     *         the expressions of the concatenation.
     */
    public AstStringConcat(Range range, AstExpression[] expressions) {
        super(range);
        this.expressions = expressions;
    }
}

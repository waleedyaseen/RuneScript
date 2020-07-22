/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.expr.literal;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.expr.AstExpression;

/**
 * Represents a literal expression node.
 *
 * @author Walied K. Yassen
 */
public abstract class AstLiteral<T> extends AstExpression {

    /**
     * The value of the literal.
     */
    @Getter
    private final T value;

    /**
     * Constructs a new {@link AstLiteral} type object instance.
     *
     * @param range the node source code range.
     * @param value the value of the literal.
     */
    AstLiteral(Range range, T value) {
        super(range);
        this.value = value;
    }
}

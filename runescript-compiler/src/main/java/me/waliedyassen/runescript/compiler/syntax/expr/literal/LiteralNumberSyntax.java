/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.expr.literal;

import me.waliedyassen.runescript.commons.document.Span;

/**
 * Represents a number literal expression node.
 *
 * @author Walied K. Yassen
 */
abstract class LiteralNumberSyntax<T extends Number> extends LiteralExpressionSyntax<T> {

    /**
     * Constructs a new {@link LiteralNumberSyntax} type object instance.
     *
     * @param span  the node source code range.
     * @param number the value of the literal.
     */
    LiteralNumberSyntax(Span span, T number) {
        super(span, number);
    }
}

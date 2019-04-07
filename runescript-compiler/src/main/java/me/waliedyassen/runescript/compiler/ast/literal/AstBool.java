/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.literal;

import me.waliedyassen.runescript.commons.document.Range;

/**
 * Represents a boolean literal expression node.
 *
 * @author Walied K. Yassen
 */
public final class AstBool extends AstLiteral {

    /**
     * The boolean literal value.
     */
    private final boolean value;

    /**
     * Construct a new {@link AstBool} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param value
     *         the boolean literal value
     */
    public AstBool(Range range, boolean value) {
        super(range);
        this.value = value;
    }

    /**
     * Gets this {@link AstBool} literal value.
     *
     * @return the boolean value of the literal.
     */
    public boolean getValue() {
        return value;
    }
}

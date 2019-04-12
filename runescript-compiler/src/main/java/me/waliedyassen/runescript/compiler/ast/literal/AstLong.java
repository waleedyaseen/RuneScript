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
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;

/**
 * Represents a long integer literal expression node.
 *
 * @author Walied K. Yassen
 */
public final class AstLong extends AstNumber {

    /**
     * The long integer value.
     */
    @Getter
    private final long value;

    /**
     * Constructs a new {@link AstLong} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param value
     *         the integer value.
     */
    public AstLong(Range range, long value) {
        super(range);
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

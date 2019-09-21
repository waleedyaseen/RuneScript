/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.ast.value;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.config.ast.visitor.AstVisitor;

/**
 * Represents a configuration property long value node.
 *
 * @author Walied K. Yassen
 */
public final class AstValueLong extends AstValue {

    /**
     * The long value.
     */
    @Getter
    private final long value;

    /**
     * Constructs a new {@link AstValueLong} type object instance.
     *
     * @param range
     *         the node source range.
     * @param value
     *         the long value.
     */
    public AstValueLong(Range range, long value) {
        super(range);
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R visit(AstVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

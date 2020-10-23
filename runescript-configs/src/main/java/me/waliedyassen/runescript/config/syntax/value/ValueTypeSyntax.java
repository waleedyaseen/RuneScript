/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.syntax.value;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.config.syntax.visitor.SyntaxVisitor;
import me.waliedyassen.runescript.type.primitive.PrimitiveType;

/**
 * Represents a configuration property type value node.
 *
 * @author Walied K. Yassen
 */
public final class ValueTypeSyntax extends ValueSyntax {

    /**
     * The primitive type value.
     */
    @Getter
    private final PrimitiveType type;

    /**
     * Constructs a new {@link ValueTypeSyntax} object instance.
     *
     * @param range
     *         the source code range of the node..
     * @param type
     *         the type of the value.
     */
    public ValueTypeSyntax(Range range, PrimitiveType type) {
        super(range);
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R accept(SyntaxVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

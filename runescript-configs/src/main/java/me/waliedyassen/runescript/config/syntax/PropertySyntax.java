/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.syntax;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.config.syntax.value.ValueSyntax;
import me.waliedyassen.runescript.config.syntax.visitor.SyntaxVisitor;

/**
 * Represents a configuration property tree node.
 *
 * @author Walied K. Yassen
 */
public final class PropertySyntax extends Syntax {

    /**
     * The property key.
     */
    @Getter
    private final IdentifierSyntax key;

    /**
     * The property value.
     */
    @Getter
    private final ValueSyntax[] values;

    /**
     * Constructs a new {@link PropertySyntax} type object instance.
     *
     * @param range
     *         the node source range.
     * @param key
     *         the key of the property.
     * @param values
     *         the values of the property.
     */
    public PropertySyntax(Range range, IdentifierSyntax key, ValueSyntax[] values) {
        super(range);
        this.key = addChild(key);
        this.values = addChild(values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R accept(SyntaxVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

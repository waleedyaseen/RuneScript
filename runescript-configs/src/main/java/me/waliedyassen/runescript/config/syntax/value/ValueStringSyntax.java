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

/**
 * Represents a configuration property string value node.
 *
 * @author Walied K. Yassen
 */
public final class ValueStringSyntax extends ValueSyntax {

    /**
     * The string value text content.
     */
    @Getter
    private final String text;

    /**
     * Constructs a new {@link ValueStringSyntax} type object instance.
     *
     * @param range
     *         the node source range.
     * @param text
     *         the string value text content.
     */
    public ValueStringSyntax(Range range, String text) {
        super(range);
        this.text = text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R accept(SyntaxVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

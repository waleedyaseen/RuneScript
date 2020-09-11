/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.syntax.value;

import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.config.syntax.visitor.SyntaxVisitor;

/**
 * Represents an erroneous property value node
 *
 * @author Walied K. Yassen
 */
public class ErrorValueSyntax extends ValueSyntax {

    /**
     * Constructs a new {@link ValueSyntax} type object instance.
     *
     * @param range the node source range range.
     */
    public ErrorValueSyntax(Range range) {
        super(range);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R accept(SyntaxVisitor<R> visitor) {
        return null;
    }
}

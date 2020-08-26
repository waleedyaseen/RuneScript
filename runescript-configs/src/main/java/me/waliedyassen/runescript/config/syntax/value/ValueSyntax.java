/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.syntax.value;

import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.config.syntax.Syntax;
import me.waliedyassen.runescript.config.syntax.visitor.SyntaxVisitor;

/**
 * Represents a configuration property value node
 *
 * @author Walied K. Yassen
 */
public abstract class ValueSyntax extends Syntax {

    /**
     * Constructs a new {@link ValueSyntax} type object instance.
     *
     * @param range
     *         the node source range range.
     */
    public ValueSyntax(Range range) {
        super(range);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract <R> R accept(SyntaxVisitor<R> visitor);
}

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
import me.waliedyassen.runescript.config.syntax.IdentifierSyntax;
import me.waliedyassen.runescript.config.syntax.visitor.SyntaxVisitor;

/**
 * Represents a constant reference value node.
 *
 * @author Walied K. Yassen
 */
public final class ValueConstantSyntax extends ValueSyntax {

    /**
     * The name of the constant.
     */
    @Getter
    private final IdentifierSyntax name;

    /**
     * Constructs a new {@link ValueConstantSyntax} type object instance.
     *
     * @param range
     *         the source code range of the node.
     * @param name
     *         the name of the constant
     */
    public ValueConstantSyntax(Range range, IdentifierSyntax name) {
        super(range);
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R accept(SyntaxVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

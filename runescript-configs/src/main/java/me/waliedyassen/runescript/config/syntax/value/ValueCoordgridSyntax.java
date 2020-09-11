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
 * Represents a configuration property coordgrid value node.
 *
 * @author Walied K. Yassen
 */
public final class ValueCoordgridSyntax extends ValueSyntax {

    /**
     * The packed value of the coordgrid.
     */
    @Getter
    private final int value;

    /**
     * Constructs a new {@link ValueCoordgridSyntax} type object instance.
     *
     * @param range the node source code range.
     * @param value the packed number of the coordgrid.
     */
    public ValueCoordgridSyntax(Range range, int value) {
        super(range);
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

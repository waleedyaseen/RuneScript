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
 * A configuration reference value AST node.
 *
 * @author Walied K. Yassen
 */
public final class ValueConfigSyntax extends ValueSyntax {

    /**
     * The name of the configuration.
     */
    @Getter
    private final IdentifierSyntax name;

    /**
     * Constructs a new {@link ValueConfigSyntax} type object instance.
     *
     * @param range
     *         the node source range code.
     * @param name
     *         the name of the configuration.
     */
    public ValueConfigSyntax(Range range, IdentifierSyntax name) {
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

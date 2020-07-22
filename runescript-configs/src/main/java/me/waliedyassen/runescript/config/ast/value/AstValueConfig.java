/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.ast.value;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.config.ast.AstIdentifier;
import me.waliedyassen.runescript.config.ast.visitor.AstVisitor;

/**
 * A configuration reference value AST node.
 *
 * @author Walied K. Yassen
 */
public final class AstValueConfig extends AstValue {

    /**
     * The name of the configuration.
     */
    @Getter
    private final AstIdentifier name;

    /**
     * Constructs a new {@link AstValueConfig} type object instance.
     *
     * @param range
     *         the node source range code.
     * @param name
     *         the name of the configuration.
     */
    public AstValueConfig(Range range, AstIdentifier name) {
        super(range);
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

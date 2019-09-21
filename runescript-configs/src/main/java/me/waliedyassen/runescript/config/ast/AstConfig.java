/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.ast;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.config.ast.visitor.AstVisitor;

/**
 * Represents a complete configuration tree node.
 *
 * @author Walied K. Yassen
 */
public final class AstConfig extends AstNode {

    /**
     * The configuration name.
     */
    @Getter
    private final AstIdentifier name;

    /**
     * The configuration properties.
     */
    @Getter
    private final AstProperty[] properties;

    /**
     * Constructs a new {@link AstConfig} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param name
     *         the configuration name.
     * @param properties
     *         the configuration properties.
     */
    public AstConfig(Range range, AstIdentifier name, AstProperty[] properties) {
        super(range);
        this.name = name;
        this.properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R visit(AstVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

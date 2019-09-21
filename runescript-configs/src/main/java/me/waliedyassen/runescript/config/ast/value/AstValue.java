/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.ast.value;

import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.config.ast.AstNode;
import me.waliedyassen.runescript.config.ast.visitor.AstVisitor;

/**
 * Represents a configuration property value node
 *
 * @author Walied K. Yassen
 */
public abstract class AstValue extends AstNode {

    /**
     * Constructs a new {@link AstValue} type object instance.
     *
     * @param range
     *         the node source range.
     */
    public AstValue(Range range) {
        super(range);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R visit(AstVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

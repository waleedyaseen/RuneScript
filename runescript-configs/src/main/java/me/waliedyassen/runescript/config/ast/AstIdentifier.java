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
 * Represents an identifier tree node.
 *
 * @author Walied K. Yassen
 */
public final class AstIdentifier extends AstNode {

    /**
     * The text content of the identifier.
     */
    @Getter
    private final String text;

    /**
     * Constructs a new {@link AstIdentifier} type object instance.
     *
     * @param range
     *         the node source code range.
     */
    public AstIdentifier(Range range, String text) {
        super(range);
        this.text = text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

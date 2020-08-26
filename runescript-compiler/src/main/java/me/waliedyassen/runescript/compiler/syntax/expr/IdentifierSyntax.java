/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.expr;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * Represents an identifier node, an identifier is any word within the document that is not a keyword.
 *
 * @author Walied K. Yassen
 */
public final class IdentifierSyntax extends ExpressionSyntax {

    /**
     * The identifier text content.
     */
    @Getter
    private final String text;

    /**
     * Constructs a new {@link IdentifierSyntax} type object instance.
     *
     * @param range
     *         the identifier source code range.
     * @param text
     *         the identifier text content.
     */
    public IdentifierSyntax(Range range, String text) {
        super(range);
        this.text = text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E, S> E accept(SyntaxVisitor<E, S> visitor) {
        return null;
    }
}

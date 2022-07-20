/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.expr;

import lombok.Getter;
import lombok.ToString;
import me.waliedyassen.runescript.commons.document.Span;
import me.waliedyassen.runescript.compiler.syntax.SyntaxToken;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * Represents an identifier node, an identifier is any word within the document that is not a keyword.
 *
 * @author Walied K. Yassen
 */
@ToString
public final class IdentifierSyntax extends ExpressionSyntax {

    /**
     * The token of the identifier.
     */
    @Getter
    private final SyntaxToken token;

    /**
     * Constructs a new {@link IdentifierSyntax} type object instance.
     *
     * @param span the identifier source code range.
     * @param token the token of the identifier.
     */
    public IdentifierSyntax(Span span, SyntaxToken token) {
        super(span);
        this.token = token;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return null;
    }

    /**
     * Returns the text of the identifier.
     *
     * @return the text of the identifier.
     */
    public String getText() {
        return token.getLexeme();
    }
}

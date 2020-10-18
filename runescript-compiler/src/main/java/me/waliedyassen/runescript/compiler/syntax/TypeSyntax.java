/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * @author Walied K. Yassen
 */
public final class TypeSyntax extends Syntax {

    /**
     * The token of the type.
     */
    @Getter
    private final SyntaxToken token;

    /**
     * Constructs a new {@link SyntaxToken} type object instance.
     *
     * @param range the source c ode range of the syntax.
     * @param token the syntax token of the type.
     */
    public TypeSyntax(Range range, SyntaxToken token) {
        super(range);
        this.token = token;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return null;
    }
}

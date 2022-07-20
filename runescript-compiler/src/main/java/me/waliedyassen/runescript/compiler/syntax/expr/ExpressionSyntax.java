/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.expr;

import me.waliedyassen.runescript.commons.document.Span;
import me.waliedyassen.runescript.compiler.syntax.Syntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * Represents an expression node, all the language expressions must be subclasses of this class.
 *
 * @author Walied K. Yassen
 */
public abstract class ExpressionSyntax extends Syntax {

    /**
     * Constructs a new {@link ExpressionSyntax} type object instance.
     *
     * @param span the expression source code range.
     */
    public ExpressionSyntax(Span span) {
        super(span);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract <T> T accept(SyntaxVisitor<T> visitor);
}

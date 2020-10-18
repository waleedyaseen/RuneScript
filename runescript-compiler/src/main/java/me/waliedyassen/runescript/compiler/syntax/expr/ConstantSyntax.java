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
import me.waliedyassen.runescript.compiler.syntax.SyntaxToken;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * Represents a constant node, a constant is temporal "variable" that will be replaced with it's value in the
 * compile-time.
 *
 * @author Walied K. Yassen
 */
public final class ConstantSyntax extends ExpressionSyntax {

    /**
     * The caret syntax token.
     */
    @Getter
    private final SyntaxToken caretToken;

    /**
     * The name of the constant.
     */
    @Getter
    private final IdentifierSyntax name;

    /**
     * Constructs a new {@link ConstantSyntax} type object instance.
     *
     * @param range      the expression source code range.
     * @param caretToken the caret syntax token.
     * @param name       the name of the constant.
     */
    public ConstantSyntax(Range range, SyntaxToken caretToken, IdentifierSyntax name) {
        super(range);
        this.caretToken = caretToken;
        this.name = addChild(name);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

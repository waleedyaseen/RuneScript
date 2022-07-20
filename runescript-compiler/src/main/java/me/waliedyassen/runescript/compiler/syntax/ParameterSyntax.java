/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Span;
import me.waliedyassen.runescript.compiler.syntax.expr.IdentifierSyntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * Represents a parameter AST node.
 *
 * @author Walied K. Yassen
 */
public final class ParameterSyntax extends Syntax {

    /**
     * The token of the dollar symbol.
     */
    private final SyntaxToken dollarToken;

    /**
     * The type of the parameter.
     */
    @Getter
    private final SyntaxToken typeToken;

    /**
     * The name of the parameter.
     */
    @Getter
    private final IdentifierSyntax name;

    /**
     * The index of the parameter.
     */
    @Getter
    private final int index;

    /**
     * Construct a new {@link ScriptSyntax} type object instance.
     *
     * @param span       the node source range.
     * @param dollarToken the token of the dollar symbol.
     * @param typeToken        the type of the parameter.
     * @param name        the name of the parameter.
     */
    public ParameterSyntax(Span span, SyntaxToken dollarToken, SyntaxToken typeToken, IdentifierSyntax name, int index) {
        super(span);
        this.dollarToken = dollarToken;
        this.typeToken = typeToken;
        this.index = index;
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

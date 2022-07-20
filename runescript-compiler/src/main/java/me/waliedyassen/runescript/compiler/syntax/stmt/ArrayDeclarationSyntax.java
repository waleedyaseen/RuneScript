/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.stmt;

import lombok.Getter;
import lombok.Setter;
import me.waliedyassen.runescript.commons.document.Span;
import me.waliedyassen.runescript.compiler.symbol.impl.ArrayInfo;
import me.waliedyassen.runescript.compiler.syntax.SyntaxToken;
import me.waliedyassen.runescript.compiler.syntax.expr.ExpressionSyntax;
import me.waliedyassen.runescript.compiler.syntax.expr.IdentifierSyntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;


/**
 * Represents an array declaration AST statement.
 *
 * @author Walied K. Yassen
 */
public final class ArrayDeclarationSyntax extends StatementSyntax {

    /**
     * The token of the define keyword.
     */
    @Getter
    private final SyntaxToken defineToken;

    /**
     * the token of the dollar symbol.
     */
    @Getter
    private final SyntaxToken dollarToken;

    /**
     * The token of the semicolon.
     */
    @Getter
    private final SyntaxToken semicolonToken;

    /**
     * The name of the array.
     */
    @Getter
    private final IdentifierSyntax name;

    /**
     * The size of the array.
     */
    @Getter
    private final ExpressionSyntax size;

    /**
     * The array symbol information.
     */
    @Getter
    @Setter
    private ArrayInfo array;

    /**
     * Construct a new {@link StatementSyntax} type object instance.
     *
     * @param span          the node source code range.
     * @param defineToken    the token of the define keyword.
     * @param dollarToken    the token of the dollar symbol.
     * @param semicolonToken the token of the semicolon.
     * @param name           the name of the array.
     * @param size           the size of the array.
     */
    public ArrayDeclarationSyntax(Span span, SyntaxToken defineToken, SyntaxToken dollarToken, SyntaxToken semicolonToken, IdentifierSyntax name, ExpressionSyntax size) {
        super(span);
        this.defineToken = defineToken;
        this.dollarToken = dollarToken;
        this.semicolonToken = semicolonToken;
        this.name = addChild(name);
        this.size = addChild(size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

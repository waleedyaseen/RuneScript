/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.stmt;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Span;
import me.waliedyassen.runescript.compiler.syntax.SyntaxToken;
import me.waliedyassen.runescript.compiler.syntax.expr.ExpressionSyntax;
import me.waliedyassen.runescript.compiler.syntax.expr.IdentifierSyntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;
import me.waliedyassen.runescript.type.primitive.PrimitiveType;

/**
 * Represents a variable define (or declaration) statement.
 *
 * @author Walied K. Yassen
 */
public final class VariableDeclarationSyntax extends StatementSyntax {

    /**
     * The token of the define keyword.
     */
    @Getter
    private final SyntaxToken defineToken;

    /**
     * The token of the dollar sign.
     */
    @Getter
    private final SyntaxToken dollarToken;

    /**
     * The variable name.
     */
    @Getter
    private final IdentifierSyntax name;

    /**
     * The variable initializer expression.
     */
    @Getter
    private final ExpressionSyntax expression;

    /**
     * Construct a new {@link VariableDeclarationSyntax} type object instance.
     *
     * @param span       the node source code range.
     * @param defineToken the token of the define keyword.
     * @param dollarToken the toke nof the dollar sign.
     * @param name        the name of the variable.
     * @param expression  the initializer expression of the variable.
     */
    public VariableDeclarationSyntax(Span span, SyntaxToken defineToken, SyntaxToken dollarToken, IdentifierSyntax name, ExpressionSyntax expression) {
        super(span);
        this.defineToken = defineToken;
        this.dollarToken = dollarToken;
        this.name = addChild(name);
        this.expression = expression != null ? addChild(expression) : null;
        setType(PrimitiveType.forRepresentation(defineToken.getLexeme().substring("def_".length())));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

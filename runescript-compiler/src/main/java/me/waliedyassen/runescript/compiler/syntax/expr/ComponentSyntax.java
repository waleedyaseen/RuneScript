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
import me.waliedyassen.runescript.compiler.syntax.expr.literal.LiteralExpressionSyntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * Represents a component literal expression node.
 *
 * @author Walied K. Yassen
 */
public final class ComponentSyntax extends ExpressionSyntax {

    /**
     * The parent name of the component.
     */
    @Getter
    private final IdentifierSyntax parentInterface;

    /**
     * The child name of the component.
     */
    @Getter
    private final ExpressionSyntax component;

    /**
     * Constructs a new {@link ComponentSyntax} type object instance.
     *
     * @param range           the source code range of the node.
     * @param parentInterface the parent name of the component.
     * @param component       the component name expression.
     */
    public ComponentSyntax(Range range, IdentifierSyntax parentInterface, ExpressionSyntax component) {
        super(range);
        this.parentInterface = addChild(parentInterface);
        this.component = addChild(component);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Returns the string value of the component expression.
     *
     * @return the string value of the component expression.
     */
    @SuppressWarnings("rawtypes")
    public String getComponentName() {
        if (component instanceof LiteralExpressionSyntax) {
            return String.valueOf(((LiteralExpressionSyntax) component).getValue());
        } else if (component instanceof IdentifierSyntax) {
            return ((IdentifierSyntax) component).getText();
        } else {
            throw new IllegalStateException();
        }
    }
}

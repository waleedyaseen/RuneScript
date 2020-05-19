/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.expr;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.expr.literal.AstLiteral;
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;

/**
 * Represents a component literal expression node.
 *
 * @author Walied K. Yassen
 */
public final class AstComponent extends AstExpression {

    /**
     * The parent name of the component.
     */
    @Getter
    private final AstIdentifier parent;

    /**
     * The child name of the component.
     */
    @Getter
    private final AstExpression component;

    /**
     * Constructs a new {@link AstComponent} type object instance.
     *
     * @param range     the source code range of the node.
     * @param parent    the parent name of the component.
     * @param component the component name expression.
     */
    public AstComponent(Range range, AstIdentifier parent, AstExpression component) {
        super(range);
        this.parent = addChild(parent);
        this.component = addChild(component);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E, S> E accept(AstVisitor<E, S> visitor) {
        return visitor.visit(this);
    }

    /**
     * Returns the string value of the component expression.
     *
     * @return the string value of the component expression.
     */
    @SuppressWarnings("rawtypes")
    public String getComponentName() {
        if (component instanceof AstLiteral) {
            return String.valueOf(((AstLiteral) component).getValue());
        } else if (component instanceof AstIdentifier) {
            return ((AstIdentifier) component).getText();
        } else {
            throw new IllegalStateException();
        }
    }
}

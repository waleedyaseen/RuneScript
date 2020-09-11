/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.syntax.visitor;

import lombok.var;
import me.waliedyassen.runescript.config.syntax.ConfigSyntax;
import me.waliedyassen.runescript.config.syntax.IdentifierSyntax;
import me.waliedyassen.runescript.config.syntax.PropertySyntax;
import me.waliedyassen.runescript.config.syntax.value.*;

/**
 * Represents an {@link SyntaxVisitor} that will visit every node and it's children in the tree.
 *
 * @author Walied K. Yassen
 */
public abstract class SyntaxTreeVisitor implements SyntaxVisitor<Object> {

    /**
     * The default return  value for the visitor methods.
     */
    protected static final Object DEFAULT = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(ConfigSyntax config) {
        config.getName().accept(this);
        for (var property : config.getProperties()) {
            property.accept(this);
        }
        return DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(PropertySyntax property) {
        property.getKey().accept(this);
        for (var value : property.getValues()) {
            value.accept(this);
        }
        return DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(ValueStringSyntax value) {
        return DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(ValueIntegerSyntax value) {
        return DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(ValueLongSyntax value) {
        return DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(ValueBooleanSyntax value) {
        return DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(ValueTypeSyntax value) {
        return DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(ValueConstantSyntax value) {
        return DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(ValueConfigSyntax value) {
        return DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(ValueCoordgridSyntax valueCoordgridSyntax) {
        return DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(IdentifierSyntax identifier) {
        return DEFAULT;
    }
}

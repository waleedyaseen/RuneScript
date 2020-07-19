/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.ast.visitor;

import lombok.var;
import me.waliedyassen.runescript.config.ast.AstConfig;
import me.waliedyassen.runescript.config.ast.AstIdentifier;
import me.waliedyassen.runescript.config.ast.AstProperty;
import me.waliedyassen.runescript.config.ast.value.AstValueBoolean;
import me.waliedyassen.runescript.config.ast.value.AstValueInteger;
import me.waliedyassen.runescript.config.ast.value.AstValueLong;
import me.waliedyassen.runescript.config.ast.value.AstValueString;

/**
 * Represents an {@link AstVisitor} that will visit every node and it's children in the tree.
 *
 * @author Walied K. Yassen
 */
public abstract class AstTreeVisitor implements AstVisitor<Object> {

    /**
     * The default return  value for the visitor methods.
     */
    protected static final Object DEFAULT = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(AstConfig config) {
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
    public Object visit(AstProperty property) {
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
    public Object visit(AstValueString value) {
        return DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(AstValueInteger value) {
        return DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(AstValueLong value) {
        return DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(AstValueBoolean value) {
        return DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(AstIdentifier identifier) {
        return DEFAULT;
    }
}

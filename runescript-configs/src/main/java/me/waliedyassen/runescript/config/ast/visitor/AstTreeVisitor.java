/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.ast.visitor;

import me.waliedyassen.runescript.config.ast.AstConfig;
import me.waliedyassen.runescript.config.ast.AstIdentifier;
import me.waliedyassen.runescript.config.ast.AstProperty;
import me.waliedyassen.runescript.config.ast.value.AstValue;
import me.waliedyassen.runescript.config.ast.value.AstValueInteger;
import me.waliedyassen.runescript.config.ast.value.AstValueLong;
import me.waliedyassen.runescript.config.ast.value.AstValueString;

/**
 * Represents an {@link AstVisitor} that will visit every node and it's children in the tree.
 *
 * @author Walied K. Yassen
 */
public abstract class AstTreeVisitor implements AstVisitor<Void> {

    /**
     * The default return  value for the visitor methods.
     */
    protected static final Void DEFAULT = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstConfig config) {
        config.getName().visit(this);
        for (var property : config.getProperties()) {
            property.visit(this);
        }
        return DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstProperty property) {
        property.getKey().visit(this);
        for (var value : property.getValues()) {
            value.visit(this);
        }
        return DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstValue value) {
        return DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstValueString value) {
        return DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstValueInteger value) {
        return DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstValueLong value) {
        return DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstIdentifier identifier) {
        return DEFAULT;
    }
}

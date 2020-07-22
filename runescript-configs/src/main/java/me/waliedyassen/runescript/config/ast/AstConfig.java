/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.ast;

import lombok.Getter;
import lombok.var;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.config.ast.value.AstValueType;
import me.waliedyassen.runescript.config.ast.visitor.AstVisitor;
import me.waliedyassen.runescript.config.binding.ConfigBinding;
import me.waliedyassen.runescript.type.PrimitiveType;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a complete configuration tree node.
 *
 * @author Walied K. Yassen
 */
public final class AstConfig extends AstNode {

    /**
     * The configuration name.
     */
    @Getter
    private final AstIdentifier name;

    /**
     * The configuration properties.
     */
    @Getter
    private final AstProperty[] properties;

    /**
     * Constructs a new {@link AstConfig} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param name
     *         the configuration name.
     * @param properties
     *         the configuration properties.
     */
    public AstConfig(Range range, AstIdentifier name, AstProperty[] properties) {
        super(range);
        this.name = addChild(name);
        this.properties = addChild(properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visit(this);
    }

    /**
     * Attempts to find the {@link AstProperty} object with the specified {@code name} in this config.
     *
     * @param name
     *         the name of the property that we want to find.
     *
     * @return the {@link AstProperty} object if found otherwise {@code null}.
     */
    public AstProperty findProperty(String name) {
        for (var property : properties) {
            if (property.getKey().getText().equals(name)) {
                return property;
            }
        }
        return null;
    }

    /**
     * Attempts to find all of the the {@link AstProperty} objects with the specified {@code name} in this config.
     *
     * @param name
     *         the name of the property that we want to find.
     *
     * @return a {@link List} of all the {@link AstProperty} objects that were found.
     */
    public List<AstProperty> findProperties(String name) {
        var list = new ArrayList<AstProperty>();
        for (var property : properties) {
            if (property.getKey().getText().equals(name)) {
                list.add(property);
            }
        }
        return list;
    }

    /**
     * Resolves the content type of this configuration.
     *
     * @param binding
     *         the binding of the configuration.
     *
     * @return the {@link PrimitiveType} of the configuration.
     */
    public PrimitiveType resolveContentType(ConfigBinding binding) {
        var contentType = binding.getContentTypeProperty();
        if (contentType == null) {
            return null;
        }
        var property = findProperty(binding.getContentTypeProperty());
        if (property == null || property.getValues().length != 1) {
            return PrimitiveType.UNDEFINED;
        }
        var value = property.getValues()[0];
        if (!(value instanceof AstValueType)) {
            return PrimitiveType.UNDEFINED;
        }
        return ((AstValueType) value).getType();
    }
}

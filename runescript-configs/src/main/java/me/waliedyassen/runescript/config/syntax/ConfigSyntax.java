/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.syntax;

import lombok.Getter;
import lombok.Setter;
import lombok.var;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.config.syntax.value.ValueTypeSyntax;
import me.waliedyassen.runescript.config.syntax.visitor.SyntaxVisitor;
import me.waliedyassen.runescript.config.binding.ConfigBinding;
import me.waliedyassen.runescript.type.primitive.PrimitiveType;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a complete configuration tree node.
 *
 * @author Walied K. Yassen
 */
public final class ConfigSyntax extends Syntax {

    /**
     * The configuration name.
     */
    @Getter
    private final IdentifierSyntax name;

    /**
     * The configuration properties.
     */
    @Getter
    private final PropertySyntax[] properties;

    /**
     * The content type of the configuration, resolved at type checking time.
     */
    @Getter
    @Setter
    private PrimitiveType contentType;

    /**
     * Constructs a new {@link ConfigSyntax} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param name
     *         the configuration name.
     * @param properties
     *         the configuration properties.
     */
    public ConfigSyntax(Range range, IdentifierSyntax name, PropertySyntax[] properties) {
        super(range);
        this.name = addChild(name);
        this.properties = addChild(properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R accept(SyntaxVisitor<R> visitor) {
        return visitor.visit(this);
    }

    /**
     * Attempts to find the {@link PropertySyntax} object with the specified {@code name} in this config.
     *
     * @param name
     *         the name of the property that we want to find.
     *
     * @return the {@link PropertySyntax} object if found otherwise {@code null}.
     */
    public PropertySyntax findProperty(String name) {
        for (var property : properties) {
            if (property.getKey().getText().equals(name)) {
                return property;
            }
        }
        return null;
    }

    /**
     * Attempts to find all of the the {@link PropertySyntax} objects with the specified {@code name} in this config.
     *
     * @param name
     *         the name of the property that we want to find.
     *
     * @return a {@link List} of all the {@link PropertySyntax} objects that were found.
     */
    public List<PropertySyntax> findProperties(String name) {
        var list = new ArrayList<PropertySyntax>();
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
        if (!(value instanceof ValueTypeSyntax)) {
            return PrimitiveType.UNDEFINED;
        }
        return ((ValueTypeSyntax) value).getType();
    }
}

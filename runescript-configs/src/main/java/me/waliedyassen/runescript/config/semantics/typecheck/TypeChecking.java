/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.semantics.typecheck;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.compiler.symbol.SymbolTable;
import me.waliedyassen.runescript.config.ast.AstConfig;
import me.waliedyassen.runescript.config.ast.AstIdentifier;
import me.waliedyassen.runescript.config.ast.AstProperty;
import me.waliedyassen.runescript.config.ast.value.*;
import me.waliedyassen.runescript.config.ast.visitor.AstTreeVisitor;
import me.waliedyassen.runescript.config.binding.ConfigBinding;
import me.waliedyassen.runescript.config.semantics.SemanticChecker;
import me.waliedyassen.runescript.config.semantics.SemanticError;
import me.waliedyassen.runescript.config.var.ConfigBasicProperty;
import me.waliedyassen.runescript.type.PrimitiveType;
import me.waliedyassen.runescript.type.Type;

/**
 * Represents the type checking semantic analysis.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class TypeChecking extends AstTreeVisitor {

    /**
     * The semantic checker which owns this type checker.
     */
    @Getter
    private final SemanticChecker checker;

    /**
     * The symbol table to use for looking up.
     */
    @Getter
    private final SymbolTable table;

    /**
     * The configuration group we are checking for.
     */
    @Getter
    private final ConfigBinding binding;

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(AstConfig config) {
        for (var property : config.getProperties()) {
            property.accept(this);
        }
        for (var entry : binding.getProperties().values()) {
            var properties = config.findProperties(entry.getName());
            if (properties.isEmpty()) {
                if (entry.isRequired()) {
                    checker.reportError(new SemanticError(config.getName(), String.format("The '%s' config requires property '%s'", binding.getGroup().getType().getRepresentation(), entry.getName())));
                }
                continue;
            }
            if (properties.size() > 1) {
                checker.reportError(new SemanticError(config.getName(), String.format("The property '%s' is already defined", entry.getName())));
            }
        }
        return DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(AstProperty property) {
        var bindingProperty = binding.findProperty(property.getKey().getText());
        if (bindingProperty == null) {
            checker.reportError(new SemanticError(property.getKey(), "Unknown property: " + property.getKey().getText()));
            return null;
        }
        if (bindingProperty instanceof ConfigBasicProperty) {
            checkBasicProperty(property, (ConfigBasicProperty) bindingProperty);
        } else {
            throw new IllegalArgumentException("Unrecognised binding property type: " + bindingProperty);
        }
        return DEFAULT;
    }

    /**
     * Performs type checking for a basic property.
     *
     * @param node
     *         the AST node of the property.
     * @param property
     *         the basic property that we want to type check.
     */
    private void checkBasicProperty(AstProperty node, ConfigBasicProperty property) {
        var components = property.getComponents();
        var values = node.getValues();
        if (components.length != values.length) {
            checker.reportError(new SemanticError(node, "Components mismatch: expected " + components.length + " component(s) but got " + values.length + " component(s)"));
            return;
        }
        for (var index = 0; index < values.length; index++) {
            var value = values[index];
            var type = (PrimitiveType) value.accept(this);
            if (type.implicitEquals(components[index])) {
                property.getRules().forEach(rule -> rule.test(this, node, value));
            } else {
                checker.reportError(new SemanticError(value, "Type mismatch: cannot convert from " + type.getRepresentation() + " to " + components[index].getRepresentation()));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrimitiveType visit(AstValueString value) {
        return PrimitiveType.STRING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrimitiveType visit(AstValueInteger value) {
        return PrimitiveType.INT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrimitiveType visit(AstValueLong value) {
        return PrimitiveType.LONG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrimitiveType visit(AstValueBoolean value) {
        return PrimitiveType.BOOLEAN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrimitiveType visit(AstValueType value) {
        return PrimitiveType.TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstValueConstant value) {
        var constantInfo = table.lookupConstant(value.getName().getText());
        if (constantInfo == null) {
            checker.reportError(new SemanticError(value, String.format("%s cannot be resolved to a constant", value.getName().getText())));
            return PrimitiveType.UNDEFINED;
        }
        return constantInfo.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(AstIdentifier identifier) {
        return DEFAULT;
    }
}

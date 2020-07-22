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
import me.waliedyassen.runescript.config.ast.AstNode;
import me.waliedyassen.runescript.config.ast.AstProperty;
import me.waliedyassen.runescript.config.ast.value.*;
import me.waliedyassen.runescript.config.ast.visitor.AstTreeVisitor;
import me.waliedyassen.runescript.config.binding.ConfigBinding;
import me.waliedyassen.runescript.config.semantics.SemanticChecker;
import me.waliedyassen.runescript.config.semantics.SemanticError;
import me.waliedyassen.runescript.config.var.ConfigBasicProperty;
import me.waliedyassen.runescript.config.var.ConfigParamProperty;
import me.waliedyassen.runescript.config.var.ConfigProperty;
import me.waliedyassen.runescript.config.var.rule.ConfigRule;
import me.waliedyassen.runescript.config.var.splitarray.ConfigSplitArrayProperty;
import me.waliedyassen.runescript.type.PrimitiveType;
import me.waliedyassen.runescript.type.Type;

import java.util.List;

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
            if (!entry.isAllowDuplicates() && properties.size() > 1) {
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
        var config = (AstConfig) property.getParent();
        if (bindingProperty instanceof ConfigBasicProperty) {
            performBasicChecks(config, property, bindingProperty);
        } else if (bindingProperty instanceof ConfigSplitArrayProperty) {
            performSplitArrayChecks(config, property, (ConfigSplitArrayProperty) bindingProperty);
        } else if (bindingProperty instanceof ConfigParamProperty) {
            performParamChecks(config, property, (ConfigParamProperty) bindingProperty);
        } else {
            throw new IllegalArgumentException("Unrecognised binding property type: " + bindingProperty);
        }
        return DEFAULT;
    }

    /**
     * Performs the basic checks for a config property.
     *
     * @param config
     *         the owner configuration of the property.
     * @param node
     *         the AST node of the property.
     * @param property
     *         the basic property that we want to type check.
     */
    private void performBasicChecks(AstConfig config, AstProperty node, ConfigProperty property) {
        var components = property.getComponents();
        var values = node.getValues();
        if (components.length != values.length) {
            checker.reportError(new SemanticError(node, "Components mismatch: expected " + components.length + " component(s) but got " + values.length + " component(s)"));
            return;
        }
        for (var index = 0; index < values.length; index++) {
            performComponentCheck(config, node, components[index], values[index], property.getRules());
        }
    }

    /**
     * Performs type checking for a split array property.
     *
     * @param config
     *         the owner configuration of the property.
     * @param node
     *         the AST node of the property.
     * @param property
     *         the split array property that we want to type check.
     */
    private void performSplitArrayChecks(AstConfig config, AstProperty node, ConfigSplitArrayProperty property) {
        performBasicChecks(config, node, property);
    }

    /**
     * Performs type checking for a param property.
     *
     * @param config
     *         the owner configuration of the property.
     * @param node
     *         the AST node of the property.
     * @param property
     *         the param property that we want to type check.
     */
    private void performParamChecks(AstConfig config, AstProperty node, ConfigParamProperty property) {
        if (node.getValues().length != 2) {
            checker.reportError(new SemanticError(node, "Components mismatch: expected 2 component(s) but got " + node.getValues().length + " component(s)"));
            return;
        }
        var paramRaw = node.getValues()[0];
        if (!performTypeCheck(paramRaw, PrimitiveType.PARAM, (PrimitiveType) paramRaw.accept(this))) {
            return;
        }
        var paramInfo = table.lookupConfig(((AstValueConfig) paramRaw).getName().getText());
        var valueRaw = node.getValues()[1];
        if (!performTypeCheck(valueRaw, (PrimitiveType) paramInfo.getContentType(), (PrimitiveType) valueRaw.accept(this))) {
            return;
        }
    }

    /**
     * Tests the specified value of the specified component against the specified list of rules.
     *
     * @param config
     *         the owner configuration of the property.
     * @param node
     *         the property of the value and the component.
     * @param component
     *         the component type of the value.
     * @param value
     *         the value that we want to test.
     * @param rules
     *         the rules that we want to test against.
     */
    private void performComponentCheck(AstConfig config, AstProperty node, PrimitiveType component, AstValue value, List<ConfigRule> rules) {
        if (performTypeCheck(value, component, (PrimitiveType) value.accept(this))) {
            rules.forEach(rule -> rule.test(this, config, node, value));
        }
    }

    /**
     * Performs a basic type mismatch check on the specified {@link AstValue value}
     *
     * @param node
     *         the node to error at if the type check fails.
     * @param expected
     *         the expected type.
     * @param actual
     *         the actual type.
     *
     * @return <code>true</code> if the type check passes otherwise <code>false</code>.
     */
    private boolean performTypeCheck(AstNode node, PrimitiveType expected, PrimitiveType actual) {
        if (!expected.implicitEquals(actual)) {
            checker.reportError(new SemanticError(node, "Type mismatch: cannot convert from " + actual.getRepresentation() + " to " + expected.getRepresentation()));
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrimitiveType visit(AstValueString value) {
        var graphic = table.lookupGraphic(value.getText());
        if (graphic != null) {
            return PrimitiveType.GRAPHIC;
        }
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
    public Type visit(AstValueConfig value) {
        var configInfo = table.lookupConfig(value.getName().getText());
        if (configInfo == null) {
            checker.reportError(new SemanticError(value, String.format("%s cannot be resolved to a config", value.getName().getText())));
            return PrimitiveType.UNDEFINED;
        }
        return configInfo.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(AstIdentifier identifier) {
        return DEFAULT;
    }
}

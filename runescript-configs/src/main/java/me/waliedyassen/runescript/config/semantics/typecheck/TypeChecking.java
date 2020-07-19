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
import me.waliedyassen.runescript.config.ast.value.AstValueBoolean;
import me.waliedyassen.runescript.config.ast.value.AstValueInteger;
import me.waliedyassen.runescript.config.ast.value.AstValueLong;
import me.waliedyassen.runescript.config.ast.value.AstValueString;
import me.waliedyassen.runescript.config.ast.visitor.AstTreeVisitor;
import me.waliedyassen.runescript.config.binding.ConfigBinding;
import me.waliedyassen.runescript.config.semantics.SemanticChecker;
import me.waliedyassen.runescript.config.semantics.SemanticError;
import me.waliedyassen.runescript.type.PrimitiveType;

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
        for (var entry : binding.getVariables().values()) {
            if (!entry.isRequired()) {
                continue;
            }
            if (config.findProperty(entry.getName()) == null) {
                checker.reportError(new SemanticError(config.getName(), String.format("The '%s' config requires property '%s'", binding.getGroup().getType().getRepresentation(), entry.getName())));
            }
        }
        return DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(AstProperty property) {
        var variable = binding.getVariables().get(property.getKey().getText());
        if (variable == null) {
            checker.reportError(new SemanticError(property.getKey(), "Unknown property: " + property.getKey().getText()));
            return null;
        }
        var components = variable.getType().getComponents();
        var values = property.getValues();
        if (components.length != values.length) {
            checker.reportError(new SemanticError(property, "Components mismatch: expected " + components.length + " component(s) but got " + values.length + " component(s)"));
            return null;
        }
        for (var index = 0; index < values.length; index++) {
            var value = values[index];
            var type = (PrimitiveType) value.accept(this);
            if (type == components[index]) {
                variable.getRules().forEach(rule -> rule.test(this, property, value));
            } else {
                checker.reportError(new SemanticError(value, "Type mismatch: cannot convert from " + type.getRepresentation() + " to " + components[index].getRepresentation()));
            }
        }
        return DEFAULT;
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
    public Object visit(AstIdentifier identifier) {
        return DEFAULT;
    }
}

/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.codegen;

import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.compiler.symbol.SymbolTable;
import me.waliedyassen.runescript.config.ast.AstConfig;
import me.waliedyassen.runescript.config.ast.AstIdentifier;
import me.waliedyassen.runescript.config.ast.AstProperty;
import me.waliedyassen.runescript.config.ast.value.*;
import me.waliedyassen.runescript.config.ast.visitor.AstVisitor;
import me.waliedyassen.runescript.config.binding.ConfigBinding;
import me.waliedyassen.runescript.config.var.ConfigBasicProperty;
import me.waliedyassen.runescript.config.var.rule.ConfigRules;
import me.waliedyassen.runescript.type.PrimitiveType;

import java.util.ArrayList;

/**
 * The code generator for the configuration compiler.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class CodeGenerator implements AstVisitor<Object> {

    /**
     * The symbol table of the compiler.
     */
    private final SymbolTable symbolTable;

    /**
     * The binding of the configuration.
     */
    private final ConfigBinding binding;


    /**
     * {@inheritDoc}
     */
    @Override
    public BinaryConfig visit(AstConfig config) {
        var count = config.getProperties().length;
        var properties = new ArrayList<BinaryProperty>(count);
        for (var index = 0; index < count; index++) {
            var property = visit(config.getProperties()[index]);
            if (property == null) {
                continue;
            }
            properties.add(property);
        }
        return new BinaryConfig(binding.getGroup(), config.getName().getText(), properties.toArray(new BinaryProperty[0]));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BinaryProperty visit(AstProperty property) {
        var bindingProperty = binding.getProperties().get(property.getKey().getText());
        if (bindingProperty instanceof ConfigBasicProperty) {
            return generateBasicProperty(property, (ConfigBasicProperty) bindingProperty);
        } else {
            throw new IllegalArgumentException("Unrecognised binding property type: " + bindingProperty);
        }
    }

    /**
     * Generates a binary property for the specified basic property.
     *
     * @param node
     *         the AST node of the property.
     * @param property
     *         the basic property that we are generating for.
     */
    private BinaryProperty generateBasicProperty(AstProperty node, ConfigBasicProperty property) {
        var rawValues = node.getValues();
        var types = new PrimitiveType[rawValues.length];
        var values = new Object[rawValues.length];
        for (var valueIndex = 0; valueIndex < rawValues.length; valueIndex++) {
            types[valueIndex] = property.getComponents()[valueIndex];
            values[valueIndex] = rawValues[valueIndex].accept(this);
        }
        if (values.length == 1) {
            Boolean rule = null;
            if (property.getRules().contains(ConfigRules.EMIT_EMPTY_IF_TRUE)) {
                rule = Boolean.TRUE;
            } else if (property.getRules().contains(ConfigRules.EMIT_EMPTY_IF_FALSE)) {
                rule = Boolean.FALSE;
            }
            if (rule != null) {
                if (values[0] == rule) {
                    // emit empty if the true matches.
                    values = null;
                } else {
                    // Skip if the rule does not match.
                    return null;
                }
            }
        }
        return new BinaryProperty(property.getOpcode(), types, values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String visit(AstValueString value) {
        return value.getText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer visit(AstValueInteger value) {
        return value.getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long visit(AstValueLong value) {
        return value.getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean visit(AstValueBoolean value) {
        return value.isValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrimitiveType visit(AstValueType value) {
        return value.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(AstValueConstant value) {
        return symbolTable.lookupConstant(value.getName().getText()).getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String visit(AstIdentifier identifier) {
        return identifier.getText();
    }
}

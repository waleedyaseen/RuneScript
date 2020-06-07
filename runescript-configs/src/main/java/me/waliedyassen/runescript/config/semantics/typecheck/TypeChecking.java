/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package me.waliedyassen.runescript.config.semantics.typecheck;

import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.config.ast.AstProperty;
import me.waliedyassen.runescript.config.ast.value.AstValueInteger;
import me.waliedyassen.runescript.config.ast.value.AstValueLong;
import me.waliedyassen.runescript.config.ast.value.AstValueString;
import me.waliedyassen.runescript.config.ast.visitor.AstTreeVisitor;
import me.waliedyassen.runescript.config.binding.ConfigBinding;
import me.waliedyassen.runescript.config.semantics.SemanticChecker;
import me.waliedyassen.runescript.config.semantics.SemanticError;
import me.waliedyassen.runescript.config.symbol.SymbolTable;
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
    private final SemanticChecker checker;

    /**
     * The symbol table to use for looking up.
     */
    private final SymbolTable table;

    /**
     * The configuration group we are checking for.
     */
    private final ConfigBinding<?> binding;

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
        var types = variable.getType().getPrimitives();
        var values = property.getValues();
        if (types.length != values.length) {
            checker.reportError(new SemanticError(property, "Argument mismatch: expected " + types.length + " argument(s) but got " + values.length + " argument(s)"));
            return null;
        }
        for (var index = 0; index < values.length; index++) {
            var type = (PrimitiveType) values[index].visit(this);
            if (type != types[index]) {
                checker.reportError(new SemanticError(property.getValues()[index], "Type mismatch: cannot convert from " + type.getRepresentation() + " to " + types[index].getRepresentation()));
            }
        }
        return DEFAULT;
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
    public PrimitiveType visit(AstValueString value) {
        return PrimitiveType.STRING;
    }
}

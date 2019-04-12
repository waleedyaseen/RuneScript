/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.semantics.checkers;

import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.ast.AstParameter;
import me.waliedyassen.runescript.compiler.ast.AstScript;
import me.waliedyassen.runescript.compiler.ast.expr.AstVariableExpression;
import me.waliedyassen.runescript.compiler.ast.stmt.AstBlockStatement;
import me.waliedyassen.runescript.compiler.ast.stmt.AstVariableDeclaration;
import me.waliedyassen.runescript.compiler.ast.stmt.AstVariableInitializer;
import me.waliedyassen.runescript.compiler.ast.visitor.AstTreeVisitor;
import me.waliedyassen.runescript.compiler.semantics.SemanticChecker;
import me.waliedyassen.runescript.compiler.semantics.SemanticError;
import me.waliedyassen.runescript.compiler.semantics.scope.Scope;
import me.waliedyassen.runescript.compiler.semantics.scope.VariableInfo;
import me.waliedyassen.runescript.compiler.util.VariableScope;

import java.util.Stack;

/**
 * Represents the local variables resolver semantic checking.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class VariableScopeBuilder extends AstTreeVisitor {

    /**
     * The owner {@link SemanticChecker} instance of this local resolver.
     */
    private final SemanticChecker checker;

    /**
     * The scopes stack.
     */
    private final Stack<Scope> scopes = new Stack<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(AstParameter parameter) {
        scopes.lastElement().declareLocalVariable(parameter.getName().getText(), parameter.getType());
        return super.visit(parameter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(AstVariableDeclaration declaration) {
        var name = declaration.getName();
        var variable = resolveVariable(VariableScope.LOCAL, name.getText());
        if (variable != null) {
            checker.reportError(new SemanticError(name, String.format("Duplicate local variable %s", name.getText())));
        } else {
            variable = scopes.lastElement().declareLocalVariable(name.getText(), declaration.getType());
            declaration.setVariable(variable);
        }
        return super.visit(declaration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(AstVariableInitializer variableInitializer) {
        var name = variableInitializer.getName();
        var variable = resolveVariable(variableInitializer.getScope(), name.getText());
        if (variable == null) {
            checker.reportError(new SemanticError(variableInitializer, String.format("%s cannot be resolved to a variable", name.getText())));
        } else {
            variableInitializer.setVariable(variable);
        }
        return super.visit(variableInitializer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(AstVariableExpression variableExpression) {
        var name = variableExpression.getName();
        var variable = resolveVariable(variableExpression.getScope(), name.getText());
        if (variable == null) {
            checker.reportError(new SemanticError(name, String.format("%s cannot be resolved to a variable", name.getText())));
        } else {
            variableExpression.setVariable(variable);
        }
        return super.visit(variableExpression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enter(AstBlockStatement blockStatement) {
        var scope = scopes.lastElement().createChild();
        scopes.push(scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exit(AstBlockStatement blockStatement) {
        scopes.pop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enter(AstScript script) {
        scopes.push(new Scope(null));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exit(AstScript script) {
        scopes.pop();
    }

    /**
     * Attempts to resolve the variable with the given {@link VariableScope scope} and {@code name}.
     *
     * @param scope
     *         the variable scope to resolve.
     * @param name
     *         the variable name to resolve.
     *
     * @return the resolved {@link VariableInfo} if it was present otherwise {@code null}.
     */
    private VariableInfo resolveVariable(VariableScope scope, String name) {
        if (scope == VariableScope.GLOBAL) {
            // we do not support that yet.
            return null;
        }
        return scopes.lastElement().getLocalVariable(name);
    }
}

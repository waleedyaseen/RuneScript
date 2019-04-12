/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
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
import me.waliedyassen.runescript.compiler.ast.visitor.AstTreeVisitor;
import me.waliedyassen.runescript.compiler.semantics.SemanticChecker;
import me.waliedyassen.runescript.compiler.semantics.SemanticError;
import me.waliedyassen.runescript.compiler.semantics.scope.Scope;

import java.util.Stack;

/**
 * Represents the local variables resolver semantic checking.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class LocalResolver extends AstTreeVisitor {

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
        scopes.lastElement().declareVariable(parameter.getName().getText(), parameter.getType());
        return super.visit(parameter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(AstVariableDeclaration declaration) {
        var scope = scopes.lastElement();
        var name = declaration.getName();
        if (scope.getVariable(declaration.getName().getText()) != null) {
            checker.reportError(new SemanticError(name, String.format("Duplicate local variable %s", name.getText())));
        } else {
            scopes.lastElement().declareVariable(name.getText(), declaration.getType());
        }
        return super.visit(declaration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(AstVariableExpression variable) {
        var scope = scopes.lastElement();
        var info = scope.getVariable(variable.getName().getText());
        if (info != null) {
            checker.reportError(new SemanticError(variable, String.format("%s cannot be resolved to a local variable", info.getName())));
        }
        return super.visit(variable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enter(AstBlockStatement blockStatement) {
        scopes.push(scopes.lastElement().createChild());
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
}

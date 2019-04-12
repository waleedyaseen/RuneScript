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
        if (scope.getVariable(declaration.getName().getText()) != null) {
            checker.reportError(new SemanticError(declaration, "Variable '" + declaration.getName().getText() + "' is already defined in the scope"));
        } else {
            scopes.lastElement().declareVariable(declaration.getName().getText(), declaration.getType());
        }
        return super.visit(declaration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(AstVariableExpression variable) {
        var scope = scopes.lastElement();
        var info =scope.getVariable(variable.getName().getText());
        if (variable != null) {
            // NOOP
        } else {
            checker.reportError(new SemanticError(variable, "Variable '" + variable.getName().getText() + "' is not defined in the scope"));
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

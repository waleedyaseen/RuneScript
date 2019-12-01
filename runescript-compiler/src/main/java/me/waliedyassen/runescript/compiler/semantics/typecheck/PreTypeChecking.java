/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.semantics.typecheck;

import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.ast.AstParameter;
import me.waliedyassen.runescript.compiler.ast.AstScript;
import me.waliedyassen.runescript.compiler.ast.expr.AstArrayExpression;
import me.waliedyassen.runescript.compiler.ast.expr.AstDynamic;
import me.waliedyassen.runescript.compiler.ast.expr.AstVariableExpression;
import me.waliedyassen.runescript.compiler.ast.stmt.*;
import me.waliedyassen.runescript.compiler.ast.visitor.AstTreeVisitor;
import me.waliedyassen.runescript.compiler.semantics.SemanticChecker;
import me.waliedyassen.runescript.compiler.semantics.SemanticError;
import me.waliedyassen.runescript.compiler.semantics.scope.Scope;
import me.waliedyassen.runescript.compiler.symbol.SymbolTable;
import me.waliedyassen.runescript.compiler.symbol.impl.script.Annotation;
import me.waliedyassen.runescript.compiler.symbol.impl.variable.VariableInfo;
import me.waliedyassen.runescript.compiler.type.ArrayReference;
import me.waliedyassen.runescript.compiler.util.VariableScope;
import me.waliedyassen.runescript.type.PrimitiveType;
import me.waliedyassen.runescript.type.TupleType;
import me.waliedyassen.runescript.type.Type;
import me.waliedyassen.runescript.type.TypeUtil;

import java.util.*;

/**
 * Contains all of the procedures and functions that will be applied right before we perform our type checking semantic
 * checks.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class PreTypeChecking extends AstTreeVisitor {

    /**
     * The stack which holds all of the current scopes.
     */
    private final Stack<Scope> scopes = new Stack<>();

    /**
     * The owner {@link SemanticChecker} object.
     */
    private final SemanticChecker checker;

    /**
     * The symbol table to register the declared scripts in.
     */
    private final SymbolTable symbolTable;

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstScript script) {
        // create the annotations list.
        Map<String, Annotation> annotations;
        if (script.getAnnotations().size() > 0) {
            annotations = new HashMap<>();
            for (var annotation : script.getAnnotations()) {
                var name = annotation.getName().getText().toLowerCase();
                if (annotations.containsKey(name)) {
                    checker.reportError(new SemanticError(annotation.getName(), String.format("The annotation %s is already defined for this script", name)));
                } else {
                    annotations.put(name, new Annotation(name, annotation.getValue().getValue()));
                }
            }
        } else {
            annotations = Collections.emptyMap();
        }
        // resolve the script trigger type.
        var triggerName = script.getTrigger();
        var trigger = checker.getEnvironment().lookupTrigger(triggerName.getText());
        // check if the script trigger type is a valid trigger type, if not produce and error.
        if (trigger == null) {
            checker.reportError(new SemanticError(triggerName, String.format("%s cannot be resolved to a trigger", triggerName.getText())));
        } else {
            // check if the trigger supports return values.
            if (script.getType() != PrimitiveType.VOID && !trigger.hasReturns()) {
                checker.reportError(new SemanticError(triggerName, String.format("The trigger type '%s' does not allow return values", trigger.getRepresentation())));
            }
            // check if the trigger matches return types.
            if (trigger.getReturnTypes() != null && !script.getType().equals(new TupleType(trigger.getReturnTypes()))) {
                checker.reportError(new SemanticError(triggerName, String.format("The trigger type '%s' requires return values of type '%s'", trigger.getRepresentation(), TypeUtil.createRepresentation(trigger.getReturnTypes()))));
            }
            // check if the trigger supports parameters.
            if (script.getParameters().length > 0 && !trigger.hasArguments()) {
                checker.reportError(new SemanticError(triggerName, String.format("The trigger type '%s' does not allow parameters", trigger.getRepresentation())));
            }
            // check if the trigger matches parameters types.
            var actual = Arrays.stream(script.getParameters()).map(AstParameter::getType).toArray(Type[]::new);
            var expected = trigger.getArgumentTypes();
            if (expected != null && (actual.length != expected.length || !Arrays.equals(actual, expected))) {
                checker.reportError(new SemanticError(triggerName, String.format("The trigger type '%s' requires parameters of type '%s'", trigger.getRepresentation(), TypeUtil.createRepresentation(expected))));
            }
            // check if the script is already defined in the symbol table, and define it if it was not, or produce an error if it was a duplicate.
            var name = script.getName();
            if (symbolTable.lookupScript(trigger, name.getText()) != null) {
                checker.reportError(new SemanticError(name, String.format("The script '%s' is already defined", name.getText())));
            } else {
                symbolTable.defineScript(annotations, trigger, name.getText(), script.getType(), Arrays.stream(script.getParameters()).map(AstParameter::getType).toArray(Type[]::new));
            }
        }
        return super.visit(script);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstParameter parameter) {
        var type = parameter.getType();
        // check if the type is an array reference and declare the array if it is.
        if (type instanceof ArrayReference) {
            var reference = (ArrayReference) type;
            scopes.lastElement().declareArray(reference.getIndex(), parameter.getName().getText(), reference.getType());
        } else {
            scopes.lastElement().declareLocalVariable(parameter.getName().getText(), parameter.getType());
        }
        return super.visit(parameter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstVariableDeclaration declaration) {
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
    public Void visit(AstVariableInitializer variableInitializer) {
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
    public Void visit(AstVariableExpression variableExpression) {
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
    public Void visit(AstArrayDeclaration declaration) {
        var name = declaration.getName();
        var array = scopes.lastElement().getArray(name.getText());
        if (array != null) {
            checker.reportError(new SemanticError(name, String.format("Duplicate array %s", name.getText())));
        } else {
            array = scopes.lastElement().declareArray(name.getText(), declaration.getType());
            declaration.setArray(array);
        }
        return super.visit(declaration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstArrayInitializer arrayInitializer) {
        var name = arrayInitializer.getName();
        var array = scopes.lastElement().getArray(name.getText());
        if (array == null) {
            checker.reportError(new SemanticError(name, String.format("%s cannot be resolved to an array", name.getText())));
        } else {
            arrayInitializer.setArray(array);
        }
        return super.visit(arrayInitializer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstArrayExpression arrayExpression) {
        var name = arrayExpression.getName();
        var array = scopes.lastElement().getArray(name.getText());
        if (array == null) {
            checker.reportError(new SemanticError(name, String.format("%s cannot be resolved to an array", name.getText())));
        } else {
            arrayExpression.setArray(array);
        }
        return super.visit(arrayExpression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstDynamic dynamic) {
        var arrayInfo = scopes.lastElement().getArray(dynamic.getName().getText());
        if (arrayInfo != null) {
            dynamic.setType(new ArrayReference(arrayInfo.getType(), arrayInfo.getIndex()));
        }
        return super.visit(dynamic);
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
        if (scope == VariableScope.LOCAL) {
            return scopes.lastElement().getLocalVariable(name);
        } else {
            return symbolTable.lookupVariable(name);
        }
    }
}

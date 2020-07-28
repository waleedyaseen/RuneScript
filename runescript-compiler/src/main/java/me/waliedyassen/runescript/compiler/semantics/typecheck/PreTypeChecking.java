/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.semantics.typecheck;

import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.compiler.ast.AstNode;
import me.waliedyassen.runescript.compiler.ast.AstParameter;
import me.waliedyassen.runescript.compiler.ast.AstScript;
import me.waliedyassen.runescript.compiler.ast.expr.*;
import me.waliedyassen.runescript.compiler.ast.stmt.AstArrayDeclaration;
import me.waliedyassen.runescript.compiler.ast.stmt.AstBlockStatement;
import me.waliedyassen.runescript.compiler.ast.stmt.AstVariableDeclaration;
import me.waliedyassen.runescript.compiler.ast.stmt.AstVariableInitializer;
import me.waliedyassen.runescript.compiler.ast.visitor.AstTreeVisitor;
import me.waliedyassen.runescript.compiler.codegen.local.Local;
import me.waliedyassen.runescript.compiler.semantics.SemanticChecker;
import me.waliedyassen.runescript.compiler.semantics.SemanticError;
import me.waliedyassen.runescript.compiler.semantics.scope.Scope;
import me.waliedyassen.runescript.compiler.symbol.ScriptSymbolTable;
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
    private final ScriptSymbolTable symbolTable;

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
                    reportError(new SemanticError(annotation.getName(), String.format("The annotation %s is already defined for this script", name)));
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
            reportError(new SemanticError(triggerName, String.format("%s cannot be resolved to a trigger", triggerName.getText())));
        } else {
            // check if the trigger supports return values.
            if (script.getType() != PrimitiveType.VOID && !trigger.hasReturns()) {
                reportError(new SemanticError(triggerName, String.format("The trigger type '%s' does not allow return values", trigger.getRepresentation())));
            }
            // check if the trigger matches return types.
            if (trigger.getReturnTypes() != null && !script.getType().equals(new TupleType(trigger.getReturnTypes()))) {
                reportError(new SemanticError(triggerName, String.format("The trigger type '%s' requires return values of type '%s'", trigger.getRepresentation(), TypeUtil.createRepresentation(trigger.getReturnTypes()))));
            }
            // check if the trigger supports parameters.
            if (script.getParameters().length > 0 && !trigger.hasArguments()) {
                reportError(new SemanticError(triggerName, String.format("The trigger type '%s' does not allow parameters", trigger.getRepresentation())));
            }
            // check if the trigger matches parameters types.
            var actual = Arrays.stream(script.getParameters()).map(AstParameter::getType).toArray(Type[]::new);
            var expected = trigger.getArgumentTypes();
            if (expected != null && (actual.length != expected.length || !Arrays.equals(actual, expected))) {
                reportError(new SemanticError(triggerName, String.format("The trigger type '%s' requires parameters of type '%s'", trigger.getRepresentation(), TypeUtil.createRepresentation(expected))));
            }
            // check if the script is already defined in the symbol table, and define it if it was not, or produce an error if it was a duplicate.
            var name = AstExpression.extractNameText(script.getName());
            var existing = symbolTable.lookupScript(trigger, name);
            if (existing != null) {
                if (annotations.containsKey("id")) {
                    reportError(new SemanticError(script.getName(), "You cannot use the 'id' annotation on overriding scripts"));
                }
                if (checker.isAllowOverriding()) {
                    var existingArguments = TypeUtil.flatten(existing.getArguments());
                    if (!Arrays.equals(actual, existingArguments)) {
                        reportError(new SemanticError(script.getName(), String.format("Mismatch overriding scripts arguments: (%s) and (%s)", TypeUtil.createRepresentation(actual), TypeUtil.createRepresentation(existingArguments))));
                    }
                    if (!existing.getType().equals(script.getType())) {
                        reportError(new SemanticError(script.getName(), String.format("Mismatch overriding scripts return type: (%s) and (%s)", TypeUtil.createRepresentation(script.getType()), TypeUtil.createRepresentation(existing.getType()))));
                    }
                } else {
                    reportError(new SemanticError(script.getName(), String.format("The script '%s' is already defined", name)));
                }
            } else {
                Integer predefinedId = null;
                if (annotations.containsKey("id")) {
                    predefinedId = annotations.get("id").getValue();
                }
                symbolTable.defineScript(annotations, trigger, name, script.getType(), Arrays.stream(script.getParameters()).map(AstParameter::getType).toArray(Type[]::new), predefinedId);
            }
        }
        return super.visit(script);
    }

    private void reportError(SemanticError semanticError) {
        checker.reportError(semanticError);
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
        var result = super.visit(declaration);
        var name = declaration.getName();
        var variable = resolveLocalVariable(name.getText());
        if (variable != null) {
            reportError(new SemanticError(name, String.format("Duplicate local variable %s", name.getText())));
        } else {
            scopes.lastElement().declareLocalVariable(name.getText(), declaration.getType());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstVariableInitializer variableInitializer) {
        var count = variableInitializer.getVariables().length;
        for (var index = 0; index < count; index++) {
            var variable = variableInitializer.getVariables()[index];
            var name = variable.getName().getText();
            if (variable instanceof AstArrayVariable) {
                var arrayVariable = (AstArrayVariable) variable;
                var arrayInfo = scopes.lastElement().getArray(name);
                if (arrayInfo == null) {
                    reportError(new SemanticError(variableInitializer, String.format("%s cannot be resolved to an array", name)));
                } else {
                    arrayVariable.setArrayInfo(arrayInfo);
                }
            } else {
                var scopedVariable = (AstScopedVariable) variable;
                scopedVariable.setType(checkVariableResolving(scopedVariable.getName(), scopedVariable.getScope(), name));
            }
        }
        return super.visit(variableInitializer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstVariableExpression variableExpression) {
        var name = variableExpression.getName();
        variableExpression.setType(checkVariableResolving(name, variableExpression.getScope(), name.getText()));
        return super.visit(variableExpression);
    }

    /**
     * Attempts to resolve the variable with the specified name.
     *
     * @param node
     *         the node to throw the errors at when the resolve fails.
     * @param scope
     *         the scope of the variable we are trying to resolve.
     * @param name
     *         the name of the variable we are trying to resolve.
     *
     * @return the type of the variable that we resolved or {@link PrimitiveType#UNDEFINED}.
     */
    private Type checkVariableResolving(AstNode node, VariableScope scope, String name) {
        switch (scope) {
            case LOCAL: {
                var local = resolveLocalVariable(name);
                if (local == null) {
                    reportError(new SemanticError(node, String.format("%s cannot be resolved to a local variable", name)));
                    return PrimitiveType.UNDEFINED;
                }
                return local.getType();
            }
            case GLOBAL: {
                var config = symbolTable.lookupVariable(name);
                if (config == null) {
                    reportError(new SemanticError(node, String.format("%s cannot be resolved to a global variable", name)));
                    return PrimitiveType.UNDEFINED;
                }
                switch ((PrimitiveType) config.getType()) {
                    case VARCSTR:
                        return PrimitiveType.STRING;
                    case VAR:
                        if (config.getContentType() == null) {
                            throw new IllegalStateException("Expected content type to be present");
                        }
                        return config.getContentType();
                    default:
                        return PrimitiveType.INT;
                }
            }
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstArrayDeclaration declaration) {
        var name = declaration.getName();
        var array = scopes.lastElement().getArray(name.getText());
        if (array != null) {
            reportError(new SemanticError(name, String.format("Duplicate array %s", name.getText())));
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
    public Void visit(AstArrayExpression arrayExpression) {
        var name = arrayExpression.getName();
        var array = scopes.lastElement().getArray(name.getText());
        if (array == null) {
            reportError(new SemanticError(name, String.format("%s cannot be resolved to an array", name.getText())));
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
     * Resolves the local variable with the specified {@code name}.
     *
     * @param name
     *         the name of the local variable that we want to resolve.
     *
     * @return the {@link VariableInfo} of the name.
     */
    private Local resolveLocalVariable(String name) {
        return scopes.lastElement().getLocalVariable(name);
    }
}

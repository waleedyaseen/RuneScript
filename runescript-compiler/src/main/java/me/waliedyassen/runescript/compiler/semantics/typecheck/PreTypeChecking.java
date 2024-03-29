/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.semantics.typecheck;

import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.codegen.local.Local;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.semantics.SemanticChecker;
import me.waliedyassen.runescript.compiler.semantics.SemanticError;
import me.waliedyassen.runescript.compiler.semantics.scope.Scope;
import me.waliedyassen.runescript.compiler.symbol.ScriptSymbolTable;
import me.waliedyassen.runescript.compiler.symbol.impl.script.Annotation;
import me.waliedyassen.runescript.compiler.symbol.impl.script.ScriptInfo;
import me.waliedyassen.runescript.compiler.syntax.ParameterSyntax;
import me.waliedyassen.runescript.compiler.syntax.ScriptSyntax;
import me.waliedyassen.runescript.compiler.syntax.Syntax;
import me.waliedyassen.runescript.compiler.syntax.expr.*;
import me.waliedyassen.runescript.compiler.syntax.stmt.ArrayDeclarationSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.BlockStatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.VariableDeclarationSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.VariableInitializerSyntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxTreeVisitor;
import me.waliedyassen.runescript.compiler.type.ArrayReference;
import me.waliedyassen.runescript.compiler.util.VariableScope;
import me.waliedyassen.runescript.type.Type;
import me.waliedyassen.runescript.type.TypeUtil;
import me.waliedyassen.runescript.type.primitive.PrimitiveType;
import me.waliedyassen.runescript.type.tuple.TupleType;

import java.util.*;

/**
 * Contains all of the procedures and functions that will be applied right before we perform our type checking semantic
 * checks.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class PreTypeChecking extends SyntaxTreeVisitor {

    /**
     * The stack which holds all the current scopes.
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
    public Void visit(ScriptSyntax script) {
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
        var triggerName = script.getName().getTrigger();
        var trigger = checker.getEnvironment().lookupTrigger(triggerName.getText());
        scopes.push(new Scope(null));
        for (var parameter : script.getParameters()) {
            parameter.accept(this);
        }
        // check if the script trigger type is a valid trigger type, if not produce and error.
        if (trigger == null) {
            reportError(new SemanticError(triggerName, String.format("%s cannot be resolved to a trigger", triggerName.getText())));
        } else {
            // check if the trigger supports return values.
            if (!TypeUtil.isVoid(script.getType()) && !trigger.hasReturns()) {
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
            var actual = Arrays.stream(script.getParameters()).map(ParameterSyntax::getType).toArray(Type[]::new);
            var expected = trigger.getArgumentTypes();
            if (expected != null && (actual.length != expected.length || !Arrays.equals(actual, expected))) {
                reportError(new SemanticError(triggerName, String.format("The trigger type '%s' requires parameters of type '%s'", trigger.getRepresentation(), TypeUtil.createRepresentation(expected))));
            }
            // check if the script is already defined in the symbol table, and define it if it was not, or produce an error if it was a duplicate.
            var name = script.getName();
            var existing = symbolTable.lookupScript(name.toText());
            if (existing != null) {
                if (annotations.containsKey("id")) {
                    reportError(new SemanticError(name, "You cannot use the 'id' annotation on overriding scripts"));
                }
                if (checker.isAllowOverriding()) {
                    var existingArguments = TypeUtil.flatten(existing.getArguments());
                    if (!Arrays.equals(actual, existingArguments)) {
                        reportError(new SemanticError(name, String.format("Mismatch overriding scripts arguments: (%s) and (%s)", TypeUtil.createRepresentation(actual), TypeUtil.createRepresentation(existingArguments))));
                    }
                    if (!existing.getType().equals(script.getType())) {
                        reportError(new SemanticError(name, String.format("Mismatch overriding scripts return type: (%s) and (%s)", TypeUtil.createRepresentation(script.getType()), TypeUtil.createRepresentation(existing.getType()))));
                    }
                } else {
                    reportError(new SemanticError(name, String.format("The script '%s' is already defined", name.toText())));
                }
            } else {
                Integer predefinedId = null;
                if (annotations.containsKey("id")) {
                    predefinedId = annotations.get("id").getValue();
                }
                if (predefinedId == null) {
                    predefinedId = -1;
                }
                var scriptInfo = new ScriptInfo(name.getName() != null ? name.getName().getText() : null, predefinedId, trigger, script.getType(), actual);
                // TODO(WAlied): ID is -1 in here
                symbolTable.defineScript(scriptInfo);
            }
        }
        script.getCode().accept(this);
        scopes.pop();
        return null;
    }

    private void reportError(SemanticError semanticError) {
        checker.reportError(semanticError);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(ParameterSyntax parameter) {
        var typeRaw = parameter.getTypeToken().getLexeme();
        if (parameter.getTypeToken().getKind() == Kind.ARRAY_TYPE) {
            var type = PrimitiveType.forRepresentation(typeRaw.substring(0, typeRaw.length() - "array".length()));
            parameter.setType(new ArrayReference(type, parameter.getIndex()));
        } else {
            parameter.setType(PrimitiveType.forRepresentation(typeRaw));
            if (!(parameter.getType() instanceof PrimitiveType) || !((PrimitiveType) parameter.getType()).isDeclarable()) {
                reportError(new SemanticError(parameter, "Illegal type: " + typeRaw));
            }
        }
        // check if the type is an array reference and declare the array if it is.
        if (parameter.getType() instanceof ArrayReference) {
            var reference = (ArrayReference) parameter.getType();
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
    public Void visit(VariableDeclarationSyntax declaration) {
        var result = super.visit(declaration);
        var name = declaration.getName();
        var typeRaw = declaration.getDefineToken().getLexeme();
        declaration.setType(PrimitiveType.forRepresentation(typeRaw.substring("def_".length())));
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
    public Void visit(VariableInitializerSyntax variableInitializer) {
        var count = variableInitializer.getVariables().length;
        for (var index = 0; index < count; index++) {
            var variable = variableInitializer.getVariables()[index];
            var name = variable.getName().getText();
            if (variable instanceof ArrayVariableSyntax arrayVariable) {
                var arrayInfo = scopes.lastElement().getArray(name);
                if (arrayInfo == null) {
                    reportError(new SemanticError(variableInitializer, String.format("%s cannot be resolved to an array", name)));
                } else {
                    arrayVariable.setArrayInfo(arrayInfo);
                    variable.setType(arrayInfo.getType());
                }
            } else {
                var scopedVariable = (ScopedVariableSyntax) variable;
                variable.setType(checkVariableResolving(scopedVariable.getName(), scopedVariable.getScope(), name));
            }
        }
        return super.visit(variableInitializer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(VariableExpressionSyntax variableExpression) {
        var name = variableExpression.getName();
        variableExpression.setType(checkVariableResolving(name, variableExpression.getScope(), name.getText()));
        return super.visit(variableExpression);
    }

    /**
     * Attempts to resolve the variable with the specified name.
     *
     * @param node  the node to throw the errors at when the resolve fails.
     * @param scope the scope of the variable we are trying to resolve.
     * @param name  the name of the variable we are trying to resolve.
     * @return the type of the variable that we resolved or {@link PrimitiveType#UNDEFINED}.
     */
    private Type checkVariableResolving(Syntax node, VariableScope scope, String name) {
        switch (scope) {
            case LOCAL: {
                var local = resolveLocalVariable(name);
                if (local == null) {
                    reportError(new SemanticError(node, String.format("%s cannot be resolved to a local variable", name)));
                    return PrimitiveType.UNDEFINED.INSTANCE;
                }
                return local.getType();
            }
            case GLOBAL: {
                var type = symbolTable.lookupVariableType(name);
                if (type == null) {
                    reportError(new SemanticError(node, String.format("%s cannot be resolved to a global variable", name)));
                    return PrimitiveType.UNDEFINED.INSTANCE;
                }
                return type;
            }
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(ArrayDeclarationSyntax declaration) {
        var typeRaw = declaration.getDefineToken().getLexeme();
        var type = PrimitiveType.forRepresentation(typeRaw.substring("def_".length()));
        var name = declaration.getName();
        var array = scopes.lastElement().getArray(name.getText());
        declaration.setType(type);
        if (array != null) {
            reportError(new SemanticError(name, String.format("Duplicate array %s", name.getText())));
        } else {
            array = scopes.lastElement().declareArray(name.getText(), type);
            declaration.setArray(array);
        }
        return super.visit(declaration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(ArrayElementSyntax arrayExpression) {
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
    public Void visit(DynamicSyntax dynamic) {
        var arrayInfo = scopes.lastElement().getArray(dynamic.getName().getText());
        if (arrayInfo != null) {
            dynamic.setType(new ArrayReference(arrayInfo.getType(), arrayInfo.getId()));
        }
        return super.visit(dynamic);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enter(BlockStatementSyntax blockStatement) {
        var scope = scopes.lastElement().createChild();
        scopes.push(scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exit(BlockStatementSyntax blockStatement) {
        scopes.pop();
    }

    /**
     * Resolves the local variable with the specified {@code name}.
     *
     * @param name the name of the local variable that we want to resolve.
     * @return the {@link Local} of the name.
     */
    private Local resolveLocalVariable(String name) {
        return scopes.lastElement().getLocalVariable(name);
    }
}

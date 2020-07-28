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
import me.waliedyassen.runescript.compiler.ast.AstNodeBase;
import me.waliedyassen.runescript.compiler.ast.AstParameter;
import me.waliedyassen.runescript.compiler.ast.AstScript;
import me.waliedyassen.runescript.compiler.ast.expr.*;
import me.waliedyassen.runescript.compiler.ast.expr.literal.*;
import me.waliedyassen.runescript.compiler.ast.stmt.*;
import me.waliedyassen.runescript.compiler.ast.stmt.conditional.AstIfStatement;
import me.waliedyassen.runescript.compiler.ast.stmt.conditional.AstWhileStatement;
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;
import me.waliedyassen.runescript.compiler.semantics.SemanticChecker;
import me.waliedyassen.runescript.compiler.semantics.SemanticError;
import me.waliedyassen.runescript.compiler.symbol.ScriptSymbolTable;
import me.waliedyassen.runescript.compiler.symbol.impl.script.ScriptInfo;
import me.waliedyassen.runescript.compiler.util.Operator;
import me.waliedyassen.runescript.compiler.util.trigger.TriggerType;
import me.waliedyassen.runescript.type.*;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Represents the type checking semantic analysis.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class TypeChecking implements AstVisitor<Type, Type> {

    // TODO: return PrimitiveType.VOID should be changed to something else that would stop the execution of the type checking
    // for the parent nodes, just to skip the redundant type checking.

    /**
     * The owner {@link SemanticChecker} object.
     */
    private final SemanticChecker checker;

    /**
     * The symbol table that we will be using for getting symbol information.
     */
    private final ScriptSymbolTable symbolTable;

    /**
     * The trigger type of the hooks.
     */
    private final TriggerType hookTriggerType;

    /**
     * The script which we are currently type checking.
     */
    private AstScript script;

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstScript script) {
        this.script = script;
        script.getCode().accept(this);
        return script.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstParameter parameter) {
        return parameter.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstLiteralBool bool) {
        return bool.setType(PrimitiveType.BOOLEAN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstLiteralInteger integer) {
        return integer.setType(PrimitiveType.INT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstLiteralLong longInteger) {
        return longInteger.setType(PrimitiveType.LONG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstLiteralString string) {
        if (symbolTable.lookupGraphic(string.getValue()) != null) {
            return string.setType(PrimitiveType.GRAPHIC);
        } else {
            return string.setType(PrimitiveType.STRING);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstLiteralCoordgrid coordgrid) {
        return coordgrid.setType(PrimitiveType.COORDGRID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstConcatenation concatenation) {
        for (var expr : concatenation.getExpressions()) {
            checkType(expr, PrimitiveType.STRING, expr.accept(this));
        }
        return concatenation.setType(PrimitiveType.STRING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstComponent component) {
        var interfaceInfo = symbolTable.lookupInterface(component.getParentInterface().getText());
        if (interfaceInfo == null) {
            checker.reportError(new SemanticError(component.getParentInterface(), String.format("Could not resolve interface with the name '%s'", component.getParentInterface().getText())));
        } else {
            component.getParentInterface().setType(PrimitiveType.INTERFACE);
            if (!(component.getComponent() instanceof AstLiteralInteger) && interfaceInfo.lookupComponent(String.valueOf(component.getComponentName())) == null) {
                checker.reportError(new SemanticError(component.getParentInterface(), String.format("Could not resolve component with the name '%s' in '%s'", component.getComponentName(), component.getParentInterface().getText())));
            }
        }
        return component.setType(PrimitiveType.COMPONENT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstVariableExpression variableExpression) {
        return variableExpression.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstArrayExpression arrayExpression) {
        if (arrayExpression.getArray() == null) {
            return PrimitiveType.UNDEFINED;
        }
        return arrayExpression.setType(arrayExpression.getArray().getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstHook hook) {
        if (hookTriggerType == null) {
            checker.reportError(new SemanticError(hook, "Hooks are not allowed"));
        } else if (hook.getName() != null) {
            var parentInfo = symbolTable.lookupCommand(((AstCommand) hook.getParent()).getName().getText());
            var scriptInfo = symbolTable.lookupScript(hookTriggerType, hook.getName().getText());
            if (scriptInfo == null) {
                checker.reportError(new SemanticError(hook.getName(), String.format("Could not resolve %s script with the name '%s'", hookTriggerType.getRepresentation(), hook.getName().getText())));
            } else {
                checkCallApplicable(hook, scriptInfo, hook.getArguments());
            }
            if (parentInfo != null) {
                var expected = parentInfo.getHookType();
                var transmits = hook.getTransmits();
                if (expected != null) {
                    if (transmits.length == 0) {
                        checker.reportError(new SemanticError(hook, String.format("Expected a transmit list of type '%s'", expected.getRepresentation())));
                    } else {
                        for (var transmit : transmits) {
                            checkType(transmit, expected, transmit.accept(this));
                        }
                    }
                } else if (transmits.length != 0) {
                    checker.reportError(new SemanticError(hook, "Unexpected transmit list"));
                }
            }
        }
        return hook.setType(PrimitiveType.HOOK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstCall call) {
        var name = call.getName();
        var info = symbolTable.lookupScript(call.getTriggerType(), name.getText());
        if (info == null) {
            checker.reportError(new SemanticError(call, String.format("Could not resolve %s script with the name '%s'", call.getTriggerType().getRepresentation(), name.getText())));
            return PrimitiveType.UNDEFINED;
        } else {
            checkCallApplicable(call, info, call.getArguments());
        }
        return call.setType(info.getType());
    }

    /**
     * Checks whether or ont a script call is applicable.
     *
     * @param call
     *         the AST node object of the call.
     * @param info
     *         the information of the script we are calling.
     * @param arguments
     *         the arguments that are used in the call.
     */
    private void checkCallApplicable(AstNodeBase call, ScriptInfo info, AstExpression[] arguments) {
        var types = new Type[arguments.length];
        for (int index = 0; index < arguments.length; index++) {
            types[index] = arguments[index].accept(this);
        }
        var actual = TypeUtil.flatten(types);
        var expected = info.getArguments();
        if (!isCallApplicable(expected, actual)) {
            checker.reportError(new SemanticError(call, String.format("The script %s(%s) is not applicable for the arguments (%s)", info.getName(), TypeUtil.createRepresentation(actual), TypeUtil.createRepresentation(expected))));
        }
    }

    /**
     * Checks whether or not a script call is applicable.
     *
     * @param expected
     *         the expected arguments of the call.
     * @param actual
     *         the actual arguments of the call.
     *
     * @return <code>true</code> if it is applicable otherwise <code>false</code>.
     */
    private boolean isCallApplicable(Type[] expected, Type[] actual) {
        return actual.length == expected.length && Arrays.equals(actual, expected);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstDynamic dynamic) {
        // If the type was determined by the PreTypeChecking, we just use that type instead.
        if (dynamic.getType() != null) {
            return dynamic.getType();
        }
        var name = dynamic.getName();
        var commandInfo = symbolTable.lookupCommand(name.getText());
        if (commandInfo != null) {
            if (commandInfo.getArguments().length > 0) {
                checker.reportError(new SemanticError(name, String.format("The command %s(%s) is not applicable for the arguments ()", name.getText(), TypeUtil.createRepresentation(commandInfo.getArguments()))));
            }
            return dynamic.setType(commandInfo.getType());
        }
        var configInfo = symbolTable.lookupConfig(name.getText());
        if (configInfo != null) {
            return dynamic.setType(configInfo.getType());
        }
        var runtimeConstantInfo = symbolTable.lookupRuntimeConstant(name.getText());
        if (runtimeConstantInfo != null) {
            return dynamic.setType(runtimeConstantInfo.getType());
        }
        checker.reportError(new SemanticError(name, String.format("%s cannot be resolved to a symbol", name.getText())));
        return PrimitiveType.UNDEFINED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstConstant constant) {
        var name = constant.getName();
        var info = symbolTable.lookupConstant(name.getText());
        if (info == null) {
            checker.reportError(new SemanticError(name, String.format("%s cannot be resolved to a constant", name.getText())));
            return PrimitiveType.VOID;
        }
        return constant.setType(info.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstCommand command) {
        var name = command.getName();
        var info = symbolTable.lookupCommand(name.getText());
        if (info == null) {
            checker.reportError(new SemanticError(name, String.format("%s cannot be resolved to a command", name.getText())));
            return PrimitiveType.VOID;
        }
        var arguments = command.getArguments();
        var types = new Type[arguments.length];
        for (var index = 0; index < arguments.length; index++) {
            types[index] = arguments[index].accept(this);
        }
        var expected = TypeUtil.flatten(types);
        var actual = info.getArguments();
        if (expected.length != actual.length || !Arrays.equals(expected, actual)) {
            checker.reportError(new SemanticError(command, String.format("The command %s(%s) is not applicable for the arguments (%s)", name.getText(), TypeUtil.createRepresentation(actual), TypeUtil.createRepresentation(expected))));
        }
        return command.setType(info.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstCalc calc) {
        var type = calc.getExpression().accept(this);
        checkType(calc.getExpression(), PrimitiveType.INT, type);
        return calc.setType(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstBinaryOperation binaryOperation) {
        var left = binaryOperation.getLeft().accept(this);
        var right = binaryOperation.getRight().accept(this);
        return binaryOperation.setType(checkOperator(binaryOperation, left, right, binaryOperation.getOperator()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstVariableDeclaration variableDeclaration) {
        var expression = variableDeclaration.getExpression();
        if (expression == null) {
            if (variableDeclaration.getType().getDefaultValue() == null) {
                checker.reportError(new SemanticError(variableDeclaration, "Variables with type '" + variableDeclaration.getType().getRepresentation() + "' must be initialised"));
            }
            return null;
        }
        checkType(expression, variableDeclaration.getType(), expression.accept(this));
        return PrimitiveType.VOID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstArrayDeclaration arrayDeclaration) {
        if (arrayDeclaration.getType().getStackType() != StackType.INT) {
            checker.reportError(new SemanticError(arrayDeclaration, "Arrays can only have a type that is derived from the int type"));
        }
        checkType(arrayDeclaration.getSize(), PrimitiveType.INT, arrayDeclaration.getSize().accept(this));
        return PrimitiveType.UNDEFINED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstVariableInitializer variableInitializer) {
        var expressionTypes = new Type[variableInitializer.getExpressions().length];
        for (var index = 0; index < expressionTypes.length; index++) {
            expressionTypes[index] = variableInitializer.getExpressions()[index].accept(this);
        }
        var variableTypes = new Type[variableInitializer.getVariables().length];
        for (var index = 0; index < variableTypes.length; index++) {
            variableTypes[index] = variableInitializer.getVariables()[index].accept(this);
        }
        var varTuple = new TupleType(variableTypes);
        var exprTuple = new TupleType(expressionTypes);
        if (!exprTuple.equals(varTuple)) {
            checker.reportError(new SemanticError(variableInitializer, String.format("Mismatch variable initializer expected: %s but got: %s", varTuple.getRepresentation(), exprTuple.getRepresentation())));
        }
        return PrimitiveType.UNDEFINED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstScopedVariable scopedVariable) {
        return scopedVariable.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstArrayVariable arrayVariable) {
        return arrayVariable.setType(arrayVariable.getArrayInfo() == null ? PrimitiveType.UNDEFINED : arrayVariable.getArrayInfo().getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstSwitchStatement switchStatement) {
        var type = switchStatement.getType();
        checkType(switchStatement.getCondition(), type, switchStatement.getCondition().accept(this));
        var defined_keys = new HashSet<Integer>();
        for (var switchCase : switchStatement.getCases()) {
            var resolvedKeys = new int[switchCase.getKeys().length];
            for (var index = 0; index < resolvedKeys.length; index++) {
                var key = switchCase.getKeys()[index];
                if (checkType(key, type, key.accept(this))) {
                    int resolvedKey = resolveCaseKey(key);
                    if (!defined_keys.add(resolvedKey)) {
                        checker.reportError(new SemanticError(key, "Duplicate case"));
                    }
                    resolvedKeys[index] = resolvedKey;
                }
            }
            switchCase.setResolvedKeys(resolvedKeys);
            switchCase.accept(this);
        }
        return null;
    }


    /**
     * Resolves the specified case key expression integer value.
     *
     * @param expression
     *         the case key expression to resolve its value.
     *
     * @return the integer value of that expression.
     */
    private int resolveCaseKey(AstExpression expression) {
        if (expression instanceof AstLiteralInteger) {
            return ((AstLiteralInteger) expression).getValue();
        } else if (expression instanceof AstLiteralBool) {
            return ((AstLiteralBool) expression).getValue() ? 1 : 0;
        } else if (expression instanceof AstConstant) {
            var symbol = symbolTable.lookupConstant(((AstConstant) expression).getName().getText());
            return (int) symbol.getValue();
        } /*else if (expression instanceof AstDynamic) {
            TODO: Re-eanble this
            var name = ((AstDynamic) expression).getName().getText();
            return symbolTable.lookupConfig(name).getId();
        } */ else {
            checker.reportError(new SemanticError(expression, "Case keys must be known at compile-time."));
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstSwitchCase switchCase) {
        switchCase.getCode().accept(this);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstIfStatement ifStatement) {
        var condition = ifStatement.getCondition().accept(this);
        checkType(ifStatement.getCondition(), PrimitiveType.BOOLEAN, condition);
        ifStatement.getTrueStatement().accept(this);
        if (ifStatement.getFalseStatement() != null) {
            ifStatement.getFalseStatement().accept(this);
        }
        return PrimitiveType.VOID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstWhileStatement whileStatement) {
        var condition = whileStatement.getCondition().accept(this);
        checkType(whileStatement.getCondition(), PrimitiveType.BOOLEAN, condition);
        whileStatement.getCode().accept(this);
        return PrimitiveType.VOID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstExpressionStatement expressionStatement) {
        return expressionStatement.getExpression().accept(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstReturnStatement returnStatement) {
        var expressions = returnStatement.getExpressions();
        Type type;
        switch (expressions.length) {
            case 0:
                type = PrimitiveType.VOID;
                break;
            case 1:
                type = expressions[0].accept(this);
                break;
            default:
                var types = new Type[expressions.length];
                for (var index = 0; index < expressions.length; index++) {
                    types[index] = expressions[index].accept(this);
                }
                type = new TupleType(types);
                break;
        }
        checkType(returnStatement, script.getType(), type);
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstBlockStatement blockStatement) {
        for (var statement : blockStatement.getStatements()) {
            statement.accept(this);
        }
        return PrimitiveType.VOID;
    }

    /**
     * Checks if the specified {@link Operator operator} is applicable to the given {@link Type left} and {@link Type
     * right} hand sides, and if it is not applicable, it will report an error back to the {@link #checker}.
     *
     * @param node
     *         the node which requested this check.
     * @param left
     *         the left hand side type.
     * @param right
     *         the right hand side type.
     * @param operator
     *         the operator to check.
     *
     * @return the output value type of the operator.
     */
    private Type checkOperator(AstNodeBase node, Type left, Type right, Operator operator) {
        var applicable = false;
        if (operator.isEquality()) {
            if (left == PrimitiveType.BOOLEAN || left == PrimitiveType.INT || left == PrimitiveType.LONG) {
                applicable = left.equals(right);
            }
        } else if (operator.isRelational()) {
            if (left == PrimitiveType.INT || left == PrimitiveType.LONG) {
                applicable = left.equals(right);
            }
        } else if (operator.isLogical()) {
            applicable = left == PrimitiveType.BOOLEAN && right == PrimitiveType.BOOLEAN;
        } else if (operator.isArithmetic()) {
            if (node.selectParent(parent -> parent instanceof AstCalc) == null) {
                checker.reportError(new SemanticError(node, "Arithmetic expressions are only allowed within a 'calc' expression"));
            }
            applicable = left == PrimitiveType.INT && right == PrimitiveType.INT;
        }
        if (!applicable) {
            checker.reportError(new SemanticError(node, "The operator '" + operator.getRepresentation() + "' is undefined for the argument type(s) " + left.getRepresentation() + ", " + right.getRepresentation()));
        }
        return operator.isArithmetic() ? PrimitiveType.INT : PrimitiveType.BOOLEAN;
    }

    /**
     * Checks if the specified {@link Type expected type} matches the specified {@link Type actual type}, and if it does
     * not match, it will report an error back to the {@link #checker}.
     *
     * @param node
     *         the node which requested this check.
     * @param expected
     *         the expected type to match against.
     * @param actual
     *         the actual type to match.
     *
     * @return <code>true</code> if the type matches the expected otherwise <code>false</code>.
     */
    private boolean checkType(AstNodeBase node, Type expected, Type actual) {
        if (!expected.equals(actual)) {
            checker.reportError(new SemanticError(node, "Type mismatch: cannot convert from " + actual.getRepresentation() + " to " + expected.getRepresentation()));
            return false;
        }
        return true;
    }
}

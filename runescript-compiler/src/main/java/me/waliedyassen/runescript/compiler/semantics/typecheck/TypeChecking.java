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
import me.waliedyassen.runescript.compiler.semantics.SemanticChecker;
import me.waliedyassen.runescript.compiler.semantics.SemanticError;
import me.waliedyassen.runescript.compiler.symbol.ScriptSymbolTable;
import me.waliedyassen.runescript.compiler.symbol.impl.script.ScriptInfo;
import me.waliedyassen.runescript.compiler.syntax.ParameterSyntax;
import me.waliedyassen.runescript.compiler.syntax.ScriptSyntax;
import me.waliedyassen.runescript.compiler.syntax.SyntaxBase;
import me.waliedyassen.runescript.compiler.syntax.expr.*;
import me.waliedyassen.runescript.compiler.syntax.expr.literal.*;
import me.waliedyassen.runescript.compiler.syntax.expr.op.BinaryOperationSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.*;
import me.waliedyassen.runescript.compiler.syntax.stmt.conditional.IfStatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.loop.BreakStatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.loop.ContinueStatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.loop.WhileStatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;
import me.waliedyassen.runescript.compiler.util.Operator;
import me.waliedyassen.runescript.compiler.util.trigger.TriggerType;
import me.waliedyassen.runescript.type.*;

import java.util.HashSet;

/**
 * Represents the type checking semantic analysis.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class TypeChecking implements SyntaxVisitor<Type> {

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
    private ScriptSyntax script;

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(ScriptSyntax script) {
        this.script = script;
        script.getCode().accept(this);
        return script.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(ParameterSyntax parameter) {
        return parameter.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(LiteralBooleanSyntax bool) {
        return bool.setType(PrimitiveType.BOOLEAN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(LiteralIntegerSyntax integer) {
        return integer.setType(PrimitiveType.INT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(LiteralLongSyntax longInteger) {
        return longInteger.setType(PrimitiveType.LONG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(LiteralStringSyntax string) {
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
    public Type visit(LiteralCoordgridSyntax coordgrid) {
        return coordgrid.setType(PrimitiveType.COORDGRID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(LiteralNullSyntax literalNullSyntax) {
        return literalNullSyntax.setType(PrimitiveType.NULL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(ConcatenationSyntax concatenation) {
        for (var expr : concatenation.getExpressions()) {
            isTypeApplicable(expr, PrimitiveType.STRING, expr.accept(this));
        }
        return concatenation.setType(PrimitiveType.STRING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(ComponentSyntax component) {
        var interfaceInfo = symbolTable.lookupInterface(component.getParentInterface().getText());
        if (interfaceInfo == null) {
            checker.reportError(new SemanticError(component.getParentInterface(), String.format("Could not resolve interface with the name '%s'", component.getParentInterface().getText())));
        } else {
            component.getParentInterface().setType(PrimitiveType.INTERFACE);
            if (!(component.getComponent() instanceof LiteralIntegerSyntax) && interfaceInfo.lookupComponent(String.valueOf(component.getComponentName())) == null) {
                checker.reportError(new SemanticError(component.getParentInterface(), String.format("Could not resolve component with the name '%s' in '%s'", component.getComponentName(), component.getParentInterface().getText())));
            }
        }
        return component.setType(PrimitiveType.COMPONENT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(VariableExpressionSyntax variableExpression) {
        return variableExpression.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(ArrayElementSyntax arrayExpression) {
        if (arrayExpression.getArray() == null) {
            return PrimitiveType.UNDEFINED;
        }
        return arrayExpression.setType(arrayExpression.getArray().getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(HookSyntax hook) {
        if (hookTriggerType == null) {
            checker.reportError(new SemanticError(hook, "Hooks are not allowed"));
        } else if (hook.getName() != null) {
            var parentInfo = symbolTable.lookupCommand(((CommandSyntax) hook.getParent()).getName().getText());
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
                            isTypeApplicable(transmit, expected, transmit.accept(this));
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
    public Type visit(CallSyntax call) {
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
     * @param call      the AST node object of the call.
     * @param info      the information of the script we are calling.
     * @param arguments the arguments that are used in the call.
     */
    private void checkCallApplicable(SyntaxBase call, ScriptInfo info, ExpressionSyntax[] arguments) {
        var types = new Type[arguments.length];
        for (int index = 0; index < arguments.length; index++) {
            types[index] = arguments[index].accept(this);
        }
        var actual = new TupleType(types);
        var expected = new TupleType(info.getArguments());
        if (!isTypeApplicable(call, expected, actual, false)) {
            checker.reportError(new SemanticError(call, String.format("The script %s(%s) is not applicable for the arguments (%s)", info.getName(), expected.getRepresentation(), actual.getRepresentation())));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(DynamicSyntax dynamic) {
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
    public Type visit(ConstantSyntax constant) {
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
    public Type visit(CommandSyntax command) {
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
        var expected = new TupleType(info.getArguments());
        var actual = new TupleType(types);
        if (!isTypeApplicable(command, expected, actual, false)) {
            checker.reportError(new SemanticError(command, String.format("The command %s(%s) is not applicable for the arguments (%s)", name.getText(), actual.getRepresentation(), expected.getRepresentation())));
        }
        return command.setType(info.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(CalcSyntax calc) {
        var type = calc.getExpression().accept(this);
        isTypeApplicable(calc.getExpression(), PrimitiveType.INT, type);
        return calc.setType(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(BinaryOperationSyntax binaryOperation) {
        var left = binaryOperation.getLeft().accept(this);
        var right = binaryOperation.getRight().accept(this);
        var type = checkOperator(binaryOperation, left, right, binaryOperation.getOperator());
        return binaryOperation.setType(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(VariableDeclarationSyntax variableDeclaration) {
        var expression = variableDeclaration.getExpression();
        if (expression == null) {
            if (variableDeclaration.getType().getDefaultValue() == null) {
                checker.reportError(new SemanticError(variableDeclaration, "Variables with type '" + variableDeclaration.getType().getRepresentation() + "' must be initialised"));
            }
            return null;
        }
        isTypeApplicable(expression, variableDeclaration.getType(), expression.accept(this));
        return PrimitiveType.VOID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(ArrayDeclarationSyntax arrayDeclaration) {
        if (arrayDeclaration.getType().getStackType() != StackType.INT) {
            checker.reportError(new SemanticError(arrayDeclaration, "Arrays can only have a type that is derived from the int type"));
        }
        isTypeApplicable(arrayDeclaration.getSize(), PrimitiveType.INT, arrayDeclaration.getSize().accept(this));
        return PrimitiveType.UNDEFINED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(VariableInitializerSyntax variableInitializer) {
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
    public Type visit(ScopedVariableSyntax scopedVariable) {
        return scopedVariable.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(ArrayVariableSyntax arrayVariable) {
        return arrayVariable.setType(arrayVariable.getArrayInfo() == null ? PrimitiveType.UNDEFINED : arrayVariable.getArrayInfo().getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(SwitchStatementSyntax switchStatement) {
        var type = switchStatement.getType();
        isTypeApplicable(switchStatement.getCondition(), type, switchStatement.getCondition().accept(this));
        var defined_keys = new HashSet<Integer>();
        for (var switchCase : switchStatement.getCases()) {
            var resolvedKeys = new int[switchCase.getKeys().length];
            for (var index = 0; index < resolvedKeys.length; index++) {
                var key = switchCase.getKeys()[index];
                if (isTypeApplicable(key, type, key.accept(this))) {
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
     * @param expression the case key expression to resolve its value.
     * @return the integer value of that expression.
     */
    private int resolveCaseKey(ExpressionSyntax expression) {
        if (expression instanceof LiteralIntegerSyntax) {
            return ((LiteralIntegerSyntax) expression).getValue();
        } else if (expression instanceof LiteralBooleanSyntax) {
            return ((LiteralBooleanSyntax) expression).getValue() ? 1 : 0;
        } else if (expression instanceof ConstantSyntax) {
            var symbol = symbolTable.lookupConstant(((ConstantSyntax) expression).getName().getText());
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
    public Type visit(SwitchCaseSyntax switchCase) {
        switchCase.getCode().accept(this);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(IfStatementSyntax ifStatement) {
        var condition = ifStatement.getCondition().accept(this);
        isTypeApplicable(ifStatement.getCondition(), PrimitiveType.BOOLEAN, condition);
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
    public Type visit(WhileStatementSyntax whileStatement) {
        var condition = whileStatement.getCondition().accept(this);
        isTypeApplicable(whileStatement.getCondition(), PrimitiveType.BOOLEAN, condition);
        whileStatement.getCode().accept(this);
        return PrimitiveType.VOID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(ContinueStatementSyntax continueStatementSyntax) {
        var whileStatementSyntax = continueStatementSyntax.selectParent(syntax -> syntax instanceof WhileStatementSyntax);
        if (whileStatementSyntax == null) {
            checker.reportError(new SemanticError(continueStatementSyntax, "Continue statement is not allowed outside of a loop"));
        }
        return PrimitiveType.VOID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(BreakStatementSyntax breakStatementSyntax) {
        var whileStatementSyntax = breakStatementSyntax.selectParent(syntax -> syntax instanceof WhileStatementSyntax);
        if (whileStatementSyntax == null) {
            checker.reportError(new SemanticError(breakStatementSyntax, "Break statement is not allowed outside of a loop"));
        }
        return PrimitiveType.VOID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(ExpressionStatementSyntax expressionStatement) {
        return expressionStatement.getExpression().accept(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(ReturnStatementSyntax returnStatement) {
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
        isTypeApplicable(returnStatement, script.getType(), type);
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(BlockStatementSyntax blockStatement) {
        for (var statement : blockStatement.getStatements()) {
            statement.accept(this);
        }
        return PrimitiveType.VOID;
    }

    /**
     * Checks if the specified {@link Operator operator} is applicable to the given {@link Type left} and {@link Type
     * right} hand sides, and if it is not applicable, it will report an error back to the {@link #checker}.
     *
     * @param node     the node which requested this check.
     * @param left     the left hand side type.
     * @param right    the right hand side type.
     * @param operator the operator to check.
     * @return the output value type of the operator.
     */
    private Type checkOperator(SyntaxBase node, Type left, Type right, Operator operator) {
        var applicable = false;
        if (operator.isEquality()) {
            if (left == PrimitiveType.BOOLEAN || left == PrimitiveType.INT || left == PrimitiveType.LONG) {
                applicable = isTypeApplicable(null, left, right, false);
            } else if (right == PrimitiveType.BOOLEAN || right == PrimitiveType.INT || right == PrimitiveType.LONG) {
                applicable = isTypeApplicable(null, right, left, false);
            }
        } else if (operator.isRelational()) {
            if (left == PrimitiveType.INT || left == PrimitiveType.LONG) {
                applicable = left.equals(right);
            }
        } else if (operator.isLogical()) {
            applicable = left == PrimitiveType.BOOLEAN && right == PrimitiveType.BOOLEAN;
        } else if (operator.isArithmetic()) {
            if (node.selectParent(parent -> parent instanceof CalcSyntax) == null) {
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
     * @param node     the node which requested this check.
     * @param expected the expected type to match against.
     * @param actual   the actual type to match.
     * @return <code>true</code> if the type matches the expected otherwise <code>false</code>.
     */
    private boolean isTypeApplicable(SyntaxBase node, Type expected, Type actual) {
        return isTypeApplicable(node, expected, actual, true);
    }

    /**
     * Checks if the specified {@link Type expected type} matches the specified {@link Type actual type}, and if it does
     * not match, it will report an error back to the {@link #checker}.
     *
     * @param node        the node which requested this check.
     * @param expected    the expected type to match against.
     * @param actual      the actual type to match.
     * @param reportError whether or not to report and error i the call is not applicable.
     * @return <code>true</code> if the type matches the expected otherwise <code>false</code>.
     */
    private boolean isTypeApplicable(SyntaxBase node, Type expected, Type actual, boolean reportError) {
        Type[] expectedFlattened = expected instanceof TupleType ? ((TupleType) expected).getFlattened() : new Type[]{expected};
        Type[] actualFlattened = actual instanceof TupleType ? ((TupleType) actual).getFlattened() : new Type[]{actual};
        boolean applicable = true;
        if (expectedFlattened.length != actualFlattened.length) {
            applicable = false;
        } else {
            for (int index = 0; index < expectedFlattened.length; index++) {
                Type expectedType = expectedFlattened[index];
                Type actualType = actualFlattened[index];
                if (actualType == PrimitiveType.NULL) {
                    applicable &= expectedType != PrimitiveType.PARAM && expectedType.getStackType() == StackType.INT;
                } else {
                    applicable &= expectedType.equals(actualType);
                }
            }
        }
        if (!applicable && reportError) {
            checker.reportError(new SemanticError(node, "Type mismatch: cannot convert from " + actual.getRepresentation() + " to " + expected.getRepresentation()));
        }
        return applicable;
    }

}
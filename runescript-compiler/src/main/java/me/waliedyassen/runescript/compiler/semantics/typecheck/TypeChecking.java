/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.semantics.typecheck;

import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.ast.AstNode;
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
import me.waliedyassen.runescript.compiler.semantics.SemanticUtil;
import me.waliedyassen.runescript.compiler.symbol.SymbolTable;
import me.waliedyassen.runescript.compiler.type.Type;
import me.waliedyassen.runescript.compiler.type.primitive.PrimitiveType;
import me.waliedyassen.runescript.compiler.type.tuple.TupleType;
import me.waliedyassen.runescript.compiler.util.Operator;
import me.waliedyassen.runescript.compiler.util.trigger.TriggerType;

import java.util.ArrayList;
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
     * The symbol table.
     */
    private final SymbolTable symbolTable;

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
        return bool.setType(PrimitiveType.BOOL);
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
        return string.setType(PrimitiveType.STRING);
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
    public Type visit(AstVariableExpression variableExpression) {
        return variableExpression.setType(variableExpression.getVariable().getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstGosub gosub) {
        var name = gosub.getName();
        var script = symbolTable.lookupScript(TriggerType.PROC, name.getText());
        if (script == null) {
            checker.reportError(new SemanticError(gosub, String.format("Could not resolve proc script with the name '%s'", name.getText())));
            return PrimitiveType.UNDEFINED;
        } else {
            var arguments = gosub.getArguments();
            var types = new Type[gosub.getArguments().length];
            for (int index = 0; index < arguments.length; index++) {
                types[index] = arguments[index].accept(this);
            }
            var expected = SemanticUtil.flatten(types);
            var actual = script.getArguments();
            if (expected.length != actual.length || !Arrays.equals(expected, actual)) {
                checker.reportError(new SemanticError(gosub, String.format("The script %s(%s) is not applicable for the arguments (%s)", name.getText(), SemanticUtil.createRepresentation(actual), SemanticUtil.createRepresentation(expected))));
            }
        }
        return gosub.setType(script.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstDynamic dynamic) {
        var name = dynamic.getName();
        var commandInfo = symbolTable.lookupCommand(name.getText());
        if (commandInfo != null) {
            if (commandInfo.getArguments().length > 0) {
                checker.reportError(new SemanticError(name, String.format("The command %s(%s) is not applicable for the arguments ()", name.getText(), SemanticUtil.createRepresentation(commandInfo.getArguments()))));
            }
            return dynamic.setType(commandInfo.getType());
        }
        var configInfo = symbolTable.lookupConfig(name.getText());
        if (configInfo != null) {
            return dynamic.setType(configInfo.getType());
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
        var expected = SemanticUtil.flatten(types);
        var actual = info.getArguments();
        if (expected.length != actual.length || !Arrays.equals(expected, actual)) {
            checker.reportError(new SemanticError(command, String.format("The command %s(%s) is not applicable for the arguments (%s)", name.getText(), SemanticUtil.createRepresentation(actual), SemanticUtil.createRepresentation(expected))));
        }
        return command.setType(info.getType());
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
            return null;
        }
        checkType(expression, variableDeclaration.getVariable().getType(), expression.accept(this));
        return PrimitiveType.VOID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstVariableInitializer variableInitializer) {
        var expression = variableInitializer.getExpression();
        if (expression != null && variableInitializer.getVariable() != null) {
            checkType(expression, variableInitializer.getVariable().getType(), expression.accept(this));
        }
        return PrimitiveType.UNDEFINED;
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
        } else if (expression instanceof AstDynamic) {
            return symbolTable.lookupConfig(((AstDynamic) expression).getName().getText()).getId();
        } else {
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
        checkType(ifStatement.getCondition(), PrimitiveType.BOOL, condition);
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
        checkType(whileStatement.getCondition(), PrimitiveType.BOOL, condition);
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
    private Type checkOperator(AstNode node, Type left, Type right, Operator operator) {
        var applicable = false;
        if (operator.isEquality()) {
            if (left == PrimitiveType.BOOL || left == PrimitiveType.INT || left == PrimitiveType.LONG) {
                applicable = left.equals(right);
            }
        } else if (operator.isRelational()) {
            if (left == PrimitiveType.INT || left == PrimitiveType.LONG) {
                applicable = left.equals(right);
            }
        } else if (operator.isLogical()) {
            if (left == PrimitiveType.BOOL && right == PrimitiveType.BOOL) {
                applicable = true;
            }
        }
        if (!applicable) {
            checker.reportError(new SemanticError(node, "The operator '" + operator.getRepresentation() + "' is undefined for the argument type(s) " + left.getRepresentation() + ", " + right.getRepresentation()));
        }
        return PrimitiveType.BOOL;
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
    private boolean checkType(AstNode node, Type expected, Type actual) {
        if (!expected.equals(actual)) {
            checker.reportError(new SemanticError(node, "Type mismatch: cannot convert from " + actual.getRepresentation() + " to " + expected.getRepresentation()));
            return false;
        }
        return true;
    }
}

/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.semantics.checkers;

import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.ast.AstNode;
import me.waliedyassen.runescript.compiler.ast.AstParameter;
import me.waliedyassen.runescript.compiler.ast.AstScript;
import me.waliedyassen.runescript.compiler.ast.expr.*;
import me.waliedyassen.runescript.compiler.ast.expr.literal.AstLiteralBool;
import me.waliedyassen.runescript.compiler.ast.expr.literal.AstLiteralInteger;
import me.waliedyassen.runescript.compiler.ast.expr.literal.AstLiteralLong;
import me.waliedyassen.runescript.compiler.ast.expr.literal.AstLiteralString;
import me.waliedyassen.runescript.compiler.ast.stmt.*;
import me.waliedyassen.runescript.compiler.ast.stmt.conditional.AstIfStatement;
import me.waliedyassen.runescript.compiler.ast.stmt.conditional.AstWhileStatement;
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;
import me.waliedyassen.runescript.compiler.semantics.SemanticChecker;
import me.waliedyassen.runescript.compiler.semantics.SemanticError;
import me.waliedyassen.runescript.compiler.type.Type;
import me.waliedyassen.runescript.compiler.type.primitive.PrimitiveType;
import me.waliedyassen.runescript.compiler.type.tuple.TupleType;
import me.waliedyassen.runescript.compiler.util.Operator;
import me.waliedyassen.runescript.compiler.util.TriggerType;

/**
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class TypeChecker implements AstVisitor<Type> {

    /**
     * The owner {@link SemanticChecker} instance of this type checker.
     */
    private final SemanticChecker checker;

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
        var trigger = script.getTrigger();
        if (TriggerType.forRepresentation(trigger.getText()) == null) {
            checker.reportError(new SemanticError(trigger, trigger.getText() + " cannot be resolved to a trigger"));
        }
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
        return PrimitiveType.BOOL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstLiteralInteger integer) {
        return PrimitiveType.INT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstLiteralLong longInteger) {
        return PrimitiveType.LONG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstLiteralString string) {
        return PrimitiveType.STRING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstConcatenation concatenation) {
        for (var expr : concatenation.getExpressions()) {
            checkType(expr, PrimitiveType.STRING, expr.accept(this));
        }
        return PrimitiveType.STRING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstVariableExpression variableExpression) {
        return variableExpression.getVariable().getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstGosub gosub) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstDynamic dynamic) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstConstant constant) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstCommand command) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstBinaryOperation binaryOperation) {
        var left = binaryOperation.getLeft().accept(this);
        var right = binaryOperation.getRight().accept(this);
        return checkOperator(binaryOperation, left, right, binaryOperation.getOperator());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstVariableDeclaration variableDeclaration) {
        return PrimitiveType.VOID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstVariableInitializer variableInitializer) {
        return PrimitiveType.VOID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visit(AstSwitchStatement switchStatement) {
        var type = switchStatement.getType();
        checkType(switchStatement.getCondition(), type, switchStatement.getCondition().accept(this));
        for (var switchCase : switchStatement.getCases()) {
            for (var key : switchCase.getKeys()) {
                checkType(key, type, key.accept(this));
            }
            switchCase.accept(this);
        }
        return null;
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
        whileStatement.accept(this);
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

    private Type checkType(AstNode node, Type expected, Type actual) {
        if (!expected.equals(actual)) {
            checker.reportError(new SemanticError(node, "Type mismatch: cannot convert from " + actual.getRepresentation() + " to " + expected.getRepresentation()));
        }
        return actual;
    }
}

/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.visitor;

import me.waliedyassen.runescript.compiler.ast.AstParameter;
import me.waliedyassen.runescript.compiler.ast.AstScript;
import me.waliedyassen.runescript.compiler.ast.expr.*;
import me.waliedyassen.runescript.compiler.ast.expr.literal.*;
import me.waliedyassen.runescript.compiler.ast.stmt.*;
import me.waliedyassen.runescript.compiler.ast.stmt.conditional.AstIfStatement;
import me.waliedyassen.runescript.compiler.ast.stmt.conditional.AstWhileStatement;

/**
 * Represents the base interface for an AST visitor.
 *
 * @param <R>
 *         the return value type.
 *
 * @author Walied K. Yassen
 */
public interface AstVisitor<R> {

    /**
     * Gets called when we have just visited an {@link AstScript} node.
     *
     * @param script
     *         the {@link AstScript} node we have just visited.
     *
     * @return the implementation output object.
     */
    default R visit(AstScript script) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstParameter} node.
     *
     * @param parameter
     *         the {@link AstParameter} node we have just visited.
     *
     * @return the implementation output object.
     */
    default R visit(AstParameter parameter) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstLiteralBool} node.
     *
     * @param bool
     *         the {@link AstLiteralBool} node we have just visited.
     *
     * @return the implementation output object.
     */
    default R visit(AstLiteralBool bool) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstLiteralInteger} node.
     *
     * @param integer
     *         the {@link AstLiteralInteger} node we have just visited.
     *
     * @return the implementation output object.
     */
    default R visit(AstLiteralInteger integer) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstLiteralLong} node.
     *
     * @param longInteger
     *         the {@link AstLiteralLong} node we have just visited.
     *
     * @return the implementation output object.
     */
    default R visit(AstLiteralLong longInteger) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstLiteralString} node.
     *
     * @param string
     *         the {@link AstLiteralString} node we have just visited.
     *
     * @return the implementation output object.
     */
    default R visit(AstLiteralString string) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstConcatenation} node.
     *
     * @param stringConcat
     *         the {@link AstConcatenation} node we have just visited.
     *
     * @return the implementation output object.
     */
    default R visit(AstConcatenation stringConcat) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstVariableExpression} node.
     *
     * @param variable
     *         the {@link AstVariableExpression} node we have just visited.
     *
     * @return the implementation output object.
     */
    default R visit(AstVariableExpression variable) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstGosub} node.
     *
     * @param gosub
     *         the {@link AstGosub} node we have just visited.
     *
     * @return the implementation output object.
     */
    default R visit(AstGosub gosub) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstDynamic} node.
     *
     * @param dynamic
     *         the {@link AstDynamic} node we have just visited.
     *
     * @return the implementation output object.
     */
    default R visit(AstDynamic dynamic) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstConstant} node.
     *
     * @param constant
     *         the {@link AstConstant} node we have just visited.
     *
     * @return the implementation output object.
     */
    default R visit(AstConstant constant) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstCommand} node.
     *
     * @param command
     *         the {@link AstCommand} node we have just visited.
     *
     * @return the implementation output object.
     */
    default R visit(AstCommand command) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstBinaryOperation} node.
     *
     * @param binaryOperation
     *         the {@link AstBinaryOperation} node we have just visited.
     *
     * @return the implementation output object.
     */
    default R visit(AstBinaryOperation binaryOperation) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstVariableDeclaration} node.
     *
     * @param variableDeclaration
     *         the {@link AstVariableDeclaration} node we have just visited.
     *
     * @return the implementation output object.
     */
    default R visit(AstVariableDeclaration variableDeclaration) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstVariableInitializer} node.
     *
     * @param variableInitializer
     *         the {@link AstVariableInitializer} node we have just visited.
     *
     * @return the implementation output object.
     */
    default R visit(AstVariableInitializer variableInitializer) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstSwitchStatement} node.
     *
     * @param switchStatement
     *         the {@link AstSwitchStatement} node we have just visited.
     *
     * @return the implementation output object.
     */
    default R visit(AstSwitchStatement switchStatement) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstSwitchCase} node.
     *
     * @param switchCase
     *         the {@link AstSwitchCase} node we have just visited.
     *
     * @return the implementation output object.
     */
    default R visit(AstSwitchCase switchCase) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstIfStatement} node.
     *
     * @param ifStatement
     *         the {@link AstIfStatement} node we have just visited.
     *
     * @return the implementation output object.
     */
    default R visit(AstIfStatement ifStatement) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstWhileStatement} node.
     *
     * @param whileStatement
     *         the {@link AstWhileStatement} node we have just visited.
     *
     * @return the implementation output object.
     */
    default R visit(AstWhileStatement whileStatement) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstExpressionStatement} node.
     *
     * @param expressionStatement
     *         the {@link AstExpressionStatement} node we have just visited.
     *
     * @return the implementation output object.
     */
    default R visit(AstExpressionStatement expressionStatement) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstReturnStatement} node.
     *
     * @param returnStatement
     *         the {@link AstReturnStatement} node we have just visited.
     *
     * @return the implementation output object.
     */
    default R visit(AstReturnStatement returnStatement) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstBlockStatement} node.
     *
     * @param blockStatement
     *         the {@link AstBlockStatement} node we have just visited.
     *
     * @return the implementation output object.
     */
    default R visit(AstBlockStatement blockStatement) {
        return null;
    }
}

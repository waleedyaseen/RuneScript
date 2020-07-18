/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.visitor;

import me.waliedyassen.runescript.compiler.ast.AstAnnotation;
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
 * @param <E>
 *         the visit expression methods return type.
 * @param <S>
 *         the visit statement methods return type.
 *
 * @author Walied K. Yassen
 */
public interface AstVisitor<E, S> {

    /**
     * Gets called when we have just visited an {@link AstScript} node.
     *
     * @param script
     *         the {@link AstScript} node we have just visited.
     *
     * @return the implementation output object.
     */
    default S visit(AstScript script) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstAnnotation} node.
     *
     * @param annotation
     *         the {@link AstAnnotation} node we have just sitied.
     *
     * @return the implementation output object.
     */
    default S visit(AstAnnotation annotation) {
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
    default S visit(AstParameter parameter) {
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
    default E visit(AstLiteralBool bool) {
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
    default E visit(AstLiteralInteger integer) {
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
    default E visit(AstLiteralLong longInteger) {
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
    default E visit(AstLiteralString string) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstLiteralCoordgrid} node.
     *
     * @param coordgrid
     *         the {@link AstLiteralCoordgrid} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(AstLiteralCoordgrid coordgrid) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstComponent} node.
     *
     * @param string
     *         the {@link AstComponent} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(AstComponent string) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstConcatenation} node.
     *
     * @param concatenation
     *         the {@link AstConcatenation} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(AstConcatenation concatenation) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstVariableExpression} node.
     *
     * @param variableExpression
     *         the {@link AstVariableExpression} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(AstVariableExpression variableExpression) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstArrayExpression} node.
     *
     * @param arrayExpression
     *         the {@link AstArrayExpression} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(AstArrayExpression arrayExpression) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstCall} node.
     *
     * @param hook
     *         the {@link AstHook} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(AstHook hook) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstCall} node.
     *
     * @param call
     *         the {@link AstCall} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(AstCall call) {
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
    default E visit(AstDynamic dynamic) {
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
    default E visit(AstConstant constant) {
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
    default E visit(AstCommand command) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstCalc} node.
     *
     * @param calc
     *         the {@link AstCalc} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(AstCalc calc) {
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
    default E visit(AstBinaryOperation binaryOperation) {
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
    default S visit(AstVariableDeclaration variableDeclaration) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstArrayDeclaration} node.
     *
     * @param arrayDeclaration
     *         the {@link AstArrayDeclaration} node we have just visited.
     *
     * @return the implementation output object.
     */
    default S visit(AstArrayDeclaration arrayDeclaration) {
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
    default S visit(AstVariableInitializer variableInitializer) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstVariable} node.
     *
     * @param variable
     *         the {@link AstVariable} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(AstVariable variable) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AstArrayInitializer} node.
     *
     * @param arrayInitializer
     *         the {@link AstVariableInitializer} node we have just visited.
     *
     * @return the implementation output object.
     */
    default S visit(AstArrayInitializer arrayInitializer) {
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
    default S visit(AstSwitchStatement switchStatement) {
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
    default S visit(AstSwitchCase switchCase) {
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
    default S visit(AstIfStatement ifStatement) {
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
    default S visit(AstWhileStatement whileStatement) {
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
    default S visit(AstExpressionStatement expressionStatement) {
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
    default S visit(AstReturnStatement returnStatement) {
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
    default S visit(AstBlockStatement blockStatement) {
        return null;
    }
}

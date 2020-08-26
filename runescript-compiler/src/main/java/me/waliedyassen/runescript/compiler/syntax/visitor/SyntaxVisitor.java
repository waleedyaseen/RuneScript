/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.visitor;

import me.waliedyassen.runescript.compiler.syntax.AnnotationSyntax;
import me.waliedyassen.runescript.compiler.syntax.ParameterSyntax;
import me.waliedyassen.runescript.compiler.syntax.ScriptSyntax;
import me.waliedyassen.runescript.compiler.syntax.expr.*;
import me.waliedyassen.runescript.compiler.syntax.expr.literal.*;
import me.waliedyassen.runescript.compiler.syntax.expr.op.BinaryOperationSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.*;
import me.waliedyassen.runescript.compiler.syntax.stmt.conditional.IfStatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.conditional.WhileStatementSyntax;

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
public interface SyntaxVisitor<E, S> {

    /**
     * Gets called when we have just visited an {@link ScriptSyntax} node.
     *
     * @param script
     *         the {@link ScriptSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default S visit(ScriptSyntax script) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link AnnotationSyntax} node.
     *
     * @param annotation
     *         the {@link AnnotationSyntax} node we have just sitied.
     *
     * @return the implementation output object.
     */
    default S visit(AnnotationSyntax annotation) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link ParameterSyntax} node.
     *
     * @param parameter
     *         the {@link ParameterSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default S visit(ParameterSyntax parameter) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link LiteralBooleanSyntax} node.
     *
     * @param bool
     *         the {@link LiteralBooleanSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(LiteralBooleanSyntax bool) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link LiteralIntegerSyntax} node.
     *
     * @param integer
     *         the {@link LiteralIntegerSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(LiteralIntegerSyntax integer) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link LiteralLongSyntax} node.
     *
     * @param longInteger
     *         the {@link LiteralLongSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(LiteralLongSyntax longInteger) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link LiteralStringSyntax} node.
     *
     * @param string
     *         the {@link LiteralStringSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(LiteralStringSyntax string) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link LiteralCoordgridSyntax} node.
     *
     * @param coordgrid
     *         the {@link LiteralCoordgridSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(LiteralCoordgridSyntax coordgrid) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link ComponentSyntax} node.
     *
     * @param string
     *         the {@link ComponentSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(ComponentSyntax string) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link ConcatenationSyntax} node.
     *
     * @param concatenation
     *         the {@link ConcatenationSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(ConcatenationSyntax concatenation) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link VariableExpressionSyntax} node.
     *
     * @param variableExpression
     *         the {@link VariableExpressionSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(VariableExpressionSyntax variableExpression) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link ArrayElementSyntax} node.
     *
     * @param arrayExpression
     *         the {@link ArrayElementSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(ArrayElementSyntax arrayExpression) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link CallSyntax} node.
     *
     * @param hook
     *         the {@link HookSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(HookSyntax hook) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link CallSyntax} node.
     *
     * @param call
     *         the {@link CallSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(CallSyntax call) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link DynamicSyntax} node.
     *
     * @param dynamic
     *         the {@link DynamicSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(DynamicSyntax dynamic) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link ConstantSyntax} node.
     *
     * @param constant
     *         the {@link ConstantSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(ConstantSyntax constant) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link CommandSyntax} node.
     *
     * @param command
     *         the {@link CommandSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(CommandSyntax command) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link CalcSyntax} node.
     *
     * @param calc
     *         the {@link CalcSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(CalcSyntax calc) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link BinaryOperationSyntax} node.
     *
     * @param binaryOperation
     *         the {@link BinaryOperationSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(BinaryOperationSyntax binaryOperation) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link VariableDeclarationSyntax} node.
     *
     * @param variableDeclaration
     *         the {@link VariableDeclarationSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default S visit(VariableDeclarationSyntax variableDeclaration) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link ArrayDeclarationSyntax} node.
     *
     * @param arrayDeclaration
     *         the {@link ArrayDeclarationSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default S visit(ArrayDeclarationSyntax arrayDeclaration) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link VariableInitializerSyntax} node.
     *
     * @param variableInitializer
     *         the {@link VariableInitializerSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default S visit(VariableInitializerSyntax variableInitializer) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link ScopedVariableSyntax} node.
     *
     * @param scopedVariable
     *         the {@link ScopedVariableSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(ScopedVariableSyntax scopedVariable) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link ArrayVariableSyntax} node.
     *
     * @param arrayVariable
     *         the {@link ArrayVariableSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default E visit(ArrayVariableSyntax arrayVariable) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link SwitchStatementSyntax} node.
     *
     * @param switchStatement
     *         the {@link SwitchStatementSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default S visit(SwitchStatementSyntax switchStatement) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link SwitchCaseSyntax} node.
     *
     * @param switchCase
     *         the {@link SwitchCaseSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default S visit(SwitchCaseSyntax switchCase) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link IfStatementSyntax} node.
     *
     * @param ifStatement
     *         the {@link IfStatementSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default S visit(IfStatementSyntax ifStatement) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link WhileStatementSyntax} node.
     *
     * @param whileStatement
     *         the {@link WhileStatementSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default S visit(WhileStatementSyntax whileStatement) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link ExpressionStatementSyntax} node.
     *
     * @param expressionStatement
     *         the {@link ExpressionStatementSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default S visit(ExpressionStatementSyntax expressionStatement) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link ReturnStatementSyntax} node.
     *
     * @param returnStatement
     *         the {@link ReturnStatementSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default S visit(ReturnStatementSyntax returnStatement) {
        return null;
    }

    /**
     * Gets called when we have just visited an {@link BlockStatementSyntax} node.
     *
     * @param blockStatement
     *         the {@link BlockStatementSyntax} node we have just visited.
     *
     * @return the implementation output object.
     */
    default S visit(BlockStatementSyntax blockStatement) {
        return null;
    }
}

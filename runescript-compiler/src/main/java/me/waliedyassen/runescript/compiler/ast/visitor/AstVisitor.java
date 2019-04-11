package me.waliedyassen.runescript.compiler.ast.visitor;

import me.waliedyassen.runescript.compiler.ast.AstParameter;
import me.waliedyassen.runescript.compiler.ast.AstScript;
import me.waliedyassen.runescript.compiler.ast.expr.*;
import me.waliedyassen.runescript.compiler.ast.literal.*;
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
     * Gets called when we have just entered an {@link AstScript} node.
     *
     * @param script
     *         the {@link AstScript} node we have just entered.
     *
     * @return the implementation output object.
     */
    default R visit(AstScript script) {
        return null;
    }

    /**
     * Gets called when we have just entered an {@link AstParameter} node.
     *
     * @param parameter
     *         the {@link AstParameter} node we have just entered.
     *
     * @return the implementation output object.
     */
    default R visit(AstParameter parameter) {
        return null;
    }

    /**
     * Gets called when we have just entered an {@link AstBool} node.
     *
     * @param bool
     *         the {@link AstBool} node we have just entered.
     *
     * @return the implementation output object.
     */
    default R visit(AstBool bool) {
        return null;
    }

    /**
     * Gets called when we have just entered an {@link AstInteger} node.
     *
     * @param integer
     *         the {@link AstInteger} node we have just entered.
     *
     * @return the implementation output object.
     */
    default R visit(AstInteger integer) {
        return null;
    }

    /**
     * Gets called when we have just entered an {@link AstLong} node.
     *
     * @param longInteger
     *         the {@link AstLong} node we have just entered.
     *
     * @return the implementation output object.
     */
    default R visit(AstLong longInteger) {
        return null;
    }

    /**
     * Gets called when we have just entered an {@link AstString} node.
     *
     * @param string
     *         the {@link AstString} node we have just entered.
     *
     * @return the implementation output object.
     */
    default R visit(AstString string) {
        return null;
    }

    /**
     * Gets called when we have just entered an {@link AstStringConcat} node.
     *
     * @param stringConcat
     *         the {@link AstStringConcat} node we have just entered.
     *
     * @return the implementation output object.
     */
    default R visit(AstStringConcat stringConcat) {
        return null;
    }

    /**
     * Gets called when we have just entered an {@link AstVariableExpression} node.
     *
     * @param variable
     *         the {@link AstVariableExpression} node we have just entered.
     *
     * @return the implementation output object.
     */
    default R visit(AstVariableExpression variable) {
        return null;
    }

    /**
     * Gets called when we have just entered an {@link AstGosub} node.
     *
     * @param gosub
     *         the {@link AstGosub} node we have just entered.
     *
     * @return the implementation output object.
     */
    default R visit(AstGosub gosub) {
        return null;
    }

    /**
     * Gets called when we have just entered an {@link AstDynamic} node.
     *
     * @param dynamic
     *         the {@link AstDynamic} node we have just entered.
     *
     * @return the implementation output object.
     */
    default R visit(AstDynamic dynamic) {
        return null;
    }

    /**
     * Gets called when we have just entered an {@link AstConstant} node.
     *
     * @param constant
     *         the {@link AstConstant} node we have just entered.
     *
     * @return the implementation output object.
     */
    default R visit(AstConstant constant) {
        return null;
    }

    /**
     * Gets called when we have just entered an {@link AstCommand} node.
     *
     * @param command
     *         the {@link AstCommand} node we have just entered.
     *
     * @return the implementation output object.
     */
    default R visit(AstCommand command) {
        return null;
    }

    /**
     * Gets called when we have just entered an {@link AstBinaryOperation} node.
     *
     * @param binaryOperation
     *         the {@link AstBinaryOperation} node we have just entered.
     *
     * @return the implementation output object.
     */
    default R visit(AstBinaryOperation binaryOperation) {
        return null;
    }

    /**
     * Gets called when we have just entered an {@link AstVariableDeclaration} node.
     *
     * @param variableDeclaration
     *         the {@link AstVariableDeclaration} node we have just entered.
     *
     * @return the implementation output object.
     */
    default R visit(AstVariableDeclaration variableDeclaration) {
        return null;
    }

    /**
     * Gets called when we have just entered an {@link AstVariableInitializer} node.
     *
     * @param variableInitializer
     *         the {@link AstVariableInitializer} node we have just entered.
     *
     * @return the implementation output object.
     */
    default R visit(AstVariableInitializer variableInitializer) {
        return null;
    }

    /**
     * Gets called when we have just entered an {@link AstSwitchStatement} node.
     *
     * @param switchStatement
     *         the {@link AstSwitchStatement} node we have just entered.
     *
     * @return the implementation output object.
     */
    default R visit(AstSwitchStatement switchStatement) {
        return null;
    }

    /**
     * Gets called when we have just entered an {@link AstSwitchCase} node.
     *
     * @param switchCase
     *         the {@link AstSwitchCase} node we have just entered.
     *
     * @return the implementation output object.
     */
    default R visit(AstSwitchCase switchCase) {
        return null;
    }

    /**
     * Gets called when we have just entered an {@link AstIfStatement} node.
     *
     * @param ifStatement
     *         the {@link AstIfStatement} node we have just entered.
     *
     * @return the implementation output object.
     */
    default R visit(AstIfStatement ifStatement) {
        return null;
    }

    /**
     * Gets called when we have just entered an {@link AstWhileStatement} node.
     *
     * @param whileStatement
     *         the {@link AstWhileStatement} node we have just entered.
     *
     * @return the implementation output object.
     */
    default R visit(AstWhileStatement whileStatement) {
        return null;
    }

    /**
     * Gets called when we have just entered an {@link AstExpressionStatement} node.
     *
     * @param expressionStatement
     *         the {@link AstExpressionStatement} node we have just entered.
     *
     * @return the implementation output object.
     */
    default R visit(AstExpressionStatement expressionStatement) {
        return null;
    }

    /**
     * Gets called when we have just entered an {@link AstReturnStatement} node.
     *
     * @param returnStatement
     *         the {@link AstReturnStatement} node we have just entered.
     *
     * @return the implementation output object.
     */
    default R visit(AstReturnStatement returnStatement) {
        return null;
    }

    /**
     * Gets called when we have just entered an {@link AstBlockStatement} node.
     *
     * @param blockStatement
     *         the {@link AstBlockStatement} node we have just entered.
     *
     * @return the implementation output object.
     */
    default R visit(AstBlockStatement blockStatement) {
        return null;
    }
}

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
import me.waliedyassen.runescript.compiler.syntax.stmt.loop.DoWhileStatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.loop.WhileStatementSyntax;

/**
 * Represents a {@link SyntaxVisitor} implementation that will visit every node in the AST tree while having access to when
 * each node has entered and when each node has left the visitor.
 */
public abstract class SyntaxTreeVisitor implements SyntaxVisitor<Void> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(ScriptSyntax script) {
        enter(script);
        for (var parameter : script.getParameters()) {
            parameter.accept(this);
        }
        script.getCode().accept(this);
        exit(script);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link ScriptSyntax} node.
     *
     * @param script
     *         the node we have just entered.
     */
    public void enter(ScriptSyntax script) {
    }


    /**
     * Gets called when we have just left an {@link ScriptSyntax} node.
     *
     * @param script
     *         the node we have just entered.
     */
    public void exit(ScriptSyntax script) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AnnotationSyntax annotation) {
        enter(annotation);
        exit(annotation);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link AnnotationSyntax} node.
     *
     * @param annotation
     *         the node we have just entered.
     */
    public void enter(AnnotationSyntax annotation) {
    }

    /**
     * Gets called when we have just left an {@link ScriptSyntax} node.
     *
     * @param annotation
     *         the node we have just entered.
     */
    public void exit(AnnotationSyntax annotation) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(ParameterSyntax parameter) {
        enter(parameter);
        exit(parameter);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link ParameterSyntax} node.
     *
     * @param parameter
     *         the node we have just entered.
     */
    public void enter(ParameterSyntax parameter) {
    }


    /**
     * Gets called when we have just left an {@link ParameterSyntax} node.
     *
     * @param parameter
     *         the node we have just entered.
     */
    public void exit(ParameterSyntax parameter) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(ParExpressionSyntax syntax) {
        enter(syntax);
        syntax.getExpression().accept(this);
        exit(syntax);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link ParExpressionSyntax} node.
     *
     * @param syntax
     *         the node we have just entered.
     */
    public void enter(ParExpressionSyntax syntax) {
    }

    /**
     * Gets called when we have just left an {@link ParExpressionSyntax} node.
     *
     * @param syntax
     *         the node we have just entered.
     */
    public void exit(ParExpressionSyntax syntax) {
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(LiteralBooleanSyntax bool) {
        enter(bool);
        exit(bool);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link LiteralBooleanSyntax} node.
     *
     * @param bool
     *         the node we have just entered.
     */
    public void enter(LiteralBooleanSyntax bool) {
    }

    /**
     * Gets called when we have just left an {@link LiteralBooleanSyntax} node.
     *
     * @param bool
     *         the node we have just entered.
     */
    public void exit(LiteralBooleanSyntax bool) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(LiteralIntegerSyntax integer) {
        enter(integer);
        exit(integer);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link LiteralIntegerSyntax} node.
     *
     * @param integer
     *         the node we have just entered.
     */
    public void enter(LiteralIntegerSyntax integer) {
    }

    /**
     * Gets called when we have just left an {@link LiteralIntegerSyntax} node.
     *
     * @param integer
     *         the node we have just entered.
     */
    public void exit(LiteralIntegerSyntax integer) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(LiteralLongSyntax longInteger) {
        enter(longInteger);
        exit(longInteger);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link LiteralLongSyntax} node.
     *
     * @param longInteger
     *         the node we have just entered.
     */
    public void enter(LiteralLongSyntax longInteger) {
    }

    /**
     * Gets called when we have just left an {@link LiteralLongSyntax} node.
     *
     * @param longInteger
     *         the node we have just entered.
     */
    public void exit(LiteralLongSyntax longInteger) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(LiteralStringSyntax string) {
        enter(string);
        exit(string);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link LiteralStringSyntax} node.
     *
     * @param string
     *         the node we have just entered.
     */
    public void enter(LiteralStringSyntax string) {
    }

    /**
     * Gets called when we have just left an {@link LiteralStringSyntax} node.
     *
     * @param string
     *         the node we have just entered.
     */
    public void exit(LiteralStringSyntax string) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(LiteralCoordgridSyntax coordgrid) {
        enter(coordgrid);
        exit(coordgrid);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link LiteralCoordgridSyntax} node.
     *
     * @param coordgrid
     *         the node we have just entered.
     */
    public void enter(LiteralCoordgridSyntax coordgrid) {
    }

    /**
     * Gets called when we have just left an {@link LiteralCoordgridSyntax} node.
     *
     * @param coordgrid
     *         the node we have just entered.
     */
    public void exit(LiteralCoordgridSyntax coordgrid) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(ConcatenationSyntax concatenation) {
        enter(concatenation);
        for (var expression : concatenation.getExpressions()) {
            expression.accept(this);
        }
        exit(concatenation);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link ConcatenationSyntax} node.
     *
     * @param concatenation
     *         the node we have just entered.
     */
    public void enter(ConcatenationSyntax concatenation) {
    }

    /**
     * Gets called when we have just left an {@link ConcatenationSyntax} node.
     *
     * @param concatenation
     *         the node we have just entered.
     */
    public void exit(ConcatenationSyntax concatenation) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(VariableExpressionSyntax variableExpression) {
        enter(variableExpression);
        exit(variableExpression);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link VariableExpressionSyntax} node.
     *
     * @param variable
     *         the node we have just entered.
     */
    public void enter(VariableExpressionSyntax variable) {
    }

    /**
     * Gets called when we have just left an {@link VariableExpressionSyntax} node.
     *
     * @param variable
     *         the node we have just entered.
     */
    public void exit(VariableExpressionSyntax variable) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(ArrayElementSyntax arrayExpression) {
        enter(arrayExpression);
        exit(arrayExpression);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link ArrayElementSyntax} node.
     *
     * @param array
     *         the node we have just entered.
     */
    public void enter(ArrayElementSyntax array) {
    }

    /**
     * Gets called when we have just left an {@link ArrayElementSyntax} node.
     *
     * @param array
     *         the node we have just entered.
     */
    public void exit(ArrayElementSyntax array) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(HookSyntax hook) {
        enter(hook);
        if (hook.getName() != null) {
            hook.getName().accept(this);
        }
        if (hook.getArguments() != null) {
            for (var argument : hook.getArguments()) {
                argument.accept(this);
            }
        }
        if (hook.getTransmits() != null) {
            for (var transmit : hook.getTransmits()) {
                transmit.accept(this);
            }
        }
        exit(hook);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link HookSyntax} node.
     *
     * @param hook
     *         the node we have just entered.
     */
    public void enter(HookSyntax hook) {
    }

    /**
     * Gets called when we have just left an {@link HookSyntax} node.
     *
     * @param hook
     *         the node we have just entered.
     */
    public void exit(HookSyntax hook) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(CallSyntax call) {
        enter(call);
        for (var expression : call.getArguments()) {
            expression.accept(this);
        }
        exit(call);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link CallSyntax} node.
     *
     * @param call
     *         the node we have just entered.
     */
    public void enter(CallSyntax call) {
    }

    /**
     * Gets called when we have just left an {@link CallSyntax} node.
     *
     * @param call
     *         the node we have just entered.
     */
    public void exit(CallSyntax call) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(DynamicSyntax dynamic) {
        enter(dynamic);
        exit(dynamic);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link DynamicSyntax} node.
     *
     * @param dynamic
     *         the node we have just entered.
     */
    public void enter(DynamicSyntax dynamic) {
    }

    /**
     * Gets called when we have just left an {@link DynamicSyntax} node.
     *
     * @param dynamic
     *         the node we have just entered.
     */
    public void exit(DynamicSyntax dynamic) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(ConstantSyntax constant) {
        enter(constant);
        exit(constant);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link ConstantSyntax} node.
     *
     * @param constant
     *         the node we have just entered.
     */
    public void enter(ConstantSyntax constant) {
    }

    /**
     * Gets called when we have just left an {@link ConstantSyntax} node.
     *
     * @param constant
     *         the node we have just entered.
     */
    public void exit(ConstantSyntax constant) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(CommandSyntax command) {
        enter(command);
        for (var expression : command.getArguments()) {
            expression.accept(this);
        }
        exit(command);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link CommandSyntax} node.
     *
     * @param command
     *         the node we have just entered.
     */
    public void enter(CommandSyntax command) {
    }

    /**
     * Gets called when we have just left an {@link CommandSyntax} node.
     *
     * @param command
     *         the node we have just entered.
     */
    public void exit(CommandSyntax command) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(CalcSyntax calc) {
        enter(calc);
        calc.getExpression().accept(this);
        exit(calc);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link CalcSyntax} node.
     *
     * @param calc
     *         the node we have just entered.
     */
    public void enter(CalcSyntax calc) {
    }

    /**
     * Gets called when we have just left an {@link CalcSyntax} node.
     *
     * @param calc
     *         the node we have just entered.
     */
    public void exit(CalcSyntax calc) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(BinaryOperationSyntax binaryOperation) {
        enter(binaryOperation);
        binaryOperation.getLeft().accept(this);
        binaryOperation.getRight().accept(this);
        exit(binaryOperation);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link BinaryOperationSyntax} node.
     *
     * @param binaryOperation
     *         the node we have just entered.
     */
    public void enter(BinaryOperationSyntax binaryOperation) {
    }

    /**
     * Gets called when we have just left an {@link BinaryOperationSyntax} node.
     *
     * @param binaryOperation
     *         the node we have just entered.
     */
    public void exit(BinaryOperationSyntax binaryOperation) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(VariableDeclarationSyntax variableDeclaration) {
        enter(variableDeclaration);
        if (variableDeclaration.getExpression() != null) {
            variableDeclaration.getExpression().accept(this);
        }
        exit(variableDeclaration);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link VariableDeclarationSyntax} node.
     *
     * @param variableDeclaration
     *         the node we have just entered.
     */
    public void enter(VariableDeclarationSyntax variableDeclaration) {
    }

    /**
     * Gets called when we have just left an {@link VariableDeclarationSyntax} node.
     *
     * @param variableDeclaration
     *         the node we have just entered.
     */
    public void exit(VariableDeclarationSyntax variableDeclaration) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(ArrayDeclarationSyntax arrayDeclaration) {
        enter(arrayDeclaration);
        arrayDeclaration.getSize().accept(this);
        exit(arrayDeclaration);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link ArrayDeclarationSyntax} node.
     *
     * @param arrayDeclaration
     *         the node we have just entered.
     */
    public void enter(ArrayDeclarationSyntax arrayDeclaration) {
    }

    /**
     * Gets called when we have just left an {@link ArrayDeclarationSyntax} node.
     *
     * @param arrayDeclaration
     *         the node we have just entered.
     */
    public void exit(ArrayDeclarationSyntax arrayDeclaration) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(VariableInitializerSyntax variableInitializer) {
        enter(variableInitializer);
        for (var expression : variableInitializer.getExpressions()) {
            expression.accept(this);
        }
        exit(variableInitializer);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link VariableInitializerSyntax} node.
     *
     * @param variableInitializer
     *         the node we have just entered.
     */
    public void enter(VariableInitializerSyntax variableInitializer) {
    }

    /**
     * Gets called when we have just left an {@link VariableInitializerSyntax} node.
     *
     * @param variableInitializer
     *         the node we have just entered.
     */
    public void exit(VariableInitializerSyntax variableInitializer) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(SwitchStatementSyntax switchStatement) {
        enter(switchStatement);
        switchStatement.getCondition().accept(this);
        for (var switchCase : switchStatement.getCases()) {
            switchCase.accept(this);
        }
        if (switchStatement.getDefaultCase() != null) {
            switchStatement.getDefaultCase().accept(this);
        }
        exit(switchStatement);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link SwitchStatementSyntax} node.
     *
     * @param switchStatement
     *         the node we have just entered.
     */
    public void enter(SwitchStatementSyntax switchStatement) {
    }

    /**
     * Gets called when we have just left an {@link SwitchStatementSyntax} node.
     *
     * @param switchStatement
     *         the node we have just entered.
     */
    public void exit(SwitchStatementSyntax switchStatement) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(SwitchCaseSyntax switchCase) {
        enter(switchCase);
        for (var expression : switchCase.getKeys()) {
            expression.accept(this);
        }
        switchCase.getCode().accept(this);
        exit(switchCase);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link SwitchCaseSyntax} node.
     *
     * @param switchCase
     *         the node we have just entered.
     */
    public void enter(SwitchCaseSyntax switchCase) {
    }

    /**
     * Gets called when we have just left an {@link SwitchCaseSyntax} node.
     *
     * @param switchCase
     *         the node we have just entered.
     */
    public void exit(SwitchCaseSyntax switchCase) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(IfStatementSyntax ifStatement) {
        enter(ifStatement);
        ifStatement.getCondition().accept(this);
        ifStatement.getTrueStatement().accept(this);
        if (ifStatement.getFalseStatement() != null) {
            ifStatement.getFalseStatement().accept(this);
        }
        exit(ifStatement);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link IfStatementSyntax} node.
     *
     * @param ifStatement
     *         the node we have just entered.
     */
    public void enter(IfStatementSyntax ifStatement) {
    }

    /**
     * Gets called when we have just left an {@link IfStatementSyntax} node.
     *
     * @param ifStatement
     *         the node we have just entered.
     */
    public void exit(IfStatementSyntax ifStatement) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(WhileStatementSyntax whileStatement) {
        enter(whileStatement);
        whileStatement.getCondition().accept(this);
        whileStatement.getCode().accept(this);
        exit(whileStatement);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link WhileStatementSyntax} node.
     *
     * @param whileStatement
     *         the node we have just entered.
     */
    public void enter(WhileStatementSyntax whileStatement) {
    }

    /**
     * Gets called when we have just left an {@link WhileStatementSyntax} node.
     *
     * @param whileStatement
     *         the node we have just entered.
     */
    public void exit(WhileStatementSyntax whileStatement) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(DoWhileStatementSyntax doWhileStatementSyntax) {
        enter(doWhileStatementSyntax);
        doWhileStatementSyntax.getCode().accept(this);
        doWhileStatementSyntax.getCondition().accept(this);
        exit(doWhileStatementSyntax);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link DoWhileStatementSyntax} node.
     *
     * @param doWhileStatementSyntax
     *         the node we have just entered.
     */
    public void enter(DoWhileStatementSyntax doWhileStatementSyntax) {
    }

    /**
     * Gets called when we have just left an {@link DoWhileStatementSyntax} node.
     *
     * @param doWhileStatementSyntax
     *         the node we have just entered.
     */
    public void exit(DoWhileStatementSyntax doWhileStatementSyntax) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(ExpressionStatementSyntax expressionStatement) {
        enter(expressionStatement);
        expressionStatement.getExpression().accept(this);
        exit(expressionStatement);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link ExpressionStatementSyntax} node.
     *
     * @param expressionStatement
     *         the node we have just entered.
     */
    public void enter(ExpressionStatementSyntax expressionStatement) {
    }

    /**
     * Gets called when we have just left an {@link ExpressionStatementSyntax} node.
     *
     * @param expressionStatement
     *         the node we have just entered.
     */
    public void exit(ExpressionStatementSyntax expressionStatement) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(ReturnStatementSyntax returnStatement) {
        enter(returnStatement);
        for (var expression : returnStatement.getExpressions()) {
            expression.accept(this);
        }
        exit(returnStatement);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link ReturnStatementSyntax} node.
     *
     * @param returnStatement
     *         the node we have just entered.
     */
    public void enter(ReturnStatementSyntax returnStatement) {
    }

    /**
     * Gets called when we have just left an {@link ReturnStatementSyntax} node.
     *
     * @param returnStatement
     *         the node we have just entered.
     */
    public void exit(ReturnStatementSyntax returnStatement) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(BlockStatementSyntax blockStatement) {
        enter(blockStatement);
        for (var statement : blockStatement.getStatements()) {
            statement.accept(this);
        }
        exit(blockStatement);
        return null;
    }

    /**
     * Gets called when we have just entered an {@link BlockStatementSyntax} node.
     *
     * @param blockStatement
     *         the node we have just entered.
     */
    public void enter(BlockStatementSyntax blockStatement) {
    }

    /**
     * Gets called when we have just left an {@link BlockStatementSyntax} node.
     *
     * @param blockStatement
     *         the node we have just entered.
     */
    public void exit(BlockStatementSyntax blockStatement) {
    }
}

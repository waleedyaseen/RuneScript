package me.waliedyassen.runescript.compiler.ast.visitor;

import me.waliedyassen.runescript.compiler.ast.AstParameter;
import me.waliedyassen.runescript.compiler.ast.AstScript;
import me.waliedyassen.runescript.compiler.ast.expr.*;
import me.waliedyassen.runescript.compiler.ast.literal.*;
import me.waliedyassen.runescript.compiler.ast.stmt.*;
import me.waliedyassen.runescript.compiler.ast.stmt.conditional.AstIfStatement;
import me.waliedyassen.runescript.compiler.ast.stmt.conditional.AstWhileStatement;

/**
 * Represents a {@link AstVisitor} implementation that will visit every node in the AST tree.
 */
public abstract class AstTreeVisitor implements AstVisitor<Void> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstScript script) {
        for (var parameter : script.getParameters()) {
            parameter.accept(this);
        }
        script.getCode().accept(this);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstParameter parameter) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstBool bool) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstInteger integer) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstLong longInteger) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstString string) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstStringConcat stringConcat) {
        for (var expression : stringConcat.getExpressions()) {
            expression.accept(this);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstVariableExpression variable) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstGosub gosub) {
        for (var expression : gosub.getArguments()) {
            expression.accept(this);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstDynamic dynamic) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstConstant constant) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstCommand command) {
        for (var expression : command.getArguments()) {
            expression.accept(this);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstBinaryOperation binaryOperation) {
        binaryOperation.getLeft().accept(this);
        binaryOperation.getRight().accept(this);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstVariableDeclaration variableDeclaration) {
        variableDeclaration.getExpression().accept(this);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstVariableInitializer variableInitializer) {
        variableInitializer.getExpression().accept(this);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstSwitchStatement switchStatement) {
        switchStatement.getCondition().accept(this);
        for (var switchCase : switchStatement.getCases()) {
            switchCase.accept(this);
        }
        if (switchStatement.getDefaultCase() != null) {
            switchStatement.accept(this);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstSwitchCase switchCase) {
        for (var expression : switchCase.getKeys()) {
            expression.accept(this);
        }
        switchCase.getCode().accept(this);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstIfStatement ifStatement) {
        ifStatement.getCondition().accept(this);
        ifStatement.getTrueStatement().accept(this);
        if (ifStatement.getFalseStatement() != null) {
            ifStatement.getFalseStatement().accept(this);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstWhileStatement whileStatement) {
        whileStatement.getCondition().accept(this);
        whileStatement.getCode().accept(this);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstExpressionStatement expressionStatement) {
        expressionStatement.getExpression().accept(this);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstReturnStatement returnStatement) {
        for (var expression : returnStatement.getExpressions()) {
            expression.accept(this);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstBlockStatement blockStatement) {
        for (var statement : blockStatement.getStatements()) {
            statement.accept(this);
        }
        return null;
    }
}

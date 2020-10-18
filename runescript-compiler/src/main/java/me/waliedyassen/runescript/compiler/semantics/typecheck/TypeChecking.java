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
import me.waliedyassen.runescript.compiler.symbol.impl.CommandInfo;
import me.waliedyassen.runescript.compiler.symbol.impl.script.ScriptInfo;
import me.waliedyassen.runescript.compiler.syntax.ParameterSyntax;
import me.waliedyassen.runescript.compiler.syntax.ScriptSyntax;
import me.waliedyassen.runescript.compiler.syntax.Syntax;
import me.waliedyassen.runescript.compiler.syntax.SyntaxBase;
import me.waliedyassen.runescript.compiler.syntax.expr.*;
import me.waliedyassen.runescript.compiler.syntax.expr.literal.*;
import me.waliedyassen.runescript.compiler.syntax.expr.op.BinaryOperationSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.*;
import me.waliedyassen.runescript.compiler.syntax.stmt.conditional.IfStatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.loop.BreakStatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.loop.ContinueStatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.loop.DoWhileStatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.loop.WhileStatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;
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
public final class TypeChecking implements SyntaxVisitor<TypeChecking.Action> {

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
    public Action visit(ScriptSyntax script) {
        this.script = script;
        script.getCode().accept(this);
        return Action.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(ParameterSyntax parameter) {
        parameter.setType(PrimitiveType.forRepresentation(parameter.getTypeToken().getLexeme()));
        return Action.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(ParExpressionSyntax syntax) {
        if (syntax.getExpression().accept(this).isContinue()) {
            syntax.setType(syntax.getExpression().getType());
            return Action.CONTINUE;
        }
        return Action.SKIP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(LiteralBooleanSyntax bool) {
        bool.setType(PrimitiveType.BOOLEAN);
        return Action.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(LiteralIntegerSyntax integer) {
        integer.setType(PrimitiveType.INT);
        return Action.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(LiteralLongSyntax longInteger) {
        longInteger.setType(PrimitiveType.LONG);
        return Action.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(LiteralStringSyntax string) {
        if (symbolTable.lookupGraphic(string.getValue()) != null) {
            string.setType(PrimitiveType.GRAPHIC);
        } else {
            string.setType(PrimitiveType.STRING);
        }
        return Action.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(LiteralCoordgridSyntax coordgrid) {
        coordgrid.setType(PrimitiveType.COORDGRID);
        return Action.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(LiteralNullSyntax literalNullSyntax) {
        literalNullSyntax.setType(PrimitiveType.NULL);
        return Action.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(LiteralTypeSyntax literalTypeSyntax) {
        literalTypeSyntax.setType(PrimitiveType.TYPE);
        return Action.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(ConcatenationSyntax concatenation) {
        for (var expr : concatenation.getExpressions()) {
            if (expr.accept(this).isContinue()) {
                checkTypeMatching(expr, PrimitiveType.STRING, expr.getType());
            }
        }
        concatenation.setType(PrimitiveType.STRING);
        return Action.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(VariableExpressionSyntax variableExpression) {
        return variableExpression.hasType() ? Action.CONTINUE : Action.SKIP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(ArrayElementSyntax arrayExpression) {
        if (arrayExpression.getArray() == null) {
            return Action.SKIP;
        }
        arrayExpression.setType(arrayExpression.getArray().getType());
        return Action.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(HookSyntax hook) {
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
                            if (transmit.accept(this).isContinue()) {
                                checkTypeMatching(transmit, expected, transmit.getType());
                            }
                        }
                    }
                } else if (transmits.length != 0) {
                    checker.reportError(new SemanticError(hook, "Unexpected transmit list"));
                }
            }
        }
        hook.setType(PrimitiveType.HOOK);
        return Action.CONTINUE;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(CallSyntax call) {
        var name = call.getName();
        var info = symbolTable.lookupScript(call.getTriggerType(), name.getText());
        if (info == null) {
            checker.reportError(new SemanticError(call, String.format("Could not resolve %s script with the name '%s'", call.getTriggerType().getRepresentation(), name.getText())));
            return Action.SKIP;
        }
        checkCallApplicable(call, info, call.getArguments());
        call.setType(info.getType());
        return Action.CONTINUE;
    }

    /**
     * Checks whether or ont a script call is applicable.
     *
     * @param call      the AST node object of the call.
     * @param info      the information of the script we are calling.
     * @param arguments the arguments that are used in the call.
     */
    private void checkCallApplicable(SyntaxBase call, ScriptInfo info, ExpressionSyntax[] arguments) {
        var check = true;
        for (var argument : arguments) {
            check &= argument.accept(this).isContinue();
        }
        if (check) {
            var actual = collectType(arguments);
            var expected = new TupleType(info.getArguments());
            if (!checkTypeMatching(call, expected, actual, false)) {
                checker.reportError(new SemanticError(call, String.format("The script %s(%s) is not applicable for the arguments (%s)", info.getName(), expected.getRepresentation(), actual.getRepresentation())));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(DynamicSyntax dynamic) {
        if (dynamic.hasType()) {
            // This means an array expression was assigned to this, this is handled in PerTypeChecking
            return Action.CONTINUE;
        }
        var name = dynamic.getName();
        var commandInfo = symbolTable.lookupCommand(name.getText());
        if (commandInfo != null) {
            if (commandInfo.getArguments().length > 0) {
                checker.reportError(new SemanticError(name, String.format("The command %s(%s) is not applicable for the arguments ()", name.getText(), TypeUtil.createRepresentation(commandInfo.getArguments()))));
            }
            dynamic.setType(commandInfo.getType());
            return Action.CONTINUE;
        }
        var configInfo = symbolTable.lookupConfig(name.getText());
        if (configInfo != null) {
            dynamic.setType(configInfo.getType());
            return Action.CONTINUE;
        }
        var runtimeConstantInfo = symbolTable.lookupRuntimeConstant(name.getText());
        if (runtimeConstantInfo != null) {
            dynamic.setType(runtimeConstantInfo.getType());
            return Action.CONTINUE;
        }
        checker.reportError(new SemanticError(name, String.format("%s cannot be resolved to a symbol", name.getText())));
        return Action.SKIP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(ConstantSyntax constant) {
        var name = constant.getName();
        var info = symbolTable.lookupConstant(name.getText());
        if (info == null) {
            checker.reportError(new SemanticError(name, String.format("%s cannot be resolved to a constant", name.getText())));
            return Action.SKIP;
        }
        constant.setType(info.getType());
        return Action.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(CommandSyntax commandSyntax) {
        var name = commandSyntax.getName();
        var info = symbolTable.lookupCommand(name.getText());
        if (info == null) {
            checker.reportError(new SemanticError(name, String.format("%s cannot be resolved to a command", name.getText())));
            return Action.SKIP;
        }
        final var actual = commandSyntax.getArguments();
        var check = true;
        for (var expressionSyntax : actual) {
            check &= expressionSyntax.accept(this).isContinue();
        }
        if (check) {
            var actualType = collectType(actual);
            var expectedTypes = processCommandExpectedArguments(info, actual);
            var expectedType = new TupleType(expectedTypes);
            if (!checkTypeMatching(commandSyntax, expectedType, actualType, false)) {
                checker.reportError(new SemanticError(commandSyntax, String.format("The command %s(%s) is not applicable for the arguments (%s)", name.getText(), actualType.getRepresentation(), expectedType.getRepresentation())));
            }
        }
        var returnType = processCommandExpectedReturns(info, actual);
        if (returnType != null) {
            commandSyntax.setType(returnType);
            return Action.CONTINUE;
        } else {
            return Action.SKIP;
        }
    }

    /**
     * Process the command expected argument types.
     *
     * @param info   the information of the command.
     * @param actual the argument expressions used for the command.
     * @return the processed expected argument {@link Type} array object.
     */
    private Type[] processCommandExpectedArguments(CommandInfo info, ExpressionSyntax[] actual) {
        var expectedTypes = info.getArguments();
        if (info.isEnum()) {
            if (actual.length > 0 && actual[0] instanceof LiteralTypeSyntax) {
                // argument 1 is "inputtype"
                var literal = (LiteralTypeSyntax) actual[0];
                expectedTypes = Arrays.copyOf(expectedTypes, expectedTypes.length);
                expectedTypes[3] = literal.getValue();
            }
        }
        return expectedTypes;
    }

    /**
     * Process the command expected return types.
     *
     * @param info   the information of the command.
     * @param actual the argument expressions used for the command.
     * @return the processed expected return {@link Type} object.
     */
    private Type processCommandExpectedReturns(CommandInfo info, ExpressionSyntax[] actual) {
        if (info.isEnum()) {
            if (actual.length > 1 && actual[1] instanceof LiteralTypeSyntax) {
                // argument 1 is "outputtype"
                var literal = (LiteralTypeSyntax) actual[1];
                return literal.getValue();
            }
        } else if (info.isParam()) {
            if (actual.length > 1 && actual[1] instanceof DynamicSyntax) {
                // argument 1 is "param"
                var literal = (DynamicSyntax) actual[1];
                var configInfo = symbolTable.lookupConfig(literal.getName().getText());
                if (configInfo != null && configInfo.getContentType() != null) {
                    return configInfo.getContentType();
                }
                return null;
            }
        }
        return info.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(CalcSyntax calc) {
        calc.setType(PrimitiveType.INT);
        if (calc.getExpression().accept(this).isContinue()) {
            checkTypeMatching(calc.getExpression(), PrimitiveType.INT, calc.getExpression().getType());
        }
        return Action.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(BinaryOperationSyntax binaryOperation) {
        var check = true;
        check &= binaryOperation.getLeft().accept(this).isContinue();
        check &= binaryOperation.getRight().accept(this).isContinue();
        if (check) {
            var type = checkOperator(binaryOperation, binaryOperation.getLeft().getType(), binaryOperation.getRight().getType(), binaryOperation.getOperator());
            binaryOperation.setType(type);
            return Action.CONTINUE;
        }
        return Action.SKIP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(VariableDeclarationSyntax variableDeclaration) {
        var expression = variableDeclaration.getExpression();
        if (expression == null) {
            if (variableDeclaration.getType().getDefaultValue() == null) {
                checker.reportError(new SemanticError(variableDeclaration, "Variables with type '" + variableDeclaration.getType().getRepresentation() + "' must be initialised"));
            }
            return Action.CONTINUE;
        }
        if (expression.accept(this).isContinue()) {
            checkTypeMatching(expression, variableDeclaration.getType(), expression.getType());
            return Action.CONTINUE;
        }
        return Action.SKIP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(ArrayDeclarationSyntax arrayDeclaration) {
        if (arrayDeclaration.getType().getStackType() != StackType.INT) {
            checker.reportError(new SemanticError(arrayDeclaration, "Arrays can only have a type that is derived from the int type"));
        }
        if (arrayDeclaration.getSize().accept(this).isContinue()) {
            checkTypeMatching(arrayDeclaration.getSize(), PrimitiveType.INT, arrayDeclaration.getSize().getType());
        }
        return Action.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(VariableInitializerSyntax variableInitializer) {
        var check = true;
        for (var expr : variableInitializer.getExpressions()) {
            check &= expr.accept(this).isContinue();
        }
        for (var var : variableInitializer.getVariables()) {
            check &= var.accept(this).isContinue();
        }
        if (check) {
            var varTuple = collectType(variableInitializer.getVariables());
            var exprTuple = collectType(variableInitializer.getExpressions());
            if (!exprTuple.equals(varTuple)) {
                checker.reportError(new SemanticError(variableInitializer, String.format("Mismatch variable initializer expected: %s but got: %s", varTuple.getRepresentation(), exprTuple.getRepresentation())));
            }
        }
        return Action.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(ScopedVariableSyntax scopedVariable) {
        return scopedVariable.hasType() ? Action.CONTINUE : Action.SKIP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(ArrayVariableSyntax arrayVariable) {
        if (arrayVariable.getArrayInfo() == null) {
            return Action.SKIP;
        }
        arrayVariable.setType(arrayVariable.getArrayInfo().getType());
        return Action.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(SwitchStatementSyntax switchStatement) {
        var type = switchStatement.getType();
        if (switchStatement.getCondition().accept(this).isContinue()) {
            checkTypeMatching(switchStatement.getCondition(), type, switchStatement.getCondition().getType());
        }
        var defined_keys = new HashSet<Integer>();
        for (var switchCase : switchStatement.getCases()) {
            var resolvedKeys = new int[switchCase.getKeys().length];
            for (var index = 0; index < resolvedKeys.length; index++) {
                var key = switchCase.getKeys()[index];
                if (key.accept(this).isContinue() && checkTypeMatching(key, type, key.getType())) {
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
    public Action visit(SwitchCaseSyntax switchCase) {
        return switchCase.getCode().accept(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(IfStatementSyntax ifStatement) {
        var cond = ifStatement.getCondition();
        if (cond.accept(this).isContinue()) {
            checkTypeMatching(ifStatement.getCondition(), PrimitiveType.BOOLEAN, cond.getType());
        }
        ifStatement.getTrueStatement().accept(this);
        if (ifStatement.getFalseStatement() != null) {
            ifStatement.getFalseStatement().accept(this);
        }
        return Action.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(WhileStatementSyntax whileStatement) {
        whileStatement.getCode().accept(this);
        var cond = whileStatement.getCondition();
        if (cond.accept(this).isContinue()) {
            checkTypeMatching(whileStatement.getCondition(), PrimitiveType.BOOLEAN, cond.getType());
        }
        return Action.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(DoWhileStatementSyntax doWhileStatementSyntax) {
        doWhileStatementSyntax.getCode().accept(this);
        var cond = doWhileStatementSyntax.getCondition();
        if (cond.accept(this).isContinue()) {
            checkTypeMatching(doWhileStatementSyntax.getCondition(), PrimitiveType.BOOLEAN, cond.getType());
        }
        return Action.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(ContinueStatementSyntax continueStatementSyntax) {
        var whileStatementSyntax = continueStatementSyntax.selectParent(syntax -> syntax instanceof WhileStatementSyntax);
        if (whileStatementSyntax == null) {
            checker.reportError(new SemanticError(continueStatementSyntax, "Continue statement is not allowed outside of a loop"));
        }
        return Action.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(BreakStatementSyntax breakStatementSyntax) {
        var whileStatementSyntax = breakStatementSyntax.selectParent(syntax -> syntax instanceof WhileStatementSyntax);
        if (whileStatementSyntax == null) {
            checker.reportError(new SemanticError(breakStatementSyntax, "Break statement is not allowed outside of a loop"));
        }
        return Action.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(ExpressionStatementSyntax expressionStatement) {
        expressionStatement.getExpression().accept(this);
        return Action.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(ReturnStatementSyntax returnStatement) {
        var expressions = returnStatement.getExpressions();
        var check = true;
        for (var expr : expressions) {
            check &= expr.accept(this).isContinue();
        }
        if (check) {
            var type = collectType(returnStatement.getExpressions());
            checkTypeMatching(returnStatement, script.getType(), type);
        }
        return Action.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action visit(BlockStatementSyntax blockStatement) {
        for (var statement : blockStatement.getStatements()) {
            statement.accept(this);
        }
        return Action.CONTINUE;
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
            applicable = checkTypeMatching(null, left, right, false);
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
    private boolean checkTypeMatching(SyntaxBase node, Type expected, Type actual) {
        return checkTypeMatching(node, expected, actual, true);
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
    private boolean checkTypeMatching(SyntaxBase node, Type expected, Type actual, boolean reportError) {
        Type[] expectedFlattened = expected instanceof TupleType ? ((TupleType) expected).getFlattened() : new Type[]{expected};
        Type[] actualFlattened = actual instanceof TupleType ? ((TupleType) actual).getFlattened() : new Type[]{actual};
        boolean applicable = true;
        if (expectedFlattened.length != actualFlattened.length) {
            applicable = false;
        } else {
            for (int index = 0; index < expectedFlattened.length; index++) {
                Type expectedType = expectedFlattened[index];
                Type actualType = actualFlattened[index];
                applicable &= isTypeCompatible(expectedType, actualType);
            }
        }
        if (!applicable && reportError) {
            checker.reportError(new SemanticError(node, "Type mismatch: cannot convert from " + actual.getRepresentation() + " to " + expected.getRepresentation()));
        }
        return applicable;
    }

    /**
     * Checks whether or not the two given types are compatible.
     *
     * @param first  the first type to check.
     * @param second the second type to check.
     * @return <code>true</code> they are otherwise <code>false</code>.
     */
    private boolean isTypeCompatible(Type first, Type second) {
        if (first == PrimitiveType.NULL || second == PrimitiveType.NULL) {
            Type other = first == PrimitiveType.NULL ? second : first;
            return other instanceof PrimitiveType && ((PrimitiveType) other).isNullable();
        } else {
            return first.equals(second);
        }
    }

    /**
     * Collects the types of the specified {@link Syntax} nodes and put them in an appropriate type.
     *
     * @param nodes the nodes that we want to collect the types from.
     * @return the {@link Type} of the nodes.
     */
    private Type collectType(Syntax[] nodes) {
        if (nodes.length == 0) {
            return PrimitiveType.VOID;
        } else if (nodes.length == 1) {
            return nodes[0].getType();
        }
        return new TupleType(Arrays.stream(nodes).map(Syntax::getType).toArray(Type[]::new));
    }

    /**
     * Represents the action that should be performed when returned from a child type checking.
     *
     * @author Walied K. Yassen
     */
    public enum Action {

        /**
         * Skip the type checking for the parent node and make the parent node skip if necessary.
         */
        SKIP,

        /**
         * Continue the type checking normally.
         */
        CONTINUE;

        /**
         * Checks whether or not this action is a continue action.
         *
         * @return <code>true</code> if it is otherwise <code>false</code>.
         */
        public boolean isContinue() {
            return this == CONTINUE;
        }
    }
}
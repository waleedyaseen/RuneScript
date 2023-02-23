/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.semantics.typecheck;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.waliedyassen.runescript.compiler.ScriptCompiler;
import me.waliedyassen.runescript.compiler.error.ErrorReporter;
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
import me.waliedyassen.runescript.compiler.type.ArrayReference;
import me.waliedyassen.runescript.compiler.util.Operator;
import me.waliedyassen.runescript.compiler.util.trigger.TriggerType;
import me.waliedyassen.runescript.type.Type;
import me.waliedyassen.runescript.type.TypeUtil;
import me.waliedyassen.runescript.type.primitive.PrimitiveType;
import me.waliedyassen.runescript.type.stack.StackType;
import me.waliedyassen.runescript.type.tuple.TupleType;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents the type checking semantic analysis.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class TypeChecking implements SyntaxVisitor<TypeCheckAction> {

    private final ScriptCompiler compiler;

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
    public TypeCheckAction visit(ScriptSyntax script) {
        this.script = script;
        script.getCode().accept(this);
        return TypeCheckAction.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(ParameterSyntax parameter) {
        parameter.setType(PrimitiveType.forRepresentation(parameter.getTypeToken().getLexeme()));
        return TypeCheckAction.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(ParExpressionSyntax syntax) {
        syntax.getExpression().setHintType(syntax.getHintType());
        if (syntax.getExpression().accept(this).isContinue()) {
            syntax.setType(syntax.getExpression().getType());
            return TypeCheckAction.CONTINUE;
        }
        return TypeCheckAction.SKIP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(LiteralBooleanSyntax bool) {
        bool.setType(PrimitiveType.BOOLEAN.INSTANCE);
        return TypeCheckAction.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(LiteralIntegerSyntax integer) {
        integer.setType(PrimitiveType.INT.INSTANCE);
        return TypeCheckAction.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(LiteralLongSyntax longInteger) {
        longInteger.setType(PrimitiveType.LONG.INSTANCE);
        return TypeCheckAction.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(LiteralStringSyntax string) {
        if (symbolTable.lookupConfig(PrimitiveType.GRAPHIC.INSTANCE, string.getValue()) != null) {
            string.setType(PrimitiveType.GRAPHIC.INSTANCE);
        } else {
            string.setType(PrimitiveType.STRING.INSTANCE);
        }
        return TypeCheckAction.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(LiteralCoordgridSyntax coordgrid) {
        coordgrid.setType(PrimitiveType.COORDGRID.INSTANCE);
        return TypeCheckAction.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(LiteralNullSyntax literalNullSyntax) {
        literalNullSyntax.setType(PrimitiveType.NULL.INSTANCE);
        return TypeCheckAction.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(LiteralTypeSyntax literalTypeSyntax) {
        literalTypeSyntax.setType(PrimitiveType.TYPE.INSTANCE);
        return TypeCheckAction.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(ConcatenationSyntax concatenation) {
        for (var expr : concatenation.getExpressions()) {
            if (expr.accept(this).isContinue()) {
                checkTypeMatching(expr, PrimitiveType.STRING.INSTANCE, expr.getType());
            }
        }
        concatenation.setType(PrimitiveType.STRING.INSTANCE);
        return TypeCheckAction.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(VariableExpressionSyntax variableExpression) {
        return variableExpression.hasType() && variableExpression.getType() != PrimitiveType.UNDEFINED.INSTANCE ? TypeCheckAction.CONTINUE : TypeCheckAction.SKIP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(ArrayElementSyntax arrayExpression) {
        if (arrayExpression.getArray() == null) {
            return TypeCheckAction.SKIP;
        }
        arrayExpression.setType(arrayExpression.getArray().getType());
        return TypeCheckAction.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(HookSyntax hook) {
        if (hookTriggerType == null) {
            checker.reportError(new SemanticError(hook, "Hooks are not allowed"));
        } else if (hook.getName() != null) {
            var fullName = String.format("[%s,%s]", hookTriggerType.getRepresentation(), hook.getName().getText());
            var parentInfo = symbolTable.lookupCommand(((CommandSyntax) hook.getParent()).getName().getText());
            var scriptInfo = symbolTable.lookupScript(fullName);
            if (scriptInfo == null) {
                checker.reportError(new SemanticError(hook.getName(), String.format("Could not resolve %s script with the name '%s'", hookTriggerType.getRepresentation(), hook.getName().getText())));
            } else {
                checkCallApplicable(hook, scriptInfo, hook.getArguments());
            }
            if (parentInfo != null) {
                var expected = parentInfo.getTransmits();
                var transmits = hook.getTransmits();
                if (expected != null) {
                    for (var transmit : transmits) {
                        transmit.setHintType(expected);
                        if (transmit.accept(this).isContinue()) {
                            checkTypeMatching(transmit, expected, transmit.getType());
                        }
                    }
                } else if (transmits.length != 0) {
                    checker.reportError(new SemanticError(hook, "Unexpected transmit list"));
                }
            }
        }
        hook.setType(PrimitiveType.HOOK.INSTANCE);
        return TypeCheckAction.CONTINUE;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(CallSyntax call) {
        final var triggerType = call.getTriggerType();
        var fullName = String.format("[%s,%s]", triggerType.getRepresentation(), call.getName().getText());
        var info = symbolTable.lookupScript(fullName);
        if (info == null) {
            checker.reportError(new SemanticError(call, String.format("Could not resolve %s script with the name '%s'", call.getTriggerType().getRepresentation(), call.getName().getText())));
            return TypeCheckAction.SKIP;
        }
        checkCallApplicable(call, info, call.getArguments());
        call.setType(info.getType());
        return TypeCheckAction.CONTINUE;
    }

    /**
     * Checks whether or ont a script call is applicable.
     *
     * @param call      the AST node object of the call.
     * @param info      the information of the script we are calling.
     * @param arguments the arguments that are used in the call.
     */
    private void checkCallApplicable(SyntaxBase call, ScriptInfo info, ExpressionSyntax[] arguments) {
        Type[] expectedArgumentTypes = info.getArguments();
        var check = true;
        var index = 0;
        for (var argument : arguments) {
            if (index < expectedArgumentTypes.length) argument.setHintType(expectedArgumentTypes[index]);
            var result = argument.accept(this);
            index += TypeUtil.flatten(new Type[]{argument.getType()}).length;
            check &= result.isContinue();
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
    public TypeCheckAction visit(DynamicSyntax dynamic) {
        if (dynamic.hasType()) {
            // This means an array expression was assigned to this, this is handled in PerTypeChecking
            return TypeCheckAction.CONTINUE;
        }
        var name = dynamic.getName();
        var commandInfo = symbolTable.lookupCommand(name.getText());
        if (commandInfo != null) {
            if (commandInfo.getArguments().length > 0) {
                checker.reportError(new SemanticError(name, String.format("The command %s(%s) is not applicable for the arguments ()", name.getText(), TypeUtil.createRepresentation(commandInfo.getArguments()))));
            }
            dynamic.setType(commandInfo.getType());
            return TypeCheckAction.CONTINUE;
        }
        var configInfo = symbolTable.lookupConfig((PrimitiveType<?>) dynamic.getHintType(), name.getText());
        if (configInfo != null) {
            dynamic.setType(dynamic.getHintType());
            return TypeCheckAction.CONTINUE;
        }
        var runtimeConstantInfo = symbolTable.lookupRuntimeConstant(name.getText());
        if (runtimeConstantInfo != null) {
            dynamic.setType(runtimeConstantInfo.getType());
            return TypeCheckAction.CONTINUE;
        }
        checker.reportError(new SemanticError(name, String.format("%s cannot be resolved to a symbol", name.getText())));
        return TypeCheckAction.SKIP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(ConstantSyntax constant) {
        var name = constant.getName();
        var info = symbolTable.lookupConfig(PrimitiveType.CONSTANT.INSTANCE, name.getText());
        if (info == null) {
            checker.reportError(new SemanticError(name, String.format("%s cannot be resolved to a constant", name.getText())));
            return TypeCheckAction.SKIP;
        }
        if (constant.getHintType() == null) {
            checker.reportError(new SemanticError(name, String.format("%s constant type cannot be inferred (value: %s)", name.getText(), info.getLiteral())));
            return TypeCheckAction.SKIP;
        }
        constant.setType(constant.getHintType());
        constant.setValue(parseConstantValue(constant, info.getLiteral(), constant.getHintType()));
        return TypeCheckAction.CONTINUE;
    }

    @SneakyThrows
    private Object parseConstantValue(ConstantSyntax constant, String literal, Type hintType) {
        if (hintType == PrimitiveType.STRING.INSTANCE && !literal.startsWith("\"")) {
            literal = "\"" + literal.replaceAll("\"", "\\\"") + "\"";
        }
        var errorReporter = new ErrorReporter();
        var parser = compiler.createParser(symbolTable, errorReporter, literal.getBytes(), "cs2");
        Object result;
        if (hintType == PrimitiveType.INT.INSTANCE) {
            result = parser.literalInteger().getValue();
        } else if (hintType == PrimitiveType.STRING.INSTANCE) {
            result = parser.literalString().getValue();
        } else {
            checker.reportError(new SemanticError(constant, "Only int or string constants are currently allowed"));
            return null;
        }
        if (!errorReporter.getErrors().isEmpty()) {
            checker.reportError(new SemanticError(constant, "Problem occurred while parsing constnat value"));
            return null;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(CommandSyntax commandSyntax) {
        var name = commandSyntax.getName();
        var info = symbolTable.lookupCommand(name.getText());
        if (info == null) {
            checker.reportError(new SemanticError(name, String.format("%s cannot be resolved to a command", name.getText())));
            return TypeCheckAction.SKIP;
        }
        final var tempTypes = info.getArguments();
        final var arguments = commandSyntax.getArguments();
        boolean isEnum = commandSyntax.getName().getText().equals("enum");
        var check = true;
        var index = 0;
        for (var argument : arguments) {
            if (isEnum && index == 3 && arguments[0] instanceof LiteralTypeSyntax typeSyntax) {
                argument.setHintType(typeSyntax.getValue());
            } else if (index < tempTypes.length) {
                argument.setHintType(tempTypes[index]);
            }
            var result = argument.accept(this);
            index += TypeUtil.flatten(new Type[]{argument.getType()}).length;
            check &= result.isContinue();
        }
        if (check) {
            var actualType = collectType(arguments);
            var expectedTypes = processCommandExpectedArguments(info, arguments);
            var expectedType = new TupleType(expectedTypes);
            if (!checkTypeMatching(commandSyntax, expectedType, actualType, false)) {
                checker.reportError(new SemanticError(commandSyntax, String.format("The command %s(%s) is not applicable for the arguments (%s)", name.getText(), actualType.getRepresentation(), expectedType.getRepresentation())));
            }
        }
        var returnType = processCommandExpectedReturns(info, arguments);
        if (returnType != null) {
            commandSyntax.setType(returnType);
            return TypeCheckAction.CONTINUE;
        } else {
            return TypeCheckAction.SKIP;
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
            if (actual.length > 1 && actual[1] instanceof LiteralTypeSyntax literal) {
                return literal.getValue();
            }
        } else if (info.isParam()) {
            if (actual.length > 1 && actual[1] instanceof DynamicSyntax literal) {
                var symbol = symbolTable.lookupConfig(PrimitiveType.PARAM.INSTANCE, literal.getName().getText());
                if (symbol != null) {
                    if (!symbol.getTransmit() && script.getExtension().equals("cs2")) {
                        checker.reportError(new SemanticError(literal, String.format("The param %s is not set to transmit", literal.getName().getText())));
                    }
                    return symbol.getType();
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
    public TypeCheckAction visit(CalcSyntax calc) {
        calc.setType(PrimitiveType.INT.INSTANCE);
        calc.getExpression().setHintType(PrimitiveType.INT.INSTANCE);
        if (calc.getExpression().accept(this).isContinue()) {
            checkTypeMatching(calc.getExpression(), PrimitiveType.INT.INSTANCE, calc.getExpression().getType());
        }
        return TypeCheckAction.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(BinaryOperationSyntax binaryOperation) {
        var left = binaryOperation.getLeft();
        var right = binaryOperation.getRight();
        if (right.getType() != PrimitiveType.UNDEFINED.INSTANCE) {
            left.setHintType(right.getType());
        } else if (binaryOperation.getHintType() != null) {
            left.setHintType(binaryOperation.getHintType());
        }
        if (left.getType() != PrimitiveType.UNDEFINED.INSTANCE) {
            right.setHintType(left.getType());
        } else if (binaryOperation.getHintType() != null) {
            right.setHintType(binaryOperation.getHintType());
        }
        var check = true;
        check &= left.accept(this).isContinue();
        if (left.getType() != PrimitiveType.UNDEFINED.INSTANCE) {
            right.setHintType(left.getType());
        }
        check &= right.accept(this).isContinue();

        if (check) {
            var type = checkOperator(binaryOperation, binaryOperation.getLeft().getType(), binaryOperation.getRight().getType(), binaryOperation.getOperator());
            binaryOperation.setType(type);
            return TypeCheckAction.CONTINUE;
        }
        return TypeCheckAction.SKIP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(VariableDeclarationSyntax variableDeclaration) {
        var expression = variableDeclaration.getExpression();
        if (expression == null) {
            if (variableDeclaration.getType().getDefaultValue() == null) {
                checker.reportError(new SemanticError(variableDeclaration, "Variables with type '" + variableDeclaration.getType().getRepresentation() + "' must be initialised"));
            }
            return TypeCheckAction.CONTINUE;
        }
        expression.setHintType(variableDeclaration.getType());
        if (expression.accept(this).isContinue()) {
            checkTypeMatching(expression, variableDeclaration.getType(), expression.getType());
            return TypeCheckAction.CONTINUE;
        }
        return TypeCheckAction.SKIP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(ArrayDeclarationSyntax arrayDeclaration) {
        if (arrayDeclaration.getType().getStackType() != StackType.INT) {
            checker.reportError(new SemanticError(arrayDeclaration, "Arrays can only have a type that is derived from the int type"));
        }
        if (arrayDeclaration.getSize().accept(this).isContinue()) {
            checkTypeMatching(arrayDeclaration.getSize(), PrimitiveType.INT.INSTANCE, arrayDeclaration.getSize().getType());
        }
        return TypeCheckAction.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(VariableInitializerSyntax variableInitializer) {
        var hintTypes = new ArrayList<Type>();
        var check = true;
        for (var var : variableInitializer.getVariables()) {
            var result = var.accept(this);
            if (!result.isContinue()) {
                check = false;
            } else {
                if (var.getType() instanceof ArrayReference ref) {
                    hintTypes.add(ref.getType());
                } else {
                    hintTypes.add(var.getType());
                }
            }
        }
        var index = 0;
        for (var expr : variableInitializer.getExpressions()) {
            if (index < hintTypes.size()) {
                expr.setHintType(hintTypes.get(index));
            }
            var result = expr.accept(this);
            if (!result.isContinue()) {
                check = false;
            } else {
                index += TypeUtil.flatten(new Type[]{expr.getType()}).length;
                hintTypes.add(expr.getType());
            }
        }
        if (check) {
            var varTuple = collectType(variableInitializer.getVariables());
            var exprTuple = collectType(variableInitializer.getExpressions());
            if (!exprTuple.equals(varTuple)) {
                checker.reportError(new SemanticError(variableInitializer, String.format("Mismatch variable initializer expected: %s but got: %s", varTuple.getRepresentation(), exprTuple.getRepresentation())));
            }
        }
        return TypeCheckAction.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(ScopedVariableSyntax scopedVariable) {
        return scopedVariable.hasType() ? TypeCheckAction.CONTINUE : TypeCheckAction.SKIP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(ArrayVariableSyntax arrayVariable) {
        if (arrayVariable.getArrayInfo() == null) {
            return TypeCheckAction.SKIP;
        }
        arrayVariable.setType(arrayVariable.getArrayInfo().getType());
        return TypeCheckAction.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(SwitchStatementSyntax switchStatement) {
        switchStatement.setType(PrimitiveType.forRepresentation(switchStatement.getSwitchToken().getLexeme().substring("switch_".length())));
        var type = switchStatement.getType();
        if (switchStatement.getCondition().accept(this).isContinue()) {
            checkTypeMatching(switchStatement.getCondition(), type, switchStatement.getCondition().getType());
        }
        if (switchStatement.getDefaultCase() != null) {
            switchStatement.getDefaultCase().accept(this);
        }
        for (var switchCase : switchStatement.getCases()) {
            for (var key : switchCase.getKeys()) {
                key.setHintType(type);
                if (key.accept(this).isContinue()) {
                    if (isConstantInt(key)) {
                        checkTypeMatching(key, type, key.getType());
                    } else {
                        checker.reportError(new SemanticError(key, "Switch cases value must be known at compile-time"));
                    }
                }
            }
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
    private boolean isConstantInt(ExpressionSyntax expression) {
        if (expression instanceof LiteralIntegerSyntax) {
            return true;
        } else if (expression instanceof LiteralBooleanSyntax) {
            return true;
        } else if (expression instanceof LiteralCoordgridSyntax) {
            return true;
        } else if (expression instanceof LiteralNullSyntax) {
            return true;
        } else if (expression instanceof ConstantSyntax) {
            return ((ConstantSyntax) expression).getValue() instanceof Integer;
        } else if (expression instanceof DynamicSyntax) {
            var configName = ((DynamicSyntax) expression).getName().getText();
            var configInfo = symbolTable.lookupConfig((PrimitiveType<?>) expression.getHintType(), configName);
            return configInfo != null && expression.getHintType().getStackType() == StackType.INT;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(SwitchCaseSyntax switchCase) {
        return switchCase.getCode().accept(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(IfStatementSyntax ifStatement) {
        var cond = ifStatement.getCondition();
        if (cond.accept(this).isContinue()) {
            checkTypeMatching(ifStatement.getCondition(), PrimitiveType.BOOLEAN.INSTANCE, cond.getType());
        }
        ifStatement.getTrueStatement().accept(this);
        if (ifStatement.getFalseStatement() != null) {
            ifStatement.getFalseStatement().accept(this);
        }
        return TypeCheckAction.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(WhileStatementSyntax whileStatement) {
        whileStatement.getCode().accept(this);
        var cond = whileStatement.getCondition();
        if (cond.accept(this).isContinue()) {
            checkTypeMatching(whileStatement.getCondition(), PrimitiveType.BOOLEAN.INSTANCE, cond.getType());
        }
        return TypeCheckAction.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(DoWhileStatementSyntax doWhileStatementSyntax) {
        doWhileStatementSyntax.getCode().accept(this);
        var cond = doWhileStatementSyntax.getCondition();
        if (cond.accept(this).isContinue()) {
            checkTypeMatching(doWhileStatementSyntax.getCondition(), PrimitiveType.BOOLEAN.INSTANCE, cond.getType());
        }
        return TypeCheckAction.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(ContinueStatementSyntax continueStatementSyntax) {
        var whileStatementSyntax = continueStatementSyntax.selectParent(syntax -> syntax instanceof WhileStatementSyntax);
        if (whileStatementSyntax == null) {
            checker.reportError(new SemanticError(continueStatementSyntax, "Continue statement is not allowed outside of a loop"));
        }
        return TypeCheckAction.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(BreakStatementSyntax breakStatementSyntax) {
        var whileStatementSyntax = breakStatementSyntax.selectParent(syntax -> syntax instanceof WhileStatementSyntax);
        if (whileStatementSyntax == null) {
            checker.reportError(new SemanticError(breakStatementSyntax, "Break statement is not allowed outside of a loop"));
        }
        return TypeCheckAction.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(ExpressionStatementSyntax expressionStatement) {
        expressionStatement.getExpression().accept(this);
        return TypeCheckAction.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(ReturnStatementSyntax returnStatement) {
        var expectedHintTypes = TypeUtil.flatten(new Type[]{script.getType()});
        var index = 0;
        var expressions = returnStatement.getExpressions();
        var check = true;
        for (var expr : expressions) {
            if (index < expectedHintTypes.length) {
                expr.setHintType(expectedHintTypes[index]);
            }
            check &= expr.accept(this).isContinue();
            index += TypeUtil.flatten(new Type[]{expr.getType()}).length;
        }
        if (check) {
            var type = collectType(returnStatement.getExpressions());
            checkTypeMatching(returnStatement, script.getType(), type);
        }
        return TypeCheckAction.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeCheckAction visit(BlockStatementSyntax blockStatement) {
        for (var statement : blockStatement.getStatements()) {
            statement.accept(this);
        }
        return TypeCheckAction.CONTINUE;
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
            if (left == PrimitiveType.INT.INSTANCE || left == PrimitiveType.LONG.INSTANCE) {
                applicable = left.equals(right);
            }
        } else if (operator.isLogical()) {
            applicable = left == PrimitiveType.BOOLEAN.INSTANCE && right == PrimitiveType.BOOLEAN.INSTANCE;
        } else if (operator.isArithmetic()) {
            if (node.selectParent(parent -> parent instanceof CalcSyntax) == null) {
                checker.reportError(new SemanticError(node, "Arithmetic expressions are only allowed within a 'calc' expression"));
            }
            applicable = left == PrimitiveType.INT.INSTANCE && right == PrimitiveType.INT.INSTANCE;
        }
        if (!applicable) {
            checker.reportError(new SemanticError(node, "The operator '" + operator.getRepresentation() + "' is undefined for the argument type(s) " + left.getRepresentation() + ", " + right.getRepresentation()));
        }
        return operator.isArithmetic() ? PrimitiveType.INT.INSTANCE : PrimitiveType.BOOLEAN.INSTANCE;
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
        if (first == PrimitiveType.NULL.INSTANCE || second == PrimitiveType.NULL.INSTANCE) {
            Type other = first == PrimitiveType.NULL.INSTANCE ? second : first;
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
            return TupleType.EMPTY;
        } else if (nodes.length == 1) {
            return nodes[0].getType();
        }
        return new TupleType(Arrays.stream(nodes).map(Syntax::getType).toArray(Type[]::new));
    }
}
/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen;

import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.compiler.codegen.block.Block;
import me.waliedyassen.runescript.compiler.codegen.block.BlockMap;
import me.waliedyassen.runescript.compiler.codegen.block.Label;
import me.waliedyassen.runescript.compiler.codegen.context.Context;
import me.waliedyassen.runescript.compiler.codegen.context.ContextType;
import me.waliedyassen.runescript.compiler.codegen.local.Local;
import me.waliedyassen.runescript.compiler.codegen.local.LocalMap;
import me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode;
import me.waliedyassen.runescript.compiler.codegen.opcode.Opcode;
import me.waliedyassen.runescript.compiler.codegen.script.BinaryScript;
import me.waliedyassen.runescript.compiler.codegen.sw.SwitchCase;
import me.waliedyassen.runescript.compiler.codegen.sw.SwitchMap;
import me.waliedyassen.runescript.compiler.codegen.sw.SwitchTable;
import me.waliedyassen.runescript.compiler.env.CompilerEnvironment;
import me.waliedyassen.runescript.compiler.symbol.ScriptSymbolTable;
import me.waliedyassen.runescript.compiler.symbol.impl.CommandInfo;
import me.waliedyassen.runescript.compiler.syntax.ParameterSyntax;
import me.waliedyassen.runescript.compiler.syntax.ScriptSyntax;
import me.waliedyassen.runescript.compiler.syntax.expr.*;
import me.waliedyassen.runescript.compiler.syntax.expr.literal.*;
import me.waliedyassen.runescript.compiler.syntax.expr.op.BinaryOperationSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.*;
import me.waliedyassen.runescript.compiler.syntax.stmt.conditional.IfStatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.loop.BreakStatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.loop.ContinueStatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.loop.WhileStatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;
import me.waliedyassen.runescript.compiler.type.ArrayReference;
import me.waliedyassen.runescript.compiler.util.VariableScope;
import me.waliedyassen.runescript.compiler.util.trigger.TriggerType;
import me.waliedyassen.runescript.type.PrimitiveType;
import me.waliedyassen.runescript.type.StackType;
import me.waliedyassen.runescript.type.TupleType;
import me.waliedyassen.runescript.type.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Stack;

import static me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode.*;

/**
 * Represents the compiler bytecode generator.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class CodeGenerator implements SyntaxVisitor<Object> {

    /**
     * The start label of a while statement attribute name.
     */
    private static final String ATTR_START_LABEL = "cg_start_label";

    /**
     * The code label of a while statement attribute name.
     */
    private static final String ATTR_CODE_LABEL = "cg_code_label";

    /**
     * The end label of a while statement attribute name.
     */
    private static final String ATTR_END_LABEL = "cg_end_label";

    /**
     * The label generator used to generate any label for this code generator.
     */
    private final LabelGenerator labelGenerator = new LabelGenerator();

    /**
     * The blocks map of the current script.
     */
    private final BlockMap blockMap = new BlockMap();

    /**
     * The locals map of the current script.
     */
    private final LocalMap localMap = new LocalMap();

    /**
     * The switch tables map of the current script.
     */
    private final SwitchMap switchMap = new SwitchMap();

    /**
     * The current block we are working on.
     */
    private final Stack<Context> contexts = new Stack<>();

    /**
     * The environment we are going to use to lookup triggers.
     */
    private final CompilerEnvironment environment;

    /**
     * The symbol table which has all the information for the current generation.
     */
    private final ScriptSymbolTable symbolTable;

    /**
     * The instructions map which contains the primary instruction opcodes.
     */
    private final InstructionMap instructionMap;

    /**
     * The trigger type which the hooks will try to look up using.
     */
    private final TriggerType hookTriggerType;

    /**
     * Initialises the code generator and reset its state.
     */
    public void initialise() {
        labelGenerator.reset();
        blockMap.reset();
        localMap.reset();
        switchMap.reset();
        contexts.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BinaryScript visit(ScriptSyntax script) {
        // perform code generation on the script.
        pushContext(ContextType.SCRIPT);
        for (var parameter : script.getParameters()) {
            parameter.accept(this);
        }
        bind(generateBlock("entry"));
        script.getCode().accept(this);
        generateDefaultReturn(script.getType());
        popContext();
        // format the script name to be in the formal format.
        var name = "[" + script.getTrigger().getText() + "," + ExpressionSyntax.extractNameText(script.getName()) + "]";
        // put all of the blocks into a sorted map.
        var blocks = new LinkedHashMap<Label, Block>();
        for (var block : blockMap.getBlocks()) {
            blocks.put(block.getLabel(), block);
        }
        // clone the local variables and parameter maps.
        var parameters = new HashMap<>(localMap.getParameters());
        var variables = new HashMap<>(localMap.getVariables());
        // create the switch tables list.
        var tables = new ArrayList<SwitchTable>(switchMap.getTables().size());
        for (int id = 0; id < switchMap.getTables().size(); id++) {
            tables.add(switchMap.getTables().get(id));
        }
        // clean-up the junk after code generation is done.
        initialise();
        // return the generated script object.
        var info = symbolTable.lookupScript(environment.lookupTrigger(script.getTrigger().getText()), ExpressionSyntax.extractNameText(script.getName()));
        return new BinaryScript(script.getExtension(), name, blocks, parameters, variables, tables, info);
    }

    /**
     * Generates the default return instruction of the specified return {@link Type type}.
     *
     * @param returnType the return type of the script.
     */
    private void generateDefaultReturn(Type returnType) {
        if (returnType != PrimitiveType.VOID) {
            if (returnType instanceof TupleType) {
                var flattened = ((TupleType) returnType).getFlattened();
                for (var type : flattened) {
                    instruction(getConstantOpcode(type), type.getDefaultValue());
                }
            } else {
                instruction(getConstantOpcode(returnType), returnType.getDefaultValue());
            }
        }
        instruction(RETURN, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Local visit(ParameterSyntax parameter) {
        return localMap.registerParameter(parameter.getName().getText(), parameter.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(LiteralBooleanSyntax bool) {
        return instruction(CoreOpcode.PUSH_INT_CONSTANT, bool.getValue() ? 1 : 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(LiteralIntegerSyntax integer) {
        return instruction(CoreOpcode.PUSH_INT_CONSTANT, integer.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(LiteralLongSyntax longInteger) {
        return instruction(CoreOpcode.PUSH_LONG_CONSTANT, longInteger.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(LiteralStringSyntax string) {
        if (string.getType() == PrimitiveType.GRAPHIC) {
            return instruction(PUSH_INT_CONSTANT, symbolTable.lookupGraphic(string.getValue()).getId());
        } else {
            return instruction(PUSH_STRING_CONSTANT, string.getValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(LiteralCoordgridSyntax coordgrid) {
        return instruction(PUSH_INT_CONSTANT, coordgrid.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(LiteralNullSyntax literalNullSyntax) {
        return instruction(PUSH_INT_CONSTANT, -1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(ConcatenationSyntax concatenation) {
        for (var expression : concatenation.getExpressions()) {
            expression.accept(this);
        }
        return instruction(CoreOpcode.JOIN_STRING, concatenation.getExpressions().length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(ComponentSyntax component) {
        var interfaceInfo = symbolTable.lookupInterface(component.getParentInterface().getText());
        var parentId = interfaceInfo.getId();
        var componentId = component.getComponent() instanceof LiteralIntegerSyntax ? ((LiteralIntegerSyntax) component.getComponent())
                .getValue() : interfaceInfo.lookupComponent(String.valueOf(component.getComponentName()));
        return instruction(PUSH_INT_CONSTANT, parentId << 16 | componentId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(VariableExpressionSyntax variableExpression) {
        var name = variableExpression.getName().getText();
        if (variableExpression.getScope() == VariableScope.LOCAL) {
            var local = localMap.lookup(name);
            return instruction(getPushVariableOpcode(variableExpression.getScope(), (PrimitiveType) local.getType()), local);
        } else {
            var config = symbolTable.lookupConfig(name);
            return instruction(getPushVariableOpcode(variableExpression.getScope(), (PrimitiveType) config.getType()), config);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(ArrayElementSyntax arrayExpression) {
        arrayExpression.getIndex().accept(this);
        return instruction(PUSH_ARRAY_INT, arrayExpression.getArray().getIndex());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(HookSyntax hook) {
        var signature = new StringBuilder();
        if (hook.getArguments() != null) {
            for (var index = 0; index < hook.getArguments().length; index++) {
                signature.append(hook.getArguments()[index].getType().getCode());
            }
        }
        if (hook.getTransmits() != null) {
            signature.append('Y');
        }
        instruction(PUSH_INT_CONSTANT, hook.getName() == null ? -1 : symbolTable.lookupScript(hookTriggerType, hook.getName().getText()));
        if (hook.getArguments() != null) {
            for (var argument : hook.getArguments()) {
                argument.accept(this);
            }
        }
        if (hook.getTransmits() != null) {
            for (var transmit : hook.getTransmits()) {
                transmit.accept(this);
            }
            instruction(PUSH_INT_CONSTANT, hook.getTransmits().length);
        }
        return instruction(PUSH_STRING_CONSTANT, signature.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(CallSyntax call) {
        for (var argument : call.getArguments()) {
            argument.accept(this);
        }
        var script = symbolTable.lookupScript(call.getTriggerType(), call.getName().getText());
        return instruction(call.getTriggerType().getOpcode(), script);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("RedundantCast")
    public Instruction visit(DynamicSyntax dynamic) {
        if (dynamic.getType() instanceof ArrayReference) {
            return instruction(PUSH_INT_CONSTANT, ((ArrayReference) dynamic.getType()).getIndex());
        } else {
            var name = dynamic.getName().getText();
            var commandInfo = symbolTable.lookupCommand(name);
            if (commandInfo != null) {
                return generateCommand(commandInfo, false);
            }
            var configInfo = symbolTable.lookupConfig(name);
            if (configInfo != null) {
                return instruction(PUSH_INT_CONSTANT, configInfo);
            }
            var runtimeConstantInfo = symbolTable.lookupRuntimeConstant(name);
            if (runtimeConstantInfo != null) {
                switch (dynamic.getType().getStackType()) {
                    case INT:
                        return instruction(PUSH_INT_CONSTANT, ((Integer) runtimeConstantInfo.getValue()));
                    case STRING:
                        return instruction(PUSH_STRING_CONSTANT, ((String) runtimeConstantInfo.getValue()));
                    case LONG:
                        return instruction(PUSH_LONG_CONSTANT, ((Long) runtimeConstantInfo.getValue()));
                }
            }
            throw new UnsupportedOperationException(name);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Instruction visit(ConstantSyntax constant) {
        var symbol = symbolTable.lookupConstant(constant.getName().getText());
        CoreOpcode opcode;
        switch (symbol.getType().getStackType()) {
            case INT:
                opcode = CoreOpcode.PUSH_INT_CONSTANT;
                break;
            case STRING:
                opcode = CoreOpcode.PUSH_STRING_CONSTANT;
                break;
            case LONG:
                opcode = CoreOpcode.PUSH_LONG_CONSTANT;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported constant base stack type: " + symbol.getType().getStackType());
        }
        return instruction(opcode, symbol.getValue());
    }

    /**
     * {@inheritDoc}
     */
    public Instruction visit(CommandSyntax command) {
        for (var argument : command.getArguments()) {
            argument.accept(this);
        }
        return generateCommand(symbolTable.lookupCommand(command.getName().getText()), command.isAlternative());
    }

    /**
     * {@inheritDoc}
     */
    public Instruction visit(CalcSyntax command) {
        command.getExpression().accept(this);
        return null;
    }

    /**
     * Generates the instruction(s) set for the specified {@link CommandInfo command}.
     *
     * @param info        the command info to generate the instruction(s) set for.
     * @param alternative whether or not the command is alternative command.
     * @return the last generated {@link Instruction} object.
     */
    private Instruction generateCommand(CommandInfo info, boolean alternative) {
        return instruction(info.getOpcode(), alternative ? 1 : 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(BinaryOperationSyntax binaryOperation) {
        if (!binaryOperation.getOperator().isArithmetic()) {
            throw new UnsupportedOperationException("You should not be doing this.");
        }
        CoreOpcode opcode;
        switch (binaryOperation.getOperator()) {
            case ADD:
                opcode = ADD;
                break;
            case SUB:
                opcode = SUB;
                break;
            case MUL:
                opcode = MUL;
                break;
            case DIV:
                opcode = DIV;
                break;
            case MOD:
                opcode = MOD;
                break;
            default:
                throw new UnsupportedOperationException("Cannot generate code for operator: " + binaryOperation.getOperator());
        }
        binaryOperation.getLeft().accept(this);
        binaryOperation.getRight().accept(this);
        return instruction(opcode, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(VariableDeclarationSyntax variableDeclaration) {
        if (variableDeclaration.getExpression() != null) {
            variableDeclaration.getExpression().accept(this);
        } else {
            var opcode = getConstantOpcode(variableDeclaration.getType());
            instruction(opcode, variableDeclaration.getType().getDefaultValue());
        }
        var local = localMap.registerVariable(variableDeclaration.getName().getText(), variableDeclaration.getType());
        return instruction(getPopVariableOpcode(VariableScope.LOCAL, (PrimitiveType) variableDeclaration.getType()), local);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(ArrayDeclarationSyntax arrayDeclaration) {
        arrayDeclaration.getSize().accept(this);
        var array = arrayDeclaration.getArray();
        return instruction(DEFINE_ARRAY, (array.getIndex() << 16) | array.getType().getCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(VariableInitializerSyntax variableInitializer) {
        var count = variableInitializer.getExpressions().length;
        for (var index = 0; index < count; index++) {
            variableInitializer.getVariables()[index].accept(this);
            variableInitializer.getExpressions()[index].accept(this);
        }
        for (var index = count - 1; index >= 0; index--) {
            var variable = variableInitializer.getVariables()[index];
            if (variable instanceof ArrayVariableSyntax) {
                var arrayVariable = (ArrayVariableSyntax) variable;
                instruction(POP_ARRAY_INT, arrayVariable.getArrayInfo().getIndex());
            } else {
                var scopedVariable = (ScopedVariableSyntax) variable;
                Object operand;
                PrimitiveType type;
                if (scopedVariable.getScope() == VariableScope.LOCAL) {
                    operand = localMap.lookup(scopedVariable.getName().getText());
                    type = (PrimitiveType) scopedVariable.getType();
                } else {
                    var configInfo = symbolTable.lookupVariable(scopedVariable.getName().getText());
                    operand = configInfo;
                    type = (PrimitiveType) configInfo.getType();
                }
                instruction(getPopVariableOpcode(scopedVariable.getScope(), type), operand);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(ArrayVariableSyntax arrayVariable) {
        arrayVariable.getIndex().accept(this);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(ScopedVariableSyntax scopedVariable) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(SwitchStatementSyntax switchStatement) {
        // grab the switch case nodes.
        var case_nodes = switchStatement.getCases();
        // create the switch cases.
        var cases = new SwitchCase[case_nodes.length];
        // generate a new switch table from the switch map.
        var switch_table = switchMap.generateTable(cases);
        // generate the switch condition code.
        switchStatement.getCondition().accept(this);
        // create the exit block label.
        var exit_label = generateLabel("switch_" + switch_table.getId() + "_exit");
        // generate the switch table instruction.
        instruction(SWITCH, switch_table);
        // generate the switch default case if it was present.
        if (switchStatement.getDefaultCase() != null) {
            switchStatement.getDefaultCase().getCode().accept(this);
        }
        // add a branch to the exit label at the end of the switch.
        instruction(BRANCH, exit_label);
        // loop through each switch case and perform code generation on it.
        for (var index = 0; index < case_nodes.length; index++) {
            var case_node = case_nodes[index];
            var case_entry = cases[index] = new SwitchCase(case_node.getResolvedKeys(), generateLabel("switch_" + switch_table.getId() + "_case"));
            // perform the code generation on the case.
            bind(generateBlock(case_entry.getLabel()));
            case_node.getCode().accept(this);
            // add a branch to the exit label.
            instruction(BRANCH, exit_label);
        }
        // generate a block for the exit label.
        bind(generateBlock(exit_label));
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(IfStatementSyntax ifStatement) {
        // preserve the labels of this if statement for number order.
        var if_true_label = labelGenerator.generate("if_true");
        var if_else_label = labelGenerator.generate("if_else");
        var if_end_label = labelGenerator.generate("if_end");
        // store whether we have an else statement or not.
        var has_else = ifStatement.getFalseStatement() != null;
        // grab the parent block of the if statement.
        var source_block = context().getBlock();
        // generate the condition of the if statement.
        generateCondition(ifStatement.getCondition(), source_block, if_true_label, has_else ? if_else_label : if_end_label);
        // generate the if-true block of the statement
        bind(generateBlock(if_true_label));
        ifStatement.getTrueStatement().accept(this);
        // generate the branch instructions for the if-true block.
        instruction(BRANCH, if_end_label);
        // generate the if-else statement block and code.
        var else_block = has_else ? generateBlock(if_else_label) : null;
        if (has_else) {
            bind(else_block);
            ifStatement.getFalseStatement().accept(this);
            instruction(BRANCH, if_end_label);
        }
        // generate the if-end block and bind it.
        bind(generateBlock(if_end_label));
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(WhileStatementSyntax whileStatement) {
        // preserve the labels of this while statement for the number order.
        var while_start_label = whileStatement.putAttribute(ATTR_START_LABEL, labelGenerator.generate("while_start"));
        var while_true_label = whileStatement.putAttribute(ATTR_CODE_LABEL, labelGenerator.generate("while_true"));
        var while_end_label = whileStatement.putAttribute(ATTR_END_LABEL, labelGenerator.generate("while_end"));
        // add a branch to the start block of the while.
        instruction(BRANCH, while_start_label);
        // generate the start block of the while statement.
        var start_block = bind(generateBlock(while_start_label));
        // generate the while statement condition.
        generateCondition(whileStatement.getCondition(), start_block, while_true_label, while_end_label);
        // generate the while statement code.
        bind(generateBlock(while_true_label));
        whileStatement.getCode().accept(this);
        // generate the while statement jump to start instruction.
        instruction(BRANCH, while_start_label);
        // generate the while end label.
        bind(generateBlock(while_end_label));
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(ContinueStatementSyntax continueStatementSyntax) {
        var whileStatementSyntax = continueStatementSyntax.selectParent(syntax -> syntax instanceof WhileStatementSyntax);
        return instruction(BRANCH, whileStatementSyntax.getAttribute(ATTR_START_LABEL));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(BreakStatementSyntax breakStatementSyntax) {
        var whileStatementSyntax = breakStatementSyntax.selectParent(syntax -> syntax instanceof WhileStatementSyntax);
        return instruction(BRANCH, whileStatementSyntax.getAttribute(ATTR_END_LABEL));
    }

    /**
     * Performs code generation on the specified {@code condition} expression and returns it's associated {@link
     * CoreOpcode opcode}.
     *
     * @param condition    the condition expression to perform the code generation on.
     * @param source_block the source block of the condition.
     * @param branch_true  the if-true block label.
     * @param branch_false the if-false block label.
     */
    private void generateCondition(ExpressionSyntax condition, Block source_block, Label branch_true, Label branch_false) {
        if (condition instanceof BinaryOperationSyntax) {
            var binaryOperation = (BinaryOperationSyntax) condition;
            var operator = binaryOperation.getOperator();
            if (operator.isEquality() || operator.isRelational()) {
                CoreOpcode opcode;
                switch (operator) {
                    case EQUAL:
                        opcode = CoreOpcode.BRANCH_EQUALS;
                        break;
                    case NOT_EQUAL:
                        opcode = CoreOpcode.BRANCH_NOT;
                        break;
                    case LESS_THAN:
                        opcode = CoreOpcode.BRANCH_LESS_THAN;
                        break;
                    case GREATER_THAN:
                        opcode = CoreOpcode.BRANCH_GREATER_THAN;
                        break;
                    case LESS_THAN_OR_EQUALS:
                        opcode = CoreOpcode.BRANCH_LESS_THAN_OR_EQUALS;
                        break;
                    case GREATER_THAN_OR_EQUALS:
                        opcode = CoreOpcode.BRANCH_GREATER_THAN_OR_EQUALS;
                        break;
                    default:
                        throw new UnsupportedOperationException("Unexpected operator: " + operator);
                }
                binaryOperation.getLeft().accept(this);
                binaryOperation.getRight().accept(this);
                instruction(source_block, opcode, branch_true);
                if (branch_false != null) {
                    instruction(source_block, BRANCH, branch_false);
                }
            } else if (operator.isLogical()) {
                switch (operator) {
                    case LOGICAL_OR:
                        generateCondition(binaryOperation.getLeft(), source_block, branch_true, null);
                        bind(source_block);
                        generateCondition(binaryOperation.getRight(), source_block, branch_true, null);
                        if (branch_false != null) {
                            instruction(source_block, BRANCH, branch_false);
                        }
                        break;
                    case LOGICAL_AND:
                        var if_and_label = labelGenerator.generate("if_and");
                        generateCondition(binaryOperation.getLeft(), source_block, if_and_label, branch_false);
                        var if_and_block = bind(generateBlock(if_and_label));
                        generateCondition(binaryOperation.getRight(), if_and_block, branch_true, branch_false);
                        break;
                    default:
                        throw new UnsupportedOperationException("Unexpected operator: " + operator);
                }
            } else {
                throw new UnsupportedOperationException("Unexpected operator: " + operator);
            }
        } else {
            condition.accept(this);
            instruction(source_block, BRANCH_IF_TRUE, branch_true);
            if (branch_false != null) {
                instruction(source_block, BRANCH, branch_false);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(ExpressionStatementSyntax expressionStatement) {
        var expression = expressionStatement.getExpression();
        expression.accept(this);
        var pushes = resolvePushCount(expression.getType());
        generateDiscard(pushes[0], pushes[1], pushes[2]);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(ReturnStatementSyntax returnStatement) {
        for (var expression : returnStatement.getExpressions()) {
            expression.accept(this);
        }
        instruction(CoreOpcode.RETURN, 0);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(BlockStatementSyntax blockStatement) {
        for (var statement : blockStatement.getStatements()) {
            statement.accept(this);
        }
        return null;
    }

    /**
     * Generate a specific amount of discard instructions for each of the stack types.
     *
     * @param numInts    the amount of integer discard instructions.
     * @param numStrings the amount of string discard instructions.
     * @param numLongs   the amount of long discard instructions.
     */
    private void generateDiscard(int numInts, int numStrings, int numLongs) {
        if (numInts > 0) {
            for (int index = 0; index < numInts; index++) {
                instruction(CoreOpcode.POP_INT_DISCARD, 0);
            }
        }
        if (numStrings > 0) {
            for (int index = 0; index < numStrings; index++) {
                instruction(CoreOpcode.POP_STRING_DISCARD, 0);
            }
        }
        if (numLongs > 0) {
            for (int index = 0; index < numLongs; index++) {
                instruction(CoreOpcode.POP_LONG_DISCARD, 0);
            }
        }
    }

    /**
     * Resolves how many pushes of each stack type does the specified element {@link Type type} do.
     *
     * @param type the type to resolve for.
     * @return the amount of pushes in one array in a specific order (int, string, long).
     */
    private int[] resolvePushCount(Type type) {
        var numInts = 0;
        var numStrings = 0;
        var numLongs = 0;
        if (type instanceof TupleType) {
            var flatten = ((TupleType) type).getFlattened();
            for (var elementType : flatten) {
                var stackType = elementType.getStackType();
                if (stackType == StackType.INT) {
                    numInts++;
                } else if (stackType == StackType.STRING) {
                    numStrings++;
                } else if (stackType == StackType.LONG) {
                    numLongs++;
                }
            }
        } else {
            var stackType = type.getStackType();
            if (stackType == StackType.INT) {
                numInts++;
            } else if (stackType == StackType.STRING) {
                numStrings++;
            } else if (stackType == StackType.LONG) {
                numLongs++;
            }
        }
        return new int[]{numInts, numStrings, numLongs};
    }

    /**
     * Creates a new {@link Instruction instruction} with the specified {@link CoreOpcode opcode} and the specified
     * {@code operand}. The {@link CoreOpcode opcode} will be remapped to a suitable regular {@link Opcode} instance
     * then passed to {@link #makeInstruction(Opcode, Object)} and then it gets added to the current active block in the
     * {@link #blockMap block map}.
     *
     * @param opcode  the opcode of the instruction.
     * @param operand the operand of the instruction.
     * @return the created {@link Instruction} object.
     */
    private Instruction instruction(CoreOpcode opcode, Object operand) {
        return instruction(context().getBlock(), opcode, operand);
    }

    /**
     * Creates a new {@link Instruction instruction} with the specified {@link CoreOpcode opcode} and the specified
     * {@code operand}. The {@link CoreOpcode opcode} will be remapped to a suitable regular {@link Opcode} instance
     * then passed to {@link #makeInstruction(Opcode, Object)} and then it gets added to the current active block in the
     * {@link #blockMap block map}.
     *
     * @param block   the block to add the instruction to.
     * @param opcode  the opcode of the instruction.
     * @param operand the operand of the instruction.
     * @return the created {@link Instruction} object.
     */
    private Instruction instruction(Block block, CoreOpcode opcode, Object operand) {
        return instruction(block, instructionMap.lookup(opcode), operand);
    }

    /**
     * Creates a new {@link Instruction instruction} using {@link #makeInstruction(Opcode, Object)} and then adds it as
     * a child instruction to the current active block in the {@link #blockMap block map}.
     *
     * @param opcode  the opcode of the instruction.
     * @param operand the operand of the instruction.
     * @return the created {@link Instruction} object.
     */
    private Instruction instruction(Opcode opcode, Object operand) {
        return instruction(context().getBlock(), opcode, operand);
    }

    /**
     * Creates a new {@link Instruction instruction} using {@link #makeInstruction(Opcode, Object)} and then adds it as
     * a child instruction to the current active block in the {@link #blockMap block map}.
     *
     * @param block   the block to add the instruction to.
     * @param opcode  the opcode of the instruction.
     * @param operand the operand of the instruction.
     * @return the created {@link Instruction} object.
     */
    private Instruction instruction(Block block, Opcode opcode, Object operand) {
        var instruction = makeInstruction(opcode, operand);
        block.add(instruction);
        return instruction;
    }

    /**
     * Creates a new {@link Instruction} object without linking it to any block.
     *
     * @param opcode  the opcode of the instruction.
     * @param operand the operand of the instruction.
     * @return the created {@link Instruction} object.
     */
    private Instruction makeInstruction(Opcode opcode, Object operand) {
        return new Instruction(opcode, operand);
    }

    /**
     * Binds the specified {@link Block block} as the current working block.
     *
     * @param block the block to bind as the working block.
     * @return the block that was passed to the method.
     */
    private Block bind(Block block) {
        context().setBlock(block);
        return block;
    }

    /**
     * Generates a new {@link Block} object.
     *
     * @param name the name of the block label.
     * @return the generated {@link Block} object.
     * @see BlockMap#generate(Label)
     */
    private Block generateBlock(String name) {
        return generateBlock(generateLabel(name));
    }

    /**
     * Generates a new {@link Block} object.
     *
     * @param label the label of the block.
     * @return the generated {@link Block} object.
     * @see BlockMap#generate(Label)
     */
    private Block generateBlock(Label label) {
        return blockMap.generate(label);
    }

    /**
     * Generates a new unique {@link Label} object.
     *
     * @param name the name of the label.
     * @return the generated {@link Label} object.
     * @see LabelGenerator#generate(String)
     */
    private Label generateLabel(String name) {
        return labelGenerator.generate(name);
    }

    /**
     * Gets the current active {@link Context} object.
     *
     * @return the active {@link Context} object.
     */
    public Context context() {
        return contexts.lastElement();
    }

    /**
     * Creates a new {@link Context} object and pushes it into the stack.
     *
     * @param type the type of the context.
     * @return the created {@link Context} object.
     */
    public Context pushContext(ContextType type) {
        var context = new Context(type);
        contexts.push(context);
        return context;
    }

    /**
     * Pops the last context from the stack.
     *
     * @return the popped {@link Context} object.
     */
    public Context popContext() {
        return contexts.pop();
    }


    /**
     * Returns the push core opcode for a variable with the specified {@link VariableScope scope} and {@link PrimitiveType type}.
     *
     * @param scope the scope of the variable we want the opcode for.
     * @param type  the type of the variable we want the opcode for,
     * @return the push {@link CoreOpcode opcode} enum constant.
     */
    private static CoreOpcode getPushVariableOpcode(VariableScope scope, PrimitiveType type) {
        switch (scope) {
            case LOCAL:
                switch (type.getStackType()) {
                    case INT:
                        return CoreOpcode.PUSH_INT_LOCAL;
                    case STRING:
                        return CoreOpcode.PUSH_STRING_LOCAL;
                    case LONG:
                        return CoreOpcode.PUSH_LONG_LOCAL;
                    default:
                        throw new UnsupportedOperationException("Unsupported local variable stack type: " + type.getStackType());

                }
            case GLOBAL:
                switch (type) {
                    case VAR:
                        return CoreOpcode.PUSH_VARP;
                    case VARBIT:
                        return CoreOpcode.PUSH_VARP_BIT;
                    case VARCINT:
                        return CoreOpcode.PUSH_VARC_INT;
                    case VARCSTR:
                        return CoreOpcode.PUSH_VARC_STRING;
                    default:
                        throw new UnsupportedOperationException("Unsupported variable domain type: " + type);
                }
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     * Returns the pop core opcode for a variable with the specified {@link VariableScope scope} and {@link PrimitiveType type}.
     *
     * @param scope the scope of the variable we want the opcode for.
     * @param type  the type of the variable we want the opcode for,
     * @return the pop {@link CoreOpcode opcode} enum constant.
     */
    private static CoreOpcode getPopVariableOpcode(VariableScope scope, PrimitiveType type) {
        switch (scope) {
            case LOCAL:
                switch (type.getStackType()) {
                    case INT:
                        return CoreOpcode.POP_INT_LOCAL;
                    case STRING:
                        return CoreOpcode.POP_STRING_LOCAL;
                    case LONG:
                        return CoreOpcode.POP_LONG_LOCAL;
                    default:
                        throw new UnsupportedOperationException("Unsupported local variable stack type: " + type.getStackType());

                }
            case GLOBAL:
                switch (type) {
                    case VAR:
                        return CoreOpcode.POP_VARP;
                    case VARBIT:
                        return CoreOpcode.POP_VARP_BIT;
                    case VARCINT:
                        return CoreOpcode.POP_VARC_INT;
                    case VARCSTR:
                        return CoreOpcode.POP_VARC_STRING;
                    default:
                        throw new UnsupportedOperationException("Unsupported variable domain type: " + type);
                }
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     * Gets the instruction {@link CoreOpcode} of the specified constant {@link Type}.
     *
     * @param type the type of the constant.
     * @return the instruction {@link CoreOpcode opcode} of that constant type.
     */
    private static CoreOpcode getConstantOpcode(Type type) {
        switch (type.getStackType()) {
            case INT:
                return CoreOpcode.PUSH_INT_CONSTANT;
            case STRING:
                return CoreOpcode.PUSH_STRING_CONSTANT;
            case LONG:
                return CoreOpcode.PUSH_LONG_CONSTANT;
            default:
                throw new UnsupportedOperationException("Unsupported stack type: " + type.getStackType());
        }
    }
}

/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen;

import lombok.RequiredArgsConstructor;
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
import me.waliedyassen.runescript.compiler.codegen.block.Block;
import me.waliedyassen.runescript.compiler.codegen.block.BlockMap;
import me.waliedyassen.runescript.compiler.codegen.block.Label;
import me.waliedyassen.runescript.compiler.codegen.context.Context;
import me.waliedyassen.runescript.compiler.codegen.context.ContextType;
import me.waliedyassen.runescript.compiler.codegen.local.Local;
import me.waliedyassen.runescript.compiler.codegen.local.LocalMap;
import me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode;
import me.waliedyassen.runescript.compiler.codegen.opcode.Opcode;
import me.waliedyassen.runescript.compiler.codegen.script.Script;
import me.waliedyassen.runescript.compiler.codegen.sw.SwitchCase;
import me.waliedyassen.runescript.compiler.codegen.sw.SwitchMap;
import me.waliedyassen.runescript.compiler.codegen.sw.SwitchTable;
import me.waliedyassen.runescript.compiler.symbol.SymbolTable;
import me.waliedyassen.runescript.compiler.symbol.impl.CommandInfo;
import me.waliedyassen.runescript.compiler.symbol.impl.variable.VariableDomain;
import me.waliedyassen.runescript.compiler.type.ArrayReference;
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
public final class CodeGenerator implements AstVisitor<Instruction, Object> {

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
     * The symbol table which has all the information for the current generation.
     */
    private final SymbolTable symbolTable;

    /**
     * The instructions map which contains the primary instruction opcodes.
     */
    private final InstructionMap instructionMap;

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
    public Script visit(AstScript script) {
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
        var name = "[" + script.getTrigger().getText() + "," + script.getName().getText() + "]";
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
        return new Script(name, blocks, parameters, variables, tables);
    }

    /**
     * Generates the default return instruction of the specified return {@link Type type}.
     *
     * @param returnType
     *         the return type of the script.
     */
    private void generateDefaultReturn(Type returnType) {
        if (returnType == PrimitiveType.VOID) {
            // NOOP
        } else if (returnType instanceof TupleType) {
            var flattened = ((TupleType) returnType).getFlattened();
            for (var type : flattened) {
                instruction(getConstantOpcode(type), type.getDefaultValue());
            }
        } else {
            instruction(getConstantOpcode(returnType), returnType.getDefaultValue());
        }
        instruction(RETURN, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Local visit(AstParameter parameter) {
        return localMap.registerParameter(parameter.getName().getText(), parameter.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(AstLiteralBool bool) {
        return instruction(CoreOpcode.PUSH_INT_CONSTANT, bool.getValue() ? 1 : 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(AstLiteralInteger integer) {
        return instruction(CoreOpcode.PUSH_INT_CONSTANT, integer.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(AstLiteralLong longInteger) {
        return instruction(CoreOpcode.PUSH_LONG_CONSTANT, longInteger.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(AstLiteralString string) {
        return instruction(CoreOpcode.PUSH_STRING_CONSTANT, string.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(AstConcatenation concatenation) {
        for (var expression : concatenation.getExpressions()) {
            expression.accept(this);
        }
        return instruction(CoreOpcode.JOIN_STRING, concatenation.getExpressions().length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(AstVariableExpression variableExpression) {
        var variable = variableExpression.getVariable();
        if (variable.getDomain() == VariableDomain.LOCAL) {
            var local = localMap.lookup(variable.getName());
            return instruction(getPushVariableOpcode(variable.getDomain(), variable.getType()), local);
        } else {
            return instruction(getPushVariableOpcode(variable.getDomain(), variable.getType()), variable);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(AstArrayExpression arrayExpression) {
        arrayExpression.getIndex().accept(this);
        return instruction(PUSH_ARRAY_INT, arrayExpression.getArray().getIndex());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(AstCall call) {
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
    public Instruction visit(AstDynamic dynamic) {
        if (dynamic.getType() instanceof ArrayReference) {
            return instruction(PUSH_INT_CONSTANT, ((ArrayReference) dynamic.getType()).getIndex());
        } else {
            var name = dynamic.getName().getText();
            var commandInfo = symbolTable.lookupCommand(name);
            if (commandInfo != null) {
                return generateCommand(commandInfo);
            } else {
                var configInfo = symbolTable.lookupConfig(name);
                return instruction(PUSH_INT_CONSTANT, configInfo.getId());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Instruction visit(AstConstant constant) {
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
    public Instruction visit(AstCommand command) {
        for (var argument : command.getArguments()) {
            argument.accept(this);
        }
        return generateCommand(symbolTable.lookupCommand(command.getName().getText()));
    }

    /**
     * {@inheritDoc}
     */
    public Instruction visit(AstCalc command) {
        return command.getExpression().accept(this);
    }

    /**
     * Generates the instruction(s) set for the specified {@link CommandInfo command}.
     *
     * @param info
     *         the command info to generate the instruction(s) set for.
     *
     * @return the last generated {@link Instruction} object.
     */
    private Instruction generateCommand(CommandInfo info) {
        return instruction(info.getOpcode(), info.isAlternative() ? 1 : 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(AstBinaryOperation binaryOperation) {
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
    public Instruction visit(AstVariableDeclaration variableDeclaration) {
        if (variableDeclaration.getExpression() != null) {
            variableDeclaration.getExpression().accept(this);
        } else {
            var opcode = getConstantOpcode(variableDeclaration.getType());
            instruction(opcode, variableDeclaration.getType().getDefaultValue());
        }
        var variable = variableDeclaration.getVariable();
        var local = localMap.registerVariable(variable.getName(), variable.getType());
        return instruction(getPopVariableOpcode(variable.getDomain(), variable.getType()), local);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(AstArrayDeclaration arrayDeclaration) {
        arrayDeclaration.getSize().accept(this);
        var array = arrayDeclaration.getArray();
        return instruction(DEFINE_ARRAY, (array.getIndex() << 16) | array.getType().getCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(AstVariableInitializer variableInitializer) {
        variableInitializer.getExpression().accept(this);
        var variable = variableInitializer.getVariable();
        var local = variable.getDomain() == VariableDomain.LOCAL ? localMap.lookup(variable.getName()) : variable;
        return instruction(getPopVariableOpcode(variable.getDomain(), variable.getType()), local);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(AstArrayInitializer arrayInitializer) {
        arrayInitializer.getValue().accept(this);
        arrayInitializer.getIndex().accept(this);
        return instruction(POP_ARRAY_INT, arrayInitializer.getArray().getIndex());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstSwitchStatement switchStatement) {
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
    public Void visit(AstIfStatement ifStatement) {
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
    public Void visit(AstWhileStatement whileStatement) {
        // preserve the labels of this while statement for the number order.
        var while_start_label = labelGenerator.generate("while_start");
        var while_true_label = labelGenerator.generate("while_true");
        var while_end_label = labelGenerator.generate("while_end");
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
     * Performs code generation on the specified {@code condition} expression and returns it's associated {@link
     * CoreOpcode opcode}.
     *
     * @param condition
     *         the condition expression to perform the code generation on.
     * @param source_block
     *         the source block of the condition.
     * @param branch_true
     *         the if-true block label.
     * @param branch_false
     *         the if-false block label.
     *
     * @return the {@link CoreOpcode} of the generated condition code.
     */
    private void generateCondition(AstExpression condition, Block source_block, Label branch_true, Label branch_false) {
        if (condition instanceof AstBinaryOperation) {
            var binaryOperation = (AstBinaryOperation) condition;
            var operator = binaryOperation.getOperator();
            if (operator.isEquality() || operator.isRelational()) {
                CoreOpcode opcode;
                switch (operator) {
                    case EQUAL:
                        opcode = CoreOpcode.BRANCH_EQUALS;
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
    public Void visit(AstExpressionStatement expressionStatement) {
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
    public Void visit(AstReturnStatement returnStatement) {
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
    public Void visit(AstBlockStatement blockStatement) {
        for (var statement : blockStatement.getStatements()) {
            statement.accept(this);
        }
        return null;
    }

    /**
     * Generate a specific amount of discard instructions for each of the stack types.
     *
     * @param numInts
     *         the amount of integer discard instructions.
     * @param numStrings
     *         the amount of string discard instructions.
     * @param numLongs
     *         the amount of long discard instructions.
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
     * @param type
     *         the type to resolve for.
     *
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
     * @param opcode
     *         the opcode of the instruction.
     * @param operand
     *         the operand of the instruction.
     *
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
     * @param block
     *         the block to add the instruction to.
     * @param opcode
     *         the opcode of the instruction.
     * @param operand
     *         the operand of the instruction.
     *
     * @return the created {@link Instruction} object.
     */
    private Instruction instruction(Block block, CoreOpcode opcode, Object operand) {
        return instruction(block, instructionMap.lookup(opcode), operand);
    }

    /**
     * Creates a new {@link Instruction instruction} using {@link #makeInstruction(Opcode, Object)} and then adds it as
     * a child instruction to the current active block in the {@link #blockMap block map}.
     *
     * @param opcode
     *         the opcode of the instruction.
     * @param operand
     *         the operand of the instruction.
     *
     * @return the created {@link Instruction} object.
     */
    private Instruction instruction(Opcode opcode, Object operand) {
        return instruction(context().getBlock(), opcode, operand);
    }

    /**
     * Creates a new {@link Instruction instruction} using {@link #makeInstruction(Opcode, Object)} and then adds it as
     * a child instruction to the current active block in the {@link #blockMap block map}.
     *
     * @param block
     *         the block to add the instruction to.
     * @param opcode
     *         the opcode of the instruction.
     * @param operand
     *         the operand of the instruction.
     *
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
     * @param opcode
     *         the opcode of the instruction.
     * @param operand
     *         the operand of the instruction.
     *
     * @return the created {@link Instruction} object.
     */
    private Instruction makeInstruction(Opcode opcode, Object operand) {
        return new Instruction(opcode, operand);
    }

    /**
     * Binds the specified {@link Block block} as the current working block.
     *
     * @param block
     *         the block to bind as the working block.
     *
     * @return the block that was passed to the method.
     */
    private Block bind(Block block) {
        context().setBlock(block);
        return block;
    }

    /**
     * Generates a new {@link Block} object.
     *
     * @param name
     *         the name of the block label.
     *
     * @return the generated {@link Block} object.
     * @see BlockMap#generate(Label)
     */
    private Block generateBlock(String name) {
        return generateBlock(generateLabel(name));
    }

    /**
     * Generates a new {@link Block} object.
     *
     * @param label
     *         the label of the block.
     *
     * @return the generated {@link Block} object.
     * @see BlockMap#generate(Label)
     */
    private Block generateBlock(Label label) {
        return blockMap.generate(label);
    }

    /**
     * Generates a new unique {@link Label} object.
     *
     * @param name
     *         the name of the label.
     *
     * @return the generated {@link Label} object.
     * @see LabelGenerator#generate(String)
     */
    private Label generateLabel(String name) {
        return labelGenerator.generate(name);
    }

    /**
     * Gets the current active {@Link Context} object.
     *
     * @return the active {@link Context} object.
     */
    public Context context() {
        return contexts.lastElement();
    }

    /**
     * Creates a new {@Link Context} object and pushes it into the stack.
     *
     * @param type
     *         the type of the context.
     *
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
     * Gets the push variable instruction {@link CoreOpcode opcode} of the specified {@link VariableDomain} and the
     * specified {@link Type}.
     *
     * @param domain
     *         the variable domain.
     * @param type
     *         the variable type.
     *
     * @return the instruction {@link CoreOpcode opcode} of that constant type.
     */
    private static CoreOpcode getPushVariableOpcode(VariableDomain domain, Type type) {
        switch (domain) {
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
            case PLAYER:
                return CoreOpcode.PUSH_VARP;
            case PLAYER_BIT:
                return CoreOpcode.PUSH_VARP_BIT;
            case CLIENT_INT:
                return CoreOpcode.PUSH_VARC_INT;
            case CLIENT_STRING:
                return CoreOpcode.PUSH_VARC_STRING;
            default:
                throw new UnsupportedOperationException("Unsupported variable domain: " + domain);
        }
    }

    /**
     * Gets the pop variable instruction {@link CoreOpcode opcode} of the specified {@link VariableDomain} and the
     * specified {@link Type}.
     *
     * @param domain
     *         the variable domain.
     * @param type
     *         the variable type.
     *
     * @return the instruction {@link CoreOpcode opcode} of that constant type.
     */
    private static CoreOpcode getPopVariableOpcode(VariableDomain domain, Type type) {
        switch (domain) {
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
            case PLAYER:
                return CoreOpcode.POP_VARP;
            case PLAYER_BIT:
                return CoreOpcode.POP_VARP_BIT;
            case CLIENT_INT:
                return CoreOpcode.POP_VARC_INT;
            case CLIENT_STRING:
                return CoreOpcode.POP_VARC_STRING;
            default:
                throw new UnsupportedOperationException("Unsupported variable domain: " + domain);
        }
    }

    /**
     * Gets the instruction {@link CoreOpcode} of the specified constant {@link Type}.
     *
     * @param type
     *         the type of the constant.
     *
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

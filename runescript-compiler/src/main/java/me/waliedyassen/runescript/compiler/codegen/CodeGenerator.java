/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.waliedyassen.runescript.compiler.ast.AstParameter;
import me.waliedyassen.runescript.compiler.ast.AstScript;
import me.waliedyassen.runescript.compiler.ast.expr.*;
import me.waliedyassen.runescript.compiler.ast.expr.literal.AstLiteralBool;
import me.waliedyassen.runescript.compiler.ast.expr.literal.AstLiteralInteger;
import me.waliedyassen.runescript.compiler.ast.expr.literal.AstLiteralLong;
import me.waliedyassen.runescript.compiler.ast.expr.literal.AstLiteralString;
import me.waliedyassen.runescript.compiler.ast.stmt.*;
import me.waliedyassen.runescript.compiler.ast.stmt.conditional.AstIfStatement;
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;
import me.waliedyassen.runescript.compiler.codegen.asm.*;
import me.waliedyassen.runescript.compiler.codegen.context.Context;
import me.waliedyassen.runescript.compiler.codegen.context.ContextType;
import me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode;
import me.waliedyassen.runescript.compiler.codegen.opcode.Opcode;
import me.waliedyassen.runescript.compiler.symbol.SymbolTable;
import me.waliedyassen.runescript.compiler.symbol.impl.variable.VariableDomain;
import me.waliedyassen.runescript.compiler.type.Type;
import me.waliedyassen.runescript.compiler.util.trigger.TriggerType;

import java.util.Stack;

import static me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode.BRANCH;

/**
 * Represents the compiler bytecode generator.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class CodeGenerator implements AstVisitor {

    /**
     * The label generator used to generate any label for this code generator.
     */
    private final LabelGenerator labelGenerator = new LabelGenerator();

    /**
     * The blocks map of the current script.
     */
    @Getter
    private final BlockMap blockMap = new BlockMap();

    /**
     * The locals map of the current script.
     */
    private final LocalMap localMap = new LocalMap();

    /**
     * The symbol table which has all the information for the current generation.
     */
    private final SymbolTable symbolTable;

    /**
     * The instructions map which contains the primary instruction opcodes.
     */
    private final InstructionMap instructionMap;

    /**
     * The current block we are working on.
     */
    private final Stack<Context> contexts = new Stack<Context>();

    /**
     * Initialises the code generator and reset its state.
     */
    public void initialise() {
        labelGenerator.reset();
        blockMap.reset();
        localMap.reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Script visit(AstScript script) {
        pushContext(ContextType.SCRIPT);
        var generated = new Script("[" + script.getTrigger().getText() + "," + script.getName().getText() + "]");
        for (var parameter : script.getParameters()) {
            parameter.accept(this);
        }
        bind(generateBlock());
        script.getCode().accept(this);
        popContext();
        return generated;
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
        return instruction(getPushVariableOpcode(variable.getDomain(), variable.getType()), variable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(AstGosub gosub) {
        for (var argument : gosub.getArguments()) {
            argument.accept(this);
        }
        var script = symbolTable.lookupScript(TriggerType.PROC, gosub.getName().getText());
        return instruction(CoreOpcode.GOSUB_WITH_PARAMS, script);
    }

    // TODO: AstDynamic code generation.

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
        var symbol = symbolTable.lookupCommand(command.getName().getText());
        for (var argument : command.getArguments()) {
            argument.accept(this);
        }
        return instruction(symbol.getOpcode(), symbol.isAlternative() ? 1 : 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CoreOpcode visit(AstBinaryOperation binaryOperation) {
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
            return opcode;
        } else {
            throw new UnsupportedOperationException("Unexpected operator: " + operator);
        }
    }

    /**
     * Performs code generation on the specified {@code condition} expression and returns it's associated {@link
     * CoreOpcode opcode}.
     *
     * @param condition
     *         the condition expression to perform the code generation on.
     *
     * @return the {@link CoreOpcode} of the generated condition code.
     */
    private CoreOpcode generateCondition(AstExpression condition) {
        if (condition instanceof AstBinaryOperation) {
            return visit((AstBinaryOperation) condition);
        } else {
            condition.accept(this);
            return CoreOpcode.BRANCH_IF_TRUE;
        }
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
    public Instruction visit(AstVariableInitializer variableInitializer) {
        variableInitializer.getExpression().accept(this);
        var variable = variableInitializer.getVariable();
        var local = variable.getDomain() == VariableDomain.LOCAL ? localMap.registerVariable(variable.getName(), variable.getType()) : variable;
        return instruction(getPopVariableOpcode(variable.getDomain(), variable.getType()), local);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(AstIfStatement ifStatement) {
        var has_else = ifStatement.getFalseStatement() != null;
        var source_block = context().getBlock();
        var opcode = generateCondition(ifStatement.getCondition());
        // generate the true statement block and code.
        var true_block = bind(generateBlock());
        ifStatement.getTrueStatement().accept(this);
        // generate the else statement block and code.
        var else_block = has_else ? generateBlock() : null;
        if (has_else) {
            bind(else_block);
            ifStatement.getFalseStatement().accept(this);
        }
        // generate the false block and bind it.
        var false_block = bind(generateBlock());
        // generate the branch instructions for all of the blocks.
        instruction(source_block, opcode, true_block);
        instruction(source_block, BRANCH, has_else ? else_block : false_block);
        instruction(true_block, BRANCH, false_block);
        if (has_else) {
            instruction(else_block, BRANCH, false_block);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(AstExpressionStatement expressionStatement) {
        var entity = expressionStatement.getExpression().accept(this);
        // TODO: Proper pop_x_discard emitting.
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(AstReturnStatement returnStatement) {
        for (var expression : returnStatement.getExpressions()) {
            expression.accept(this);
        }
        return instruction(CoreOpcode.RETURN, 0);
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
     * @return the generated {@link Block} object.
     * @see BlockMap#generate(Label)
     */
    private Block generateBlock() {
        return blockMap.generate(generateLabel());
    }

    /**
     * Generates a new unique {@link Label} object.
     *
     * @return the generated {@link Label} object.
     * @see LabelGenerator#generate()
     */
    private Label generateLabel() {
        return labelGenerator.generate();
    }

    /**
     * Gets the current active {@Link Context} object.
     *
     * @return the active {@link Context} object.
     */
    private Context context() {
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
    private Context pushContext(ContextType type) {
        var context = new Context(type);
        contexts.push(context);
        return context;
    }

    /**
     * Pops the last context from the stack.
     *
     * @return the popped {@link Context} object.
     */
    private Context popContext() {
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

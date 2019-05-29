/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.ast.AstParameter;
import me.waliedyassen.runescript.compiler.ast.AstScript;
import me.waliedyassen.runescript.compiler.ast.expr.AstConcatenation;
import me.waliedyassen.runescript.compiler.ast.expr.AstGosub;
import me.waliedyassen.runescript.compiler.ast.expr.AstVariableExpression;
import me.waliedyassen.runescript.compiler.ast.expr.literal.AstLiteralBool;
import me.waliedyassen.runescript.compiler.ast.expr.literal.AstLiteralInteger;
import me.waliedyassen.runescript.compiler.ast.expr.literal.AstLiteralLong;
import me.waliedyassen.runescript.compiler.ast.expr.literal.AstLiteralString;
import me.waliedyassen.runescript.compiler.ast.stmt.AstBlockStatement;
import me.waliedyassen.runescript.compiler.ast.stmt.AstVariableInitializer;
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;
import me.waliedyassen.runescript.compiler.codegen.asm.*;

import java.util.List;
import java.util.Stack;

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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Script visit(AstScript script) {
        var generated = new Script("[" + script.getTrigger().getText() + "," + script.getName().getText() + "]");
        script.getCode().accept(this);
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
        return instruction(instructionMap.getPushConstantInt(), bool.getValue() ? 1 : 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(AstLiteralInteger integer) {
        return instruction(instructionMap.getPushConstantInt(), integer.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(AstLiteralString string) {
        return instruction(instructionMap.getPushConstantString(), string.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(AstLiteralLong longInteger) {
        return instruction(instructionMap.getPushConstantLong(), longInteger.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(AstConcatenation concatenation) {
        for (var expression : concatenation.getExpressions()) {
            expression.accept(this);
        }
        return instruction(instructionMap.getJoinString(), concatenation.getExpressions().length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instruction visit(AstVariableExpression variableExpression) {
        var local = localMap.lookup(variableExpression.getName().getText());
        int opcode;
        switch (local.getType().getStackType()) {
            case INT:
                opcode = instructionMap.getPushLocalInt();
                break;
            case STRING:
                opcode = instructionMap.getPushLocalString();
                break;
            case LONG:
                opcode = instructionMap.getPushLocalLong();
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return instruction(opcode, local);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Block visit(AstBlockStatement blockStatement) {
        var block = generateBlock();
        for (var statement : blockStatement.getStatements()) {
            statement.accept(this);
        }
        return block;
    }

    /**
     * Creates a new {@link Instruction instruction} using {@link #makeInstruction(int, Object)} and then adds it as a
     * child instruction to the current active block in the {@link #blockMap block map}.
     *
     * @param opcode
     *         the opcode of the instruction.
     * @param operand
     *         the operand of the instruction.
     *
     * @return the created {@link Instruction} object.
     */
    private Instruction instruction(int opcode, Object operand) {
        var instruction = makeInstruction(opcode, operand);
        blockMap.getCurrent().add(instruction);
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
    private Instruction makeInstruction(int opcode, Object operand) {
        return new Instruction(opcode, operand);
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
}

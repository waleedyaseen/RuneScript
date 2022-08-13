/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.writer.bytecode;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.codegen.block.Block;
import me.waliedyassen.runescript.compiler.codegen.block.BlockList;
import me.waliedyassen.runescript.compiler.codegen.block.Label;
import me.waliedyassen.runescript.compiler.codegen.local.Local;
import me.waliedyassen.runescript.compiler.codegen.script.BinaryScript;
import me.waliedyassen.runescript.compiler.codegen.sw.SwitchTable;
import me.waliedyassen.runescript.compiler.codegen.writer.CodeWriter;
import me.waliedyassen.runescript.compiler.idmapping.IDManager;
import me.waliedyassen.runescript.compiler.symbol.Symbol;
import me.waliedyassen.runescript.type.stack.StackType;

import java.util.*;

/**
 * Represents a {@link CodeWriter} implementation that writes to asm bytecode format and outputs an {@link
 * BytecodeScript} object containing all the data for the byte code.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class BytecodeCodeWriter extends CodeWriter<BytecodeScript> {

    /**
     * An empty list to save some lines of code that compares if the variable stack is present or not.
     */
    private static final List<Local> EMPTY = Collections.emptyList();

    /**
     * The ID provider which is used to translate names into ids.
     */
    private final IDManager idManager;

    /**
     * Whether or not the code writer supports long primitive type.
     */
    private final boolean supportsLongPrimitiveType;

    /**
     * {@inheritDoc}
     */
    @Override
    public BytecodeScript write(BinaryScript script) {
        // Build the address table of the blocks.
        final var addressTable = buildAddressTable(script.getBlockList());
        // Build the index table of the local variables.
        final var localTable = buildLocalTable(script.getParameters(), script.getVariables());
        // Calculate the local variables and  parameters count.
        var numIntParameters = script.getParameters().getOrDefault(StackType.INT, EMPTY).size();
        var numStringParameters = script.getParameters().getOrDefault(StackType.STRING, EMPTY).size();
        var numLongParameters = script.getParameters().getOrDefault(StackType.LONG, EMPTY).size();
        var numIntLocals = script.getVariables().getOrDefault(StackType.INT, EMPTY).size() + numIntParameters;
        var numStringLocals = script.getVariables().getOrDefault(StackType.STRING, EMPTY).size() + numStringParameters;
        var numLongLocals = script.getVariables().getOrDefault(StackType.LONG, EMPTY).size() + numLongParameters;
        var switchTables = new LinkedList<Hashtable<Integer, Integer>>();
        // create the codegen context.
        var context = new BytecodeGenContext(script, addressTable, localTable, switchTables);
        final var instructions = new ArrayList<BytecodeInstruction>();
        for (var block : script.getBlockList().getBlocks()) {
            for (var instruction : block.getInstructions()) {
                var address = instructions.size();
                var operand = resolveOperand(context, address, instruction.getOperand());
                if (operand == null) {
                    throw new IllegalStateException("Null operands are not allowed");
                }
                instructions.add(new BytecodeInstruction(instruction.getOpcode().getCode(), instruction.getOpcode().isLarge(), operand));
            }
        }
        // Create the container object and return it.
        return new BytecodeScript(
                script.getName(), numIntParameters, numStringParameters, numLongParameters, numIntLocals,
                numStringLocals, numLongLocals, instructions.toArray(new BytecodeInstruction[0]), switchTables,
                supportsLongPrimitiveType);
    }

    /**
     * Resolves the value of the specified {@code operand}.
     *
     * @param context the bytecode code generation context.
     * @param address the address of the instruction the operand is for.
     * @param operand the operand that we are trying to resolve
     * @return the resolved operand value.
     */
    private Object resolveOperand(BytecodeGenContext context, int address, Object operand) {
        if (operand instanceof Label) {
            return context.addressTable.get(operand) - address - 1;
        } else if (operand instanceof SwitchTable) {
            var jumps = new Hashtable<Integer, Integer>();
            for (var $case : ((SwitchTable) operand).getCases()) {
                var jump = context.addressTable.get($case.getLabel()) - address - 1;
                for (var key : $case.getKeys()) {
                    jumps.put((int) resolveOperand(context, address, key), jump);
                }
            }
            var index = context.switchTables.size();
            context.switchTables.add(jumps);
            return index;
        } else if (operand instanceof Symbol symbol) {
            return symbol.getId();
        } else if (operand instanceof Local) {
            return context.localTable.get(operand);
        } else if (operand instanceof Integer) {
            return operand;
        } else if (operand instanceof String) {
            return operand;
        } else if (operand instanceof Long) {
            return operand;
        } else if (operand instanceof Boolean) {
            return ((Boolean) operand) ? 1 : 0;
        } else {
            throw new UnsupportedOperationException("Unsupported operand type: " + operand);
        }
    }

    /**
     * Builds the index table of the specified local variables nad parameters.
     *
     * @param parameters the parameters to  build the index table for.
     * @param variables  the local variables to build the index table for.
     * @return the index table as a {@link Map} object.
     */
    private Map<Local, Integer> buildLocalTable(Map<StackType, List<Local>> parameters, Map<StackType, List<Local>> variables) {
        final Map<StackType, List<Local>>[] combined = new Map[]{parameters, variables};
        var table = new HashMap<Local, Integer>();
        var numInts = 0;
        var numStrings = 0;
        var numLongs = 0;
        for (var map : combined) {
            for (var stackType : map.keySet()) {
                for (var local : map.get(stackType)) {
                    switch (stackType) {
                        case INT:
                            table.put(local, numInts++);
                            break;
                        case STRING:
                            table.put(local, numStrings++);
                            break;
                        case LONG:
                            table.put(local, numLongs++);
                            break;
                    }
                }
            }
        }
        return table;
    }

    /**
     * Builds the address table for the specified map of {@link Block blocks}.
     *
     * @param blocks the map of blocks to build the address table for.
     * @return the address table as a {@link Map} object.
     */
    private Map<Label, Integer> buildAddressTable(BlockList blocks) {
        var table = new HashMap<Label, Integer>();
        var address = 0;
        for (var block : blocks.getBlocks()) {
            table.put(block.getLabel(), address);
            address += block.getInstructions().size();
        }
        return table;
    }

    /**
     * A bytecode generation context.
     *
     * @author Walied K. Yassen
     */
    @Data
    private static final class BytecodeGenContext {

        /**
         * The script that we are generating.
         */
        @Getter
        private final BinaryScript script;

        /**
         * A table of all the labels.
         */
        @Getter
        private final Map<Label, Integer> addressTable;

        /**
         * A table of all the local variables.
         */
        @Getter
        private final Map<Local, Integer> localTable;

        /**
         * A list of all the switch tables.
         */
        @Getter
        private final LinkedList<Hashtable<Integer, Integer>> switchTables;

    }
}


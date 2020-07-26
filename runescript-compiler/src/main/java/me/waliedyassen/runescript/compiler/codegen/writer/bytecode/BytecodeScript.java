/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.writer.bytecode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.var;
import me.waliedyassen.runescript.util.StreamUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 * Represents a byte code script.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class BytecodeScript {

    /**
     * The name of the script.
     */
    @Getter
    private final String name;

    /**
     * The amount of integer parameters of the script.
     */
    @Getter
    private final int numIntParameters;

    /**
     * The amount of string parameters of the script.
     */
    @Getter
    private final int numStringParameters;

    /**
     * The amount of long parameters of the script.
     */
    @Getter
    private final int numLongParameters;

    /**
     * The amount of integer locals of the script.
     */
    @Getter
    private final int numIntLocals;

    /**
     * The amount of string locals of the script.
     */
    @Getter
    private final int numStringLocals;

    /**
     * The amount of long locals of the script.
     */
    @Getter
    private final int numLongLocals;

    /**
     * The instructions of the script.
     */
    @Getter
    private final BytecodeInstruction[] instructions;

    /**
     * The switch tables of the script.
     */
    @Getter
    private final LinkedList<Hashtable<Integer, Integer>> switchTables;

    /**
     * Whether or not the script supports long primitive type.
     */
    @Getter
    private final boolean supportsLongPrimitiveType;

    /**
     * Encodes the bytecode script to bytecode data.
     */
    @SneakyThrows
    public byte[] encode() {
        try (var bos = new ByteArrayOutputStream(); var data = new DataOutputStream(bos)) {
            // write the name of the script.
            StreamUtil.writeString(data, name);
            for (var instruction : instructions) {
                var operand = instruction.getOperand();
                data.writeShort(instruction.getOpcode());
                if (operand instanceof String) {
                    StreamUtil.writeString(data, (String) operand);
                } else if (operand instanceof Long) {
                    if (!supportsLongPrimitiveType) {
                        throw new IllegalArgumentException("Long operands are not allowed");
                    }
                    data.writeLong((long) operand);
                } else {
                    if (instruction.isLarge()) {
                        data.writeInt((int) operand);
                    } else {
                        data.writeByte((int) operand);
                    }
                }
            }
            // write the instructions of the script.
            data.writeInt(instructions.length);
            // write the locals count of the script.
            data.writeShort(numIntLocals);
            data.writeShort(numStringLocals);
            if (supportsLongPrimitiveType) {
                data.writeShort(numLongLocals);
            }
            // write the parameters count of the script.
            data.writeShort(numIntParameters);
            data.writeShort(numStringParameters);
            if (supportsLongPrimitiveType) {
                data.writeShort(numLongParameters);
            }
            // write the switch tables of the script.
            var size = 1;
            data.writeByte(switchTables.size());
            for (var table : switchTables) {
                data.writeShort(table.size());
                for (var entry : table.entrySet()) {
                    data.writeInt(entry.getKey());
                    data.writeInt(entry.getValue());
                }
                size += 2 + table.size() * 8;
            }
            data.writeShort(size);
            return bos.toByteArray();
        }
    }
}

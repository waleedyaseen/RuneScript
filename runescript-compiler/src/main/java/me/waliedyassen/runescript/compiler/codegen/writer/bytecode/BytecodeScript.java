/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.writer.bytecode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
     * Writes the byte code data to the specified {@link OutputStream stream}.
     *
     * @param stream                    the stream to write the byte code data to.
     * @param supportsLongPrimitiveType whether or not the writer supports long primitive type.
     * @throws IOException if anything occurs while writing the data.
     */
    public void write(OutputStream stream, boolean supportsLongPrimitiveType) throws IOException {
        try (var data = new DataOutputStream(stream)) {
            // write the name of the script.
            writeString(data, name);
            for (var instruction : instructions) {
                var operand = instruction.getOperand();
                data.writeShort(instruction.getOpcode());
                if (operand instanceof String) {
                    writeString(data, (String) operand);
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
        }
    }

    /**
     * Writes a C-Style string (null terminated string) to the specified {@link DataOutputStream stream}.
     *
     * @param stream the stream to write to.
     * @param value  the string to write.
     * @throws IOException if anything occurs while writing the string to the stream.
     */
    private void writeString(DataOutputStream stream, String value) throws IOException {
        stream.writeBytes(value);
        stream.writeByte(0);
    }
}

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.runtime.executor.impl;

import me.waliedyassen.runescript.runtime.executor.instruction.InstructionExecutor;

/**
 * Contains all of the core RuneScript operations.
 *
 * @author Walied K. Yasssen
 */
public interface CoreOps {

    /**
     * Pushes a constant integer value to the stack.
     */
    InstructionExecutor PUSH_CONSTANT_INT = (runtime, opcode) -> runtime.pushInt(runtime.intOperand());

    /**
     * Pushes a constant string value to the stack.
     */
    InstructionExecutor PUSH_CONSTANT_STRING = (runtime, opcode) -> runtime.pushString(runtime.stringOperand());

    /**
     * Pushes a long string value to the stack.
     */
    InstructionExecutor PUSH_CONSTANT_LONG = (runtime, opcode) -> runtime.pushLong(runtime.longOperand());

    /**
     * Discards the last value from the int stack.
     */
    InstructionExecutor POP_INT_DISCARD = (runtime, opcode) -> runtime.popInt();

    /**
     * Discards the last value from the string stack.
     */
    InstructionExecutor POP_STRING_DISCARD = (runtime, opcode) -> runtime.popString();

    /**
     * Discards the last value from the long stack.
     */
    InstructionExecutor POP_LONG_DISCARD = (runtime, opcode) -> runtime.popLong();

    /**
     * Pushes the value of an integer local field to the stack.
     */
    InstructionExecutor PUSH_INT_LOCAL = (runtime, opcode) -> runtime.pushInt(runtime.getIntLocals()[runtime.intOperand()]);

    /**
     * Updates the value of a local field from the stack.
     */
    InstructionExecutor POP_INT_LOCAL = (runtime, opcode) -> runtime.getIntLocals()[runtime.intOperand()] = runtime.popInt();

    /**
     * Pushes the value of a string local field to the stack.
     */
    InstructionExecutor PUSH_STRING_LOCAL = (runtime, opcode) -> runtime.pushString(runtime.getStringLocals()[runtime.intOperand()]);

    /**
     * Updates the value of a string local field from the stack.
     */
    InstructionExecutor POP_STRING_LOCAL = (runtime, opcode) -> runtime.getStringLocals()[runtime.intOperand()] = runtime.popString();

    /**
     * Pushes the value of an long local field to the stack.
     */
    InstructionExecutor PUSH_LONG_LOCAL = (runtime, opcode) -> runtime.pushLong(runtime.getLongLocals()[runtime.intOperand()]);

    /**
     * Updates the value of a long local field from the stack.
     */
    InstructionExecutor POP_LONG_LOCAL = (runtime, opcode) -> runtime.getLongLocals()[runtime.intOperand()] = runtime.popLong();

    /**
     * Branch to an address that is X away from the current address.
     */
    InstructionExecutor BRANCH = (runtime, opcode) -> runtime.setAddress(runtime.getAddress() + runtime.intOperand());

    /**
     * Branch to address that is X away from the current address if the X value is not equal to Y value.
     */
    InstructionExecutor BRANCH_NOT = (runtime, opcode) -> {
        var right = runtime.popInt();
        var left = runtime.popInt();
        if (left != right) {
            runtime.setAddress(runtime.getAddress() + runtime.intOperand());
        }
    };

    /**
     * Branch to address that is X away from the current address if the X value is equal to Y value.
     */
    InstructionExecutor BRANCH_EQUALS = (runtime, opcode) -> {
        var right = runtime.popInt();
        var left = runtime.popInt();
        if (left == right) {
            runtime.setAddress(runtime.getAddress() + runtime.intOperand());
        }
    };

    /**
     * Branch to address that is X away from the current address if the X value is less than Y value.
     */
    InstructionExecutor BRANCH_LESS_THAN = (runtime, opcode) -> {
        var right = runtime.popInt();
        var left = runtime.popInt();
        if (left < right) {
            runtime.setAddress(runtime.getAddress() + runtime.intOperand());
        }
    };

    /**
     * Branch to address that is X away from the current address if the X value is greater than to Y value.
     */
    InstructionExecutor BRANCH_GREATER_THAN = (runtime, opcode) -> {
        var right = runtime.popInt();
        var left = runtime.popInt();
        if (left > right) {
            runtime.setAddress(runtime.getAddress() + runtime.intOperand());
        }
    };

    /**
     * Branch to address that is X away from the current address if the X value is greater than to Y value.
     */
    InstructionExecutor BRANCH_LESS_THAN_OR_EQUALS = (runtime, opcode) -> {
        var right = runtime.popInt();
        var left = runtime.popInt();
        if (left <= right) {
            runtime.setAddress(runtime.getAddress() + runtime.intOperand());
        }
    };

    /**
     * Branch to address that is X away from the current address if the X value is greater than to Y value.
     */
    InstructionExecutor BRANCH_GREATER_THAN_OR_EQUALS = (runtime, opcode) -> {
        var right = runtime.popInt();
        var left = runtime.popInt();
        if (left >= right) {
            runtime.setAddress(runtime.getAddress() + runtime.intOperand());
        }
    };

    /**
     * Takes an X amount of strings and combine them into one string then push that into the stack.
     */
    InstructionExecutor JOIN_STRING = (runtime, opcode) -> {
        var count = runtime.intOperand();
        var size = 0;
        for (var index = 0; index < count; index++) {
            var value = runtime.getStringStack().get(runtime.getStringStack().size() - count + index);
            size += value == null ? 4 : value.length();
        }
        var builder = new StringBuilder(size);
        for (var index = 0; index < count; index++) {
            var value = runtime.getStringStack().get(runtime.getStringStack().size() - count + index);
            builder.append(value);
        }
        for (var index = 0; index < count; index++) {
            runtime.popString();
        }
        runtime.pushString(builder.toString());
    };
}

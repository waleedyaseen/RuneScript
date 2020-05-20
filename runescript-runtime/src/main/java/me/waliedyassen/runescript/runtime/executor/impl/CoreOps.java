/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.runtime.executor.impl;

import me.waliedyassen.runescript.runtime.ScriptFrame;
import me.waliedyassen.runescript.runtime.ScriptRuntime;
import me.waliedyassen.runescript.runtime.executor.ExecutionException;
import me.waliedyassen.runescript.runtime.executor.instruction.InstructionExecutor;

import java.util.Stack;

/**
 * Contains all of the core RuneScript operations.
 *
 * @author Walied K. Yasssen
 */
public interface CoreOps {

    /**
     * Pushes a constant integer value to the stack.
     */
    InstructionExecutor<? extends ScriptRuntime> PUSH_CONSTANT_INT = runtime -> runtime.pushInt(runtime.intOperand());

    /**
     * Pushes a constant string value to the stack.
     */
    InstructionExecutor<? extends ScriptRuntime> PUSH_CONSTANT_STRING = runtime -> runtime.pushString(runtime.stringOperand());

    /**
     * Pushes a long string value to the stack.
     */
    InstructionExecutor<? extends ScriptRuntime> PUSH_CONSTANT_LONG = runtime -> runtime.pushLong(runtime.longOperand());

    /**
     * Discards the last value from the int stack.
     */
    InstructionExecutor<? extends ScriptRuntime> POP_INT_DISCARD = ScriptRuntime::popInt;

    /**
     * Discards the last value from the string stack.
     */
    InstructionExecutor<? extends ScriptRuntime> POP_STRING_DISCARD = ScriptRuntime::popString;

    /**
     * Discards the last value from the long stack.
     */
    InstructionExecutor<? extends ScriptRuntime> POP_LONG_DISCARD = ScriptRuntime::popLong;

    /**
     * Pushes the value of an integer local field to the stack.
     */
    InstructionExecutor<? extends ScriptRuntime> PUSH_INT_LOCAL = runtime -> runtime.pushInt(runtime.getIntLocals()[runtime.intOperand()]);

    /**
     * Updates the value of a local field from the stack.
     */
    InstructionExecutor<? extends ScriptRuntime> POP_INT_LOCAL = runtime -> runtime.getIntLocals()[runtime.intOperand()] = runtime.popInt();

    /**
     * Pushes the value of a string local field to the stack.
     */
    InstructionExecutor<? extends ScriptRuntime> PUSH_STRING_LOCAL = runtime -> runtime.pushString(runtime.getStringLocals()[runtime.intOperand()]);

    /**
     * Updates the value of a string local field from the stack.
     */
    InstructionExecutor<? extends ScriptRuntime> POP_STRING_LOCAL = runtime -> runtime.getStringLocals()[runtime.intOperand()] = runtime.popString();

    /**
     * Pushes the value of an long local field to the stack.
     */
    InstructionExecutor<? extends ScriptRuntime> PUSH_LONG_LOCAL = runtime -> runtime.pushLong(runtime.getLongLocals()[runtime.intOperand()]);

    /**
     * Updates the value of a long local field from the stack.
     */
    InstructionExecutor<? extends ScriptRuntime> POP_LONG_LOCAL = runtime -> runtime.getLongLocals()[runtime.intOperand()] = runtime.popLong();

    /**
     * Branch to an address that is X away from the current address.
     */
    InstructionExecutor<? extends ScriptRuntime> BRANCH = runtime -> runtime.setAddress(runtime.getAddress() + runtime.intOperand());

    /**
     * Branch to address that is X away from the current address if the X value is not equal to Y value.
     */
    InstructionExecutor<? extends ScriptRuntime> BRANCH_NOT = runtime -> {
        var right = runtime.popInt();
        var left = runtime.popInt();
        if (left != right) {
            runtime.setAddress(runtime.getAddress() + runtime.intOperand());
        }
    };

    /**
     * Branch to address that is X away from the current address if the X value is equal to Y value.
     */
    InstructionExecutor<? extends ScriptRuntime> BRANCH_EQUALS = runtime -> {
        var right = runtime.popInt();
        var left = runtime.popInt();
        if (left == right) {
            runtime.setAddress(runtime.getAddress() + runtime.intOperand());
        }
    };

    /**
     * Branch to address that is X away from the current address if the X value is less than Y value.
     */
    InstructionExecutor<? extends ScriptRuntime> BRANCH_LESS_THAN = runtime -> {
        var right = runtime.popInt();
        var left = runtime.popInt();
        if (left < right) {
            runtime.setAddress(runtime.getAddress() + runtime.intOperand());
        }
    };

    /**
     * Branch to address that is X away from the current address if the X value is greater than to Y value.
     */
    InstructionExecutor<? extends ScriptRuntime> BRANCH_GREATER_THAN = runtime -> {
        var right = runtime.popInt();
        var left = runtime.popInt();
        if (left > right) {
            runtime.setAddress(runtime.getAddress() + runtime.intOperand());
        }
    };

    /**
     * Branch to address that is X away from the current address if the X value is greater than to Y value.
     */
    InstructionExecutor<? extends ScriptRuntime> BRANCH_LESS_THAN_OR_EQUALS = runtime -> {
        var right = runtime.popInt();
        var left = runtime.popInt();
        if (left <= right) {
            runtime.setAddress(runtime.getAddress() + runtime.intOperand());
        }
    };

    /**
     * Branch to address that is X away from the current address if the X value is greater than to Y value.
     */
    InstructionExecutor<? extends ScriptRuntime> BRANCH_GREATER_THAN_OR_EQUALS = runtime -> {
        var right = runtime.popInt();
        var left = runtime.popInt();
        if (left >= right) {
            runtime.setAddress(runtime.getAddress() + runtime.intOperand());
        }
    };

    /**
     * Takes an X amount of strings and combine them into one string then push that into the stack.
     */
    InstructionExecutor<? extends ScriptRuntime> JOIN_STRING = runtime -> {
        var count = runtime.intOperand();
        var size = 0;
        for (var index = 0; index < count; index++) {
            Stack<String> stack = runtime.getStringStack();
            var value = stack.get(runtime.getStringStack().size() - count + index);
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

    /**
     * Returns the execution to the script that is one level higher in execution stack or do nothing if there is none.
     */
    InstructionExecutor<? extends ScriptRuntime> RETURN = runtime -> {
        if (runtime.getFrames().isEmpty()) {
            return;
        }
        var frame = runtime.getFrames().pop();
        runtime.set(frame);
        ScriptFramePool.push(frame);
    };

    /**
     * Jumps to the specific script and returns to the original when the execution is over.
     */
    InstructionExecutor<? extends ScriptRuntime> GOSUB_WITH_PARAMS = runtime -> {
        var name = runtime.stringOperand();
        var script = runtime.getPool().getCache().get(name);
        if (script == null) {
            throw new ExecutionException("Failed to resolve script for name: " + name);
        }
        var frame = ScriptFramePool.pop();
        frame.set(runtime);
        runtime.getFrames().push(frame);
        for (var index = 0; index < ScriptRuntime.MAX_LOCALS; index++) {
            runtime.getIntLocals()[index] = index < script.getNumIntArguments() ? runtime.popInt() : 0;
        }
        for (var index = 0; index < ScriptRuntime.MAX_LOCALS; index++) {
            runtime.getStringLocals()[index] = index < script.getNumStringArguments() ? runtime.popString() : null;
        }
        for (var index = 0; index < ScriptRuntime.MAX_LOCALS; index++) {
            runtime.getLongLocals()[index] = index < script.getNumLongArguments() ? runtime.popLong() : 0;
        }
    };


    /**
     * Jumps to the specific script without returning to the original when the execution is over.
     */
    InstructionExecutor<? extends ScriptRuntime> JUMP_WITH_PARAMS = runtime -> {
        var name = runtime.stringOperand();
        var script = runtime.getPool().getCache().get(name);
        if (script == null) {
            throw new ExecutionException("Failed to resolve script for name: " + name);
        }
        runtime.setScript(script);
        runtime.setAddress(-1);
        for (var index = 0; index < ScriptRuntime.MAX_LOCALS; index++) {
            runtime.getIntLocals()[index] = index < script.getNumIntArguments() ? runtime.popInt() : 0;
        }
        for (var index = 0; index < ScriptRuntime.MAX_LOCALS; index++) {
            runtime.getStringLocals()[index] = index < script.getNumStringArguments() ? runtime.popString() : null;
        }
        for (var index = 0; index < ScriptRuntime.MAX_LOCALS; index++) {
            runtime.getLongLocals()[index] = index < script.getNumLongArguments() ? runtime.popLong() : 0;
        }
        runtime.getIntStack().clear();
        runtime.getStringStack().clear();
        runtime.getLongStack().clear();
        while (!runtime.getFrames().isEmpty()) {
            ScriptFramePool.push(runtime.getFrames().pop());
        }
    };

    /**
     * Performs a switch statement for the value on the stack with the switch table of the operand value.
     */
    InstructionExecutor<? extends ScriptRuntime> SWITCH = runtime -> {
        var switchTable = runtime.getScript().getSwitchTable()[runtime.intOperand()];
        if (switchTable == null) {
            throw new ExecutionException("Failed to find a switch table for switch index: " + runtime.intOperand());
        }
        var jump = switchTable.get(runtime.popInt());
        if (jump != null) {
            runtime.setAddress(runtime.getAddress() + 1);
        }
    };
}

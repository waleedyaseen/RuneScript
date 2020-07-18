package me.waliedyassen.runescript.runtime.executor.impl;

import lombok.var;
import me.waliedyassen.runescript.runtime.ScriptRuntime;
import me.waliedyassen.runescript.runtime.executor.ExecutionException;
import me.waliedyassen.runescript.runtime.executor.instruction.InstructionExecutor;

/**
 * Contains all of the common array operations.
 *
 * @author Walied K. Yassen
 */
public interface ArrayOps {

    /**
     * Defines a new array storage in the runtime.
     */
    InstructionExecutor<? extends ScriptRuntime> DEFINE_ARRAY = runtime -> {
        var id = runtime.intOperand() >> 16;
        var type = runtime.intOperand() & 0xffff;
        var size = runtime.popInt();
        if (size < 0 || size > 5000) {
            throw new ExecutionException("Failed to execute define_array: invalid array size: " + size);
        }
        runtime.getArraySize()[id] = size;
        var defaultValue = -1;
        if (type == 105) {
            defaultValue = 0;
        }
        for (var index = 0; index < size; index++) {
            runtime.getArrayElements()[id][index] = defaultValue;
        }
    };

    /**
     * Pushes an integer value from the array storage.
     */
    InstructionExecutor<? extends ScriptRuntime> PUSH_ARRAY_INT = runtime -> {
        var id = runtime.intOperand();
        var index = runtime.popInt();
        if (index < 0 || index >= runtime.getArraySize()[id]) {
            throw new ExecutionException("Failed to execute push_array_int: invalid array index: " + index);
        }
        runtime.pushInt(runtime.getArrayElements()[id][index]);
    };

    /**
     * Pops an integer value into the array storage.
     */
    InstructionExecutor<? extends ScriptRuntime> POP_ARRAY_INT = runtime -> {
        int id = runtime.intOperand();
        var value = runtime.popInt();
        var index = runtime.popInt();
        if (index < 0 || index >= runtime.getArraySize()[id]) {
            throw new ExecutionException("Failed to execute pop_array_int: invalid array index: " + index);
        }
        runtime.getArrayElements()[id][index] = value;
    };
}

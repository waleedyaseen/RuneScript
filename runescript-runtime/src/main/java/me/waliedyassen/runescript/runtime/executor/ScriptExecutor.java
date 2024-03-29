/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.runtime.executor;

import lombok.Getter;
import me.waliedyassen.runescript.runtime.ScriptRuntime;
import me.waliedyassen.runescript.runtime.ScriptRuntimePool;
import me.waliedyassen.runescript.runtime.ScriptRuntimeSetup;
import me.waliedyassen.runescript.runtime.cache.ScriptCache;
import me.waliedyassen.runescript.runtime.executor.instruction.InstructionExecutorMap;
import me.waliedyassen.runescript.runtime.script.Script;

import java.util.function.Function;

/**
 * A script executor, it is responsible for executing individual scripts.
 *
 * @author Walied K. Yassen
 */
public final class ScriptExecutor<R extends ScriptRuntime> {

    /**
     * The {@link ScriptRuntime} objects pool of the executor.
     */
    @Getter
    private final ScriptRuntimePool<R> pool;

    /**
     * The instruction executors map of the executor.
     */
    private final InstructionExecutorMap executorMap;

    /**
     * Constructs a new {@link ScriptExecutor} type object instance.
     *
     * @param poolSize        the runtime objects pool maximum size.
     * @param cache           the cache which is responsible for loading scripts.
     * @param runtimeSupplier the supplier of the runtime objects.
     * @param executorMap     the instructions executor map of the executor.
     */
    public ScriptExecutor(int poolSize, ScriptCache cache, Function<ScriptRuntimePool<R>, R> runtimeSupplier, InstructionExecutorMap executorMap) {
        this.executorMap = executorMap;
        pool = new ScriptRuntimePool<>(runtimeSupplier, cache, poolSize);
    }

    /**
     * Executes the specified {@link Script} in a new {@link ScriptRuntime runtime}.
     *
     * @param setup  the runtime setup.
     * @param script the script which we want to execute.
     * @return the runtime to store if the execution was aborted.
     * @throws ExecutionException if anything occurs during the execution.
     */
    public R execute(ScriptRuntimeSetup<R> setup, Script script) throws ExecutionException {
        try {
            var runtime = pool.pop();
            setup.setup(runtime);
            if (execute(runtime, script) != null) {
                return runtime;
            } else {
                runtime.close();
            }
            return null;
        } catch (ExecutionException e) {
            throw e;
        } catch (Throwable e) {
            throw new ExecutionException("Error executing script: " + script.getName(), e);
        }
    }

    /**
     * Executes the specified {@link Script script} in the given {@link ScriptRuntime runtime}.
     *
     * @param runtime the runtime to execute the script in.
     * @param script  the script which we want to execute.
     * @return the runtime to store if the execution was aborted.
     * @throws ExecutionException if anything occurs during the execution.
     */
    @SuppressWarnings("unchecked")
    public R execute(R runtime, Script script) throws ExecutionException {
        // Update the runtime script.
        runtime.setScript(script);
        return resume(runtime);
    }

    /**
     * Resumes the execution of the specified {@code runtime.}
     *
     * @param runtime the runtime to resume the execute for.
     * @return R the runtime to store if the execution was aborted.
     */
    public R resume(R runtime) {
        runtime.setAbort(false);
        while (true) {
            var script = runtime.getScript();
            if (runtime.getAddress() >= script.getInstructions().length) {
                break;
            }
            var opcode = script.getInstructions()[runtime.getAddress()];
            var executor = executorMap.lookup(opcode);
            if (executor == null) {
                throw new ExecutionException("Missing InstructionExecutor for instruction with opcode: " + opcode);
            }
            executor.execute(runtime);
            runtime.setAddress(runtime.getAddress() + 1);
            if (runtime.isAbort()) {
                return runtime;
            }
        }
        return null;
    }
}

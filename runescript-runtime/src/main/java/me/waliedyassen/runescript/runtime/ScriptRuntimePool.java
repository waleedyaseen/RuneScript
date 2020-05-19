/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.runtime;

import lombok.RequiredArgsConstructor;

import java.util.Stack;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a pool of {@link ScriptRuntime} objects with a limited size.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class ScriptRuntimePool<R extends ScriptRuntime> {

    /**
     * A stack of the {@link ScriptRuntime} objects that are ready to be used.
     */
    private final Stack<R> runtimes = new Stack<>();

    /**
     * The supplier of the script runtime.
     */
    private final Function<ScriptRuntimePool<R>, R> supplier;

    /**
     * The maximum amount of {@link ScriptRuntime} we can store in the pool.
     */
    private final int limit;

    /**
     * Attempts to pop an free existing {@link ScriptRuntime} from the pool stack, if none was available, it will create
     * a new {@link ScriptRuntime} object and return it.
     *
     * @return the popped or created {@link ScriptRuntime} object.
     */
    public R pop() {
        if (runtimes.isEmpty()) {
            return supplier.apply(this);
        }
        var runtime = runtimes.pop();
        runtime.reset();
        return runtime;
    }

    /**
     * Attempts to push the specified {@link ScriptRuntime} object back into the pool stack, if the current pool stack
     * size exceeds the {@link #limit} nothing will happen.
     *
     * @param runtime the runtime object we want to push back into the pool stack.
     */
    public void push(R runtime) {
        if (runtimes.size() >= limit) {
            return;
        }
        runtimes.push(runtime);
    }
}
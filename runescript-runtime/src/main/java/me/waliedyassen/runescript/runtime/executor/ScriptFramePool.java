/*
 * Copyright (c) 2021 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.runtime.executor;

import me.waliedyassen.runescript.runtime.ScriptFrame;

import java.util.Stack;

/**
 * Represents a pool for {@link ScriptFrame} objects.
 *
 * @author Walied K. Yassen
 */
public final class ScriptFramePool {

    /**
     * The maximum amount of objects that can be pushed onto the pool stack.
     */
    private static final int POOL_SIZE = 512;

    /**
     * The stack which contains the pool objects.
     */
    private static final Stack<ScriptFrame> frames = new Stack<>();

    /**
     * Pushes the specified {@link ScriptFrame} object back into the pool.
     *
     * @param frame the frame object to  push back into the pool.
     */
    public static void push(ScriptFrame frame) {
        if (frames.size() >= POOL_SIZE) {
            return;
        }
        frames.push(frame);
    }

    /**
     * Pops a {@link ScriptFrame} object from the pool or create new one if the pool had no objects available.
     *
     * @return the {@link ScriptFrame} object.
     */
    public static ScriptFrame pop() {
        if (frames.isEmpty()) {
            return new ScriptFrame();
        }
        return frames.pop();
    }

    private ScriptFramePool() {
        // NOOP
    }
}

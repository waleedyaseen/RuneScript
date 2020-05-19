/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.runtime;

/**
 * An interface which is responsible for setting up the runtime context.
 *
 * @param <R> the type of the runtime.
 * @author Walied K. Yassen
 */
@FunctionalInterface
public interface ScriptRuntimeSetup<R extends ScriptRuntime> {

    /**
     * Sets-up the context of the specified {@link R runtime}.
     *
     * @param runtime the runetime to setup the context.
     */
    void setup(R runtime);
}

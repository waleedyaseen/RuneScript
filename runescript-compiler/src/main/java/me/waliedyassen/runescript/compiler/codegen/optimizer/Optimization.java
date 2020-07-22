/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.optimizer;

import me.waliedyassen.runescript.compiler.codegen.script.Script;

/**
 * Represents a script optimization that is applied after the code generation and before the code writing.
 *
 * @author Walied K. Yassen
 */
public abstract class Optimization {

    /**
     * Runs the optimization on the specified {@link Script script}.
     *
     * @param optimizer
     *         the optimizer which is running this optimization.
     * @param script
     *         the script to run the optimization on.
     *
     * @return the amount of units that has been optimized.
     */
    public abstract int run(Optimizer optimizer, Script script);

    /**
     * Cleans-up the state from the current run.
     *
     * @param optimizer
     *         the optimizer which is running this optimizaiton.
     * @param script
     *         the script we ran the optimization on.
     */
    public abstract void clean(Optimizer optimizer, Script script);
}

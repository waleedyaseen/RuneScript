/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen;

import lombok.var;
import me.waliedyassen.runescript.compiler.codegen.block.Label;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents the code generator label factory.
 *
 * @author Walied K. Yassen.
 */
public final class LabelGenerator {

    /**
     * The unique name counter.
     */
    private final Map<String, AtomicInteger> counters = new HashMap<>();

    /**
     * The unique shared counter.
     */
    private final AtomicInteger shared = new AtomicInteger();

    /**
     * Generates a new unique {@link Label} object.
     *
     * @param name
     *         the name of the label.
     *
     * @return the created {@link Label} object instance.
     */
    public Label generate(String name) {
        var counter = counters.get(name);
        if (counter == null) {
            counters.put(name, counter = new AtomicInteger());
        }
        return new Label(shared.getAndIncrement(), name + "_" + counter.getAndIncrement());
    }

    /**
     * Resets the state of this label generator.
     */
    public void reset() {
        counters.clear();
        shared.set(0);
    }
}

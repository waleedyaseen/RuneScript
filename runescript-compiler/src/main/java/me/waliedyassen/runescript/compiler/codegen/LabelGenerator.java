/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen;

import me.waliedyassen.runescript.compiler.codegen.asm.Label;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents the code generator label factory.
 *
 * @author Walied K. Yassen.
 */
public final class LabelGenerator {

    /**
     * The unique id counter.
     */
    private AtomicInteger counter = new AtomicInteger();

    /**
     * Generates a new unique {@link Label} object.
     *
     * @return the created {@link Label} object instance.
     */
    public Label generate() {
        return new Label(counter.getAndIncrement());
    }

    /**
     * Resets the state of this label generator.
     */
    public void reset() {
        counter.set(0);
    }
}

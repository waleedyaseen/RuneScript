/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.runtime.script;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A runtime script, holds all the data and the information we need to execute the script.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class Script {

    /**
     * The name of the script.
     */
    @Getter
    private final String name;

    /**
     * The instructions of the script.
     */
    @Getter
    private final int[] instructions;

    /**
     * The integer operands of the script.
     */
    @Getter
    private final Object[] operands;
}

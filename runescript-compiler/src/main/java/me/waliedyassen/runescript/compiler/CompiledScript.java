/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.symbol.impl.script.ScriptInfo;

/**
 * Represents a single compiled bytecode script.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class CompiledScript {

    /**
     * The name of the script.
     */
    @Getter
    private final String name;

    // TODO: Add typing to the output.

    /**
     * The compiled bytecode data of the script.
     */
    @Getter
    private final Object output;

    /**
     * The info of the script that was compiled.
     */
    @Getter
    private final ScriptInfo info;
}

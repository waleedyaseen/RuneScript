/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.asm;

import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.type.Type;

/**
 * Represents a local variable/parameter.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class Local {

    /**
     * The local variable type.
     */
    private final Type type;
}

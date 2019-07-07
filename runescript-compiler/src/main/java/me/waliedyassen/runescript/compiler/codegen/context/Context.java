/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.context;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.waliedyassen.runescript.compiler.codegen.asm.Block;

/**
 * Represents a code generation context.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public class Context {

    /**
     * The type of this context.
     */
    @Getter
    private final @NonNull ContextType type;

    /**
     * The current working block.
     */
    @Getter @Setter
    private Block block;
}
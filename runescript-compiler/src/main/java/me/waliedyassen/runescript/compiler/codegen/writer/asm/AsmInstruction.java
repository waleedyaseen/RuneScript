/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.writer.asm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents an assembly instruction.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class AsmInstruction {

    /**
     *
     */
    @Getter
    private final int opcode;

    /**
     *
     */
    @Getter
    private final boolean large;

    /**
     *
     */
    @Getter
    private final Object operand;
}


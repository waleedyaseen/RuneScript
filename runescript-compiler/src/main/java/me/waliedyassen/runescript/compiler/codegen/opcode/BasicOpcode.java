/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.opcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A basic implementation of the {@link Opcode} interface.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class BasicOpcode implements Opcode {

    /**
     * The code (or id) of the opcode.
     */
    @Getter
    private final int code;

    /**
     * Whether or not the opcode is large.
     */
    @Getter
    private final boolean large;
}

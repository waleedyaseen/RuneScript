/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class InstructionMap {

    /**
     *
     */
    @Getter
    private final int pushConstantInt;

    /**
     *
     */
    @Getter
    private final int pushConstantString;

    /**
     *
     */
    @Getter
    private final int pushConstantLong;
    /**
     *
     */
    @Getter
    private final int pushLocalInt;

    /**
     *
     */
    @Getter
    private final int pushLocalString;

    /**
     *
     */
    @Getter
    private final int pushLocalLong;

    /**
     *
     */
    @Getter
    private final int joinString;
}

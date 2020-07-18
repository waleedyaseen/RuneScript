/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.symbol.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.symbol.Symbol;
import me.waliedyassen.runescript.type.PrimitiveType;

/**
 * Represents a local array symbol information.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class ArrayInfo extends Symbol {

    /**
     * The index of the array.
     */
    @Getter
    private final int index;

    /**
     * The name of the array.
     */
    @Getter
    private final String name;

    /**
     * The type of the array.
     */
    @Getter
    private final PrimitiveType type;
}

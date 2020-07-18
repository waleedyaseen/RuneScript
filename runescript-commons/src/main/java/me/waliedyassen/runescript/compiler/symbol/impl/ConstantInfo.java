/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.symbol.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.symbol.Symbol;
import me.waliedyassen.runescript.type.Type;

/**
 * Represents a compile-time constant symbol information.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class ConstantInfo extends Symbol {

    /**
     * The constant name.
     */
    @Getter
    private final String name;

    /**
     * The constant type.
     */
    @Getter
    private final Type type;

    /**
     * The constant value.
     */
    @Getter
    private final Object value;
}

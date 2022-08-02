/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
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
import me.waliedyassen.runescript.type.primitive.PrimitiveType;

/**
 * The symbol information for a runtime constant value, which is a constant that is replaced at runtime with another value.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public final class RuntimeConstantInfo extends Symbol {

    /**
     * The name of the constant.
     */
    private final String name;

    private final int id;

    /**
     * The type of the constant.
     */
    private final PrimitiveType type;

    /**
     * The value of the constant.
     */
    private final Object value;
}

/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.symbol.impl.variable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.symbol.Symbol;
import me.waliedyassen.runescript.type.Type;

/**
 * Represents a declared local variable information.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class VariableInfo extends Symbol {

    /**
     * The domain of the variable.
     */
    @Getter
    private final VariableDomain domain;

    /**
     * The name of the variable.
     */
    @Getter
    private final String name;

    /**
     * The type of the variable.
     */
    @Getter
    private final Type type;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return (domain == VariableDomain.LOCAL ? "$" : "%") + name;
    }
}

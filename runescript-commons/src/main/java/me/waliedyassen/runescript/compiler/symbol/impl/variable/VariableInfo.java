/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.symbol.impl.variable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.waliedyassen.runescript.compiler.symbol.impl.ConfigInfo;
import me.waliedyassen.runescript.type.Type;

/**
 * Represents a declared local variable information.
 *
 * @author Walied K. Yassen
 */
@EqualsAndHashCode(callSuper = true)
public final class VariableInfo extends ConfigInfo {

    /**
     * The domain of the variable.
     */
    @Getter
    private final VariableDomain domain;

    /**
     * Constructs a new {@link VariableInfo} type object instance.
     *
     * @param name
     *         the name of the variable.
     * @param type
     *         the type of the variable.
     * @param contentType
     *         the content type of the variable.
     * @param domain
     *         the domain of the variable.
     */
    public VariableInfo(String name, Type type, Type contentType, VariableDomain domain) {
        super(name, type, contentType);
        this.domain = domain;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return (getDomain() == VariableDomain.LOCAL ? "$" : "%") + getName();
    }
}

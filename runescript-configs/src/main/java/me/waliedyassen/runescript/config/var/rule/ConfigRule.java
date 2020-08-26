/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.var.rule;

import me.waliedyassen.runescript.config.syntax.ConfigSyntax;
import me.waliedyassen.runescript.config.syntax.PropertySyntax;
import me.waliedyassen.runescript.config.syntax.value.ValueSyntax;
import me.waliedyassen.runescript.config.syntax.value.ValueConstantSyntax;
import me.waliedyassen.runescript.config.syntax.value.ValueIntegerSyntax;
import me.waliedyassen.runescript.config.semantics.typecheck.TypeChecking;

/**
 * A configuration rule that can be used for testing values.
 *
 * @author Walied K. Yassen
 */
public interface ConfigRule {

    /**
     * Tests the configuration value against this configuration rule.
     *  @param checking
     *         the type checker that we are using for the configuration.
     * @param config
     * @param property
     *         the property which the value lies in.
     * @param value
     */
    void test(TypeChecking checking, ConfigSyntax config, PropertySyntax property, ValueSyntax value);

    /**
     * Resolves the specified {@link ValueSyntax} to an integer.
     *
     * @param checking
     *         the type checking of the configuration.
     * @param value
     *         the value that we are checking.
     *
     * @return the integer value.
     *
     * @throws IllegalStateException
     *         if the specified value cannot be resolved to an integer.
     */
    static Integer resolveInteger(TypeChecking checking, ValueSyntax value) {
        if (value instanceof ValueIntegerSyntax) {
            return ((ValueIntegerSyntax) value).getValue();
        } else if (value instanceof ValueConstantSyntax) {
            return (Integer) checking.getTable().lookupConstant(((ValueConstantSyntax) value).getName().getText()).getValue();
        } else {
            throw new IllegalStateException("Unrecognised value type: " + value);
        }
    }
}

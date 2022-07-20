/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.var.rule.impl;

import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.config.syntax.ConfigSyntax;
import me.waliedyassen.runescript.config.syntax.PropertySyntax;
import me.waliedyassen.runescript.config.syntax.value.ValueSyntax;
import me.waliedyassen.runescript.config.semantics.SemanticError;
import me.waliedyassen.runescript.config.semantics.typecheck.TypeChecking;
import me.waliedyassen.runescript.config.var.rule.ConfigRule;

/**
 * A rule which checks if an integer is between a specific range inclusively.
 *
 * @author Walied K. yassen
 */
@RequiredArgsConstructor
public final class ConfigRangeRule implements ConfigRule {

    /**
     * The minimum value of the range.
     */
    private final int minimum;

    /**
     * The maximum value of the range.
     */
    private final int maximum;

    /**
     * {@inheritDoc}
     */
    @Override
    public void test(TypeChecking checking, ConfigSyntax config, PropertySyntax property, ValueSyntax value) {
        var integer = ConfigRule.resolveInteger(checking, value);
        if (integer < minimum || integer > maximum) {
            checking.getChecker().reportError(new SemanticError(value, "Value of this component must be in range [" + minimum + "," + maximum + "]"));
        }
    }
}

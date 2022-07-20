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
import me.waliedyassen.runescript.config.semantics.SemanticError;
import me.waliedyassen.runescript.config.semantics.typecheck.TypeChecking;

/**
 * An enum of all configuration rules that require no parameter.
 *
 * @author Walied K. Yassen
 */
public enum ConfigRules implements ConfigRule {

    /**
     * A rule that only allow positive integer values.
     */
    POSITIVE_ONLY {
        /**
         * {@inheritDoc}
         */
        @Override
        public void test(TypeChecking checking, ConfigSyntax config, PropertySyntax property, ValueSyntax value) {
            var integer = ConfigRule.resolveInteger(checking, value);
            if (integer < 1) {
                checking.getChecker().reportError(new SemanticError(value, "Expected a positive value for this component"));
            }
        }
    },

    /**
     * Emits the property with empty operands if the value is false.
     */
    EMIT_EMPTY_IF_FALSE,

    /**
     * Emits the property with empty operands if the value is true.
     */
    EMIT_EMPTY_IF_TRUE;

    /**
     * {@inheritDoc}
     */
    @Override
    public void test(TypeChecking checking, ConfigSyntax config, PropertySyntax property, ValueSyntax value) {
        // NOOP
    }
}
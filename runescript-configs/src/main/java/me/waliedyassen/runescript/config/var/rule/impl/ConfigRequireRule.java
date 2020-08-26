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
 * A configuration rule which ensures a property with a specific name is present.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class ConfigRequireRule implements ConfigRule {

    /**
     * The required sibling properties.
     */
    private final String name;

    /**
     * {@inheritDoc}
     */
    @Override
    public void test(TypeChecking checking, ConfigSyntax config, PropertySyntax property, ValueSyntax value) {
        if (config.findProperty(name) == null) {
            checking.getChecker().reportError(new SemanticError(property, String.format("Property '%s' requires property '%s'", property.getKey().getText(), name)));
        }
    }
}

/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.symbol.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.waliedyassen.runescript.compiler.symbol.Symbol;
import me.waliedyassen.runescript.config.ConfigGroup;

/**
 * Represents a configuration symbol in the symbols table.
 *
 * @author Walied K. Yassen
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class ConfigInfo extends Symbol {

    /**
     * The name of the configuration.
     */
    private final String name;

    /**
     * The group of the configuration.
     */
    private final ConfigGroup group;
}

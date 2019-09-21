/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.symbol;

import me.waliedyassen.runescript.config.ConfigGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the configurations compiler symbols table.
 *
 * @author Walied K. Yassen
 */
public final class SymbolTable {

    /**
     * The configuration groups defined
     */
    private final Map<ConfigGroup, Map<String, ConfigInfo>> configs = new HashMap<>();

    /**
     * Looks-up for the {@link ConfigInfo} with the specified {@link ConfigGroup group} and {@code name}.
     *
     * @param group
     *         the group of the configuration.
     * @param name
     *         the name of the configuration.
     *
     * @return the {@link ConfigInfo} object if it was present otherwise {@code null}.
     */
    public ConfigInfo lookupConfig(ConfigGroup group, String name) {
        return lookupConfigs(group).get(name);
    }

    /**
     * Looks-up for the specified configurations map with the specified {@link ConfigGroup}.
     *
     * @param group
     *         the group of the configurations.
     *
     * @return the {@link Map} object of the specified group.
     */
    public Map<String, ConfigInfo> lookupConfigs(ConfigGroup group) {
        var map = configs.get(group);
        if (map == null) {
            configs.put(group, map = new HashMap<>());
        }
        return map;
    }

    /**
     * Defines a {@link ConfigInfo} in the symbol table.
     *
     * @param group
     *         the group of the configuration.
     * @param name
     *         the name of the configuration.
     */
    public void defineConfig(ConfigGroup group, String name) {
        var map = lookupConfigs(group);
        map.put(name, new ConfigInfo(name, group));
    }
}

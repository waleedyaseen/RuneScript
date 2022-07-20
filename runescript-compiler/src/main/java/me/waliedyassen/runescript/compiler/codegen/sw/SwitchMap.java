/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.sw;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a code generator switch tables map.
 *
 * @author Walied K. Yassen
 */
public final class SwitchMap {

    /**
     * The registered switch tables.
     */
    @Getter
    private final Map<Integer, SwitchTable> tables = new HashMap<>();


    /**
     * Generates a new {@link SwitchTable} object.
     *
     * @param cases
     *         the cases of the switch table.
     *
     * @return the generated {@link SwitchTable} object.
     */
    public SwitchTable generateTable(SwitchCase[] cases) {
        var switch_table = new SwitchTable(tables.size(), cases);
        tables.put(switch_table.getId(), switch_table);
        return switch_table;
    }

    /**
     * Resets the switch table.
     */
    public void reset() {
        tables.clear();
    }
}

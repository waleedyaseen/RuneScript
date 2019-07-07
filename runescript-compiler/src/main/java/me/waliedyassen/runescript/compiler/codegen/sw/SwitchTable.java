/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.sw;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents a generated switch table.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class SwitchTable {

    /**
     * The id of the switch table.
     */
    @Getter
    private final int id;

    /**
     * The cases that are within this switch table.
     */
    @Getter
    private final SwitchCase[] cases;


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "switch_" + id;
    }
}

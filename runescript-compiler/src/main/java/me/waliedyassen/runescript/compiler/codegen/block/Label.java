/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.block;

import lombok.Data;
import lombok.Getter;

/**
 * Represents a code label, used for branching and jumping to targets.
 *
 * @author Walied K. Yassen
 */
@Data
public final class Label {

    /**
     * The id of the label.
     */
    @Getter
    private final int id;

    /**
     * The name of the label.
     */
    @Getter
    private final String name;

    /**
     * Checks hwether or not this label is the entry label.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public boolean isEntryLabel() {
        // We do it this way because the entry label always has an id of 0 and it is way faster than comparing strings.
        return id == 0;
    }
}

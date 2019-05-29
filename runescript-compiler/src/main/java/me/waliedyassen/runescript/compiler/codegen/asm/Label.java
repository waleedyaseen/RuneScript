/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.asm;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents a code label, used for branching and jumping to targets.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
@EqualsAndHashCode
public final class Label {

    /**
     * The name format of the label.
     */
    private final String NAME_FORMAT = "label_%d";

    /**
     * The id of the label.
     */
    @Getter
    private final int id;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format(NAME_FORMAT, id);
    }
}

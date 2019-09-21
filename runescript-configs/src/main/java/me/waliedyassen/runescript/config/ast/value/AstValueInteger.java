/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.ast.value;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;

/**
 * Represents a configuration property integer value node.
 *
 * @author Walied K. Yassen
 */
public final class AstValueInteger extends AstValue {

    /**
     * The integer value.
     */
    @Getter
    private final int value;

    /**
     * Constructs a new {@link AstValueInteger} type object instance.
     *
     * @param range
     *         the node source range.
     * @param value
     *         the integer value.
     */
    public AstValueInteger(Range range, int value) {
        super(range);
        this.value = value;
    }
}

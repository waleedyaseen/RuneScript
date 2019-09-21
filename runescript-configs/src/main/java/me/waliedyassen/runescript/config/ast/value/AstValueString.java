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
 * Represents a configuration property string value node.
 *
 * @author Walied K. Yassen
 */
public final class AstValueString extends AstValue {

    /**
     * The string value text content.
     */
    @Getter
    private final String text;

    /**
     * Constructs a new {@link AstValueString} type object instance.
     *
     * @param range
     *         the node source range.
     * @param text
     *         the string value text content.
     */
    public AstValueString(Range range, String text) {
        super(range);
        this.text = text;
    }
}

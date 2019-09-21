/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.ast;

import me.waliedyassen.runescript.commons.document.Range;

/**
 * Represents a complete configuration tree node.
 *
 * @author Walied K. Yassen
 */
public final class AstConfig extends AstNode {

    /**
     * Constructs a new {@link AstConfig} type object instance.
     *
     * @param range
     *         the node source code range.
     */
    public AstConfig(Range range) {
        super(range);
    }
}

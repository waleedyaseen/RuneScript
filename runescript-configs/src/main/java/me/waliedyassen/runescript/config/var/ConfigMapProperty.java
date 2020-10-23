/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.var;

import lombok.Data;
import me.waliedyassen.runescript.type.primitive.PrimitiveType;

/**
 * A single map configuration property.
 *
 * @author Walied K. Yassen
 */
@Data
public final class ConfigMapProperty implements ConfigProperty {

    /**
     * The name of the property.
     */
    private final String name;

    /**
     * The opcodes of the property.
     */
    private final int[] opcodes;

    /**
     * The key type property name of the property.
     */
    private final String keyTypeProperty;

    /**
     * The value type property name of the property.
     */
    private final String valueTypeProperty;

    /**
     * {@inheritDoc}
     */
    @Override
    public PrimitiveType[] getComponents() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAllowDuplicates() {
        return true;
    }
}

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.var;

import lombok.Data;
import me.waliedyassen.runescript.type.PrimitiveType;

/**
 * A configuration param property.
 *
 * @author Walied K. Yassen
 */
@Data
public final class ConfigParamProperty implements ConfigProperty {

    /**
     * The name of the param property.
     */
    private final String name;

    /**
     * The code of the param property.
     */
    private final int code;

    /**
     * {@inheritDoc}
     */
    @Override
    public PrimitiveType[] getComponents() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAllowDuplicates() {
        return true;
    }
}

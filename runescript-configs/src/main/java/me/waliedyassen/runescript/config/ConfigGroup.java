/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config;

import me.waliedyassen.runescript.type.PrimitiveType;

/**
 * Represents a configuration group.
 *
 * @author Walied K. Yassen
 */
public interface ConfigGroup {
    /**
     * Gets the configuration group {@link PrimitiveType type}.
     *
     * @return the configuration group {@link PrimitiveType type}.
     */
    PrimitiveType getType();
}

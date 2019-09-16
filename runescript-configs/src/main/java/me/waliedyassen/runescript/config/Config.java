/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents RuneScript type configuration.
 *
 * @author Walied K. Yassen
 */
public abstract class Config {
    /**
     * The type configuration name.
     */
    @Getter @Setter
    protected String configName;
}

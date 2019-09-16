/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.annotation;

/**
 * An annotation to specify the configuration array binding entry properties.
 *
 * @author Walied K. Yassen
 */
public @interface ConfigArray {
    /**
     * The size of ths configuration array binding entry.
     *
     * @return the size of the configuration array binding entry.
     */
    int size();
}

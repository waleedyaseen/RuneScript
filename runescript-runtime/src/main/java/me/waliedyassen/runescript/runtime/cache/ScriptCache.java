/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.runtime.cache;

import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.runtime.script.Script;

/**
 * Represents a script cache that can be used for loading and storing the loaded scripts in memory for faster access.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public abstract class ScriptCache {

    /**
     * Returns the {@link Script} object with the specified {@code name}.
     *
     * @param id the id of the script to get.
     * @return the {@link Script} object if found otherwise {@code null}.
     */
    public abstract Script get(int id);

    /**
     * Returns the {@link Script} object with the specified {@code name}.
     *
     * @param name the name of the script to get.
     * @return the {@link Script} object if found otherwise {@code null}.
     */
    public abstract Script get(String name);
}

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.pack.provider;

import me.waliedyassen.runescript.editor.pack.Pack;

/**
 * A provider interface for {@link Pack} objects which requires an extension to be provided for each {@link Pack}.
 *
 * @author Walied K. Yassen
 */
public interface PackProvider {

    /**
     * Creates a new {@link Pack} object for the specified {@code packName}.
     *
     * @param packName the pack database name to create the {@link Pack} object for.
     * @return the created {@link Pack} object or {@code null} if we failed to open one.
     */
    Pack create(String packName);
}

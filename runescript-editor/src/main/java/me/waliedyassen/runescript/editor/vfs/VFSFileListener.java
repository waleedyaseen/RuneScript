/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.vfs;

import java.nio.file.Path;

/**
 * Represents a listener that can be found to a {@link VFSFile} and listen to it's events.
 *
 * @author Walied K. Yassen
 */
public interface VFSFileListener {

    /**
     * Gets called when an entity was just created.
     *
     * @param path the path of the entity that was created.
     */
    void onEntityCreate(Path path);

    /**
     * Gets called when an entity was just deleted.
     *
     * @param path the path of the entity that was deleted.
     */
    void onEntityDelete(Path path);
}


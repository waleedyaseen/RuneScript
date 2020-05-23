/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package me.waliedyassen.runescript.editor.vfs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents a single file or directory in the virtual file system.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class VFSFile {

    /**
     * The owner virtual file system of this file.
     */
    private final VFS vfs;

    /**
     * The path which leads to the file.
     */
    private final Path path;

    /**
     * A list of all the listeners that are subscribed to this virtual file.
     */
    @Getter
    private List<VFSFileListener> listeners;

    /**
     * Whether or not this file has already subscribed to the watcher service or not.
     */
    private boolean subscribedToWatcher;

    /**
     * Adds a new listener to this virtual file listeners list.
     *
     * @param listener the listener to add to the virtual file listeners.
     */
    public void addListener(VFSFileListener listener) {
        if (listeners == null) {
            listeners = new CopyOnWriteArrayList<>();
        }
        listeners.add(listener);
        if (!subscribedToWatcher) {
            vfs.getWatcher().subscribe(path);
            subscribedToWatcher = true;
        }
    }

    /**
     * Removes the specified {@link VFSFileListener} from the listeners list.
     *
     * @param listener the listener to remove from the listeners list.
     */
    public void removeListener(VFSFileListener listener) {
        if (listeners == null) {
            return;
        }
        listeners.remove(listener);
        if (listeners.isEmpty()) {
            listeners = null;
        }
    }
}

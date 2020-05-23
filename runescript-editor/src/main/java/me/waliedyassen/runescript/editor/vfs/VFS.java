/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.vfs;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

/**
 * Represents a virtual file system manager.
 *
 * @author Walied K. Yassen
 */
@Slf4j
public final class VFS {

    /**
     * The root directory of the virtual file system.
     */
    private final Path root;

    /**
     * The {@link VFSWatcher watcher} object of the file system.
     */
    private final VFSWatcher watcher;

    /**
     * Constructs a new {@link VFS} type object instance.
     *
     * @param root the root of the virtual file system.
     */
    @SneakyThrows
    public VFS(Path root) {
        this.root = root;
        watcher = new VFSWatcher(root);
    }

    /**
     * Closes the virtual file system.
     */
    public void close() {
        watcher.close();
    }
}

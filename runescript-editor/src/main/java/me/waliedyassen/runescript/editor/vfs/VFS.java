/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.vfs;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import me.waliedyassen.runescript.editor.util.ex.PathEx;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a virtual file system manager.
 *
 * @author Walied K. Yassen
 */
@Slf4j
public final class VFS {

    /**
     * A map of all the files that are currently alive in the virtual file system.
     */
    private final Map<String, VFSFile> filesByRelativePath = new HashMap<>();

    /**
     * The root directory of the virtual file system.
     */
    @Getter
    private final Path rootPath;

    /**
     * The {@link VFSWatcher watcher} object of the file system.
     */
    @Getter
    private final VFSWatcher watcher;

    /**
     * Constructs a new {@link VFS} type object instance.
     *
     * @param rootPath the root of the virtual file system.
     */
    @SneakyThrows
    public VFS(Path rootPath) {
        this.rootPath = rootPath.toAbsolutePath();
        log.info("Root path is set to: {}", rootPath);
        watcher = new VFSWatcher(this);
        filesByRelativePath.put(normalizeToKey(rootPath), new VFSFile(this, rootPath));
    }

    /**
     * Returns the virtual file with the specified {@link Path path}.
     *
     * @param path the path of the virtual file that we want to return.
     * @return the {@link VFSFile} object or {@code null} if it was not found.
     */
    public VFSFile getFile(Path path) {
        return filesByRelativePath.get(normalizeToKey(path));
    }

    /**
     * Removes the virtual file with the specified {@link Path path}.
     *
     * @param path the path of the virtual file that we want to remove.
     */
    public void removeFile(Path path) {
        var key = normalizeToKey(path);
        filesByRelativePath.remove(key);
    }

    /**
     * Returns the virtual file of the specified {@link Path path}.
     *
     * @param path the virtual file of the specified path.
     * @return the {@link VFSFile} object or {@code null} if it was not found.
     */
    public VFSFile resolveFile(Path path) {
        var file = getFile(path);
        if (file == null && Files.exists(path)) {
            var parent = resolveFile(path.getParent());
            if (parent == null) {
                throw new IllegalStateException("The specified file path does not belong to this virtual file system");
            }
            filesByRelativePath.put(normalizeToKey(path), file = new VFSFile(this, path));
        }
        return file;
    }

    /**
     * Closes the virtual file system.
     */
    public void close() {
        watcher.close();
    }

    /**
     * Normalizes the specified {@link Path} to a key string.
     *
     * @param path the path to normalize to a key string.
     * @return the normalized string form of the path.
     */
    private String normalizeToKey(Path path) {
        return PathEx.normalizeRelative(rootPath, path);
    }
}

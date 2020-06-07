/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.vfs;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import me.waliedyassen.runescript.editor.job.WorkExecutor;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Represents a change watcher (modify, create, delete) of a virtual file system.
 *
 * @author Walied K. Yassen
 */
@Slf4j
public final class VFSWatcher {

    /**
     * The virtual fiel system which this watcher is for.
     */
    private final VFS vfs;

    /**
     * The watch service of the VFS watcher.
     */
    private final WatchService service;

    /**
     * The future for the watch scheduled polling task.
     */
    private final ScheduledFuture<?> future;

    /**
     * Constructs a new {@link VFSWatcher} type object instance.
     *
     * @param vfs the virtual file system this watcher is for.
     * @throws IOException if anything occurs while creating the watch service.
     */
    public VFSWatcher(VFS vfs) throws IOException {
        this.vfs = vfs;
        service = FileSystems.getDefault().newWatchService();
        future = WorkExecutor.getSingleThreadScheduler().scheduleAtFixedRate(this::performUpdate, 500, 500, TimeUnit.MILLISECONDS);
    }

    /**
     * Polls all of the events of the watch service and process them.
     */
    private void performUpdate() {
        do {
            var watchKey = service.poll();
            if (watchKey == null) {
                break;
            }
            try {
                var parent = (Path) watchKey.watchable();
                var parentFile = vfs.resolveFile(parent);
                if (parentFile == null || parentFile.getListeners() == null || parentFile.getListeners().isEmpty()) {
                    continue;
                }
                for (var event : watchKey.pollEvents()) {
                    var path = parent.resolve((Path) event.context());
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        parentFile.getListeners().forEach(listener -> listener.onEntityCreate(path));
                    } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                        parentFile.getListeners().forEach(listener -> listener.onEntityDelete(path));
                        vfs.removeFile(path);
                    } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        // NOOP
                    }
                }
            } catch (Throwable e) {
                log.error("An error occurred while performing the events for watch key: {}", watchKey, e);
            } finally {
                watchKey.reset();
            }
        } while (true);
    }


    /**
     * Registers the specified {@link Path} in the watch service.
     *
     * @param path the path to register in the watch service.
     */
    public void subscribe(Path path) {
        try {
            path.register(service, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
        } catch (IOException e) {
            log.error("Failed to register a path in the watch service of a VFS. (path={})", path, e);
        }
    }

    /**
     * Shuts down the VFS watcher and teh scheduled service.
     */
    public void close() {
        try {
            service.close();
            future.cancel(false);
        } catch (IOException e) {
            // NOOP
        }
    }
}

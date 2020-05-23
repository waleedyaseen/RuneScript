/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.vfs;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Represents a change watcher (modify, create, delete) of a virtual file system.
 *
 * @author Walied K. Yassen
 */
public final class VFSWatcher {

    /**
     * The executor service which is responsible for scheduling all of the watchers updating tasks.
     */
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

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
     * @param root the path which leads to the root of the project.
     * @throws IOException if anything occurs while creating the watch service.
     */
    public VFSWatcher(Path root) throws IOException {
        service = FileSystems.getDefault().newWatchService();
        future = executorService.scheduleAtFixedRate(this::performUpdate, 500, 500, TimeUnit.MILLISECONDS);
        root.register(service, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
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
            for (var event : watchKey.pollEvents()) {
                var path = (Path) event.context();
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    System.out.println("Create: " + path.toAbsolutePath());
                } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                    System.out.println("Delete: " + path);
                } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                    System.out.println("Modified: " + path);
                }
            }
            watchKey.reset();
        } while (true);
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

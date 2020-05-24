/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.project.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.CompiledScript;
import me.waliedyassen.runescript.compiler.CompilerErrors;
import me.waliedyassen.runescript.editor.Api;
import me.waliedyassen.runescript.editor.job.WorkExecutor;
import me.waliedyassen.runescript.editor.project.Project;
import me.waliedyassen.runescript.editor.util.ChecksumUtil;
import me.waliedyassen.runescript.editor.util.ex.PathEx;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Represents the cache of a specific project.
 *
 * @author Walied K. Yassen
 */
public final class Cache {

    /**
     * A map of all the cached files in the project.
     */
    @Getter
    private final Map<String, CachedFile> cachedFiles = new HashMap<>();

    /**
     * The project which this cache is for.
     */
    private final Project project;

    /**
     * The save task future of the cache.
     */
    private final ScheduledFuture<?> saveTaskFuture;

    /**
     * Whether or not the cache is currently dirty and needs saving.
     */
    private volatile boolean dirty;

    // TODO: Verify the commands, instructions, and triggers are the same.
    // TODO: Build a dependency tree for all of the scripts.

    /**
     * Constructs a new {@link Cache} type object instance.
     *
     * @param project the project which this cache is for.
     */
    public Cache(Project project) {
        this.project = project;
        saveTaskFuture = WorkExecutor.getSingleThreadScheduler().scheduleWithFixedDelay(this::performSaving, 5000, 5000L, TimeUnit.MILLISECONDS);
    }

    /**
     * Performs the saving task of the cache.
     */
    public void performSaving() {
        if (!dirty) {
            return;
        }
        project.saveCache();
        dirty = false;
    }

    /**
     * Deserialises the cache content form the specified {@link DataInputStream}.
     *
     * @param stream the stream to deserialise the cache content from.
     * @throws IOException if anything occurs while reading data from the specified stream.
     */
    public void read(DataInputStream stream) throws IOException {
        var filesCount = stream.readInt();
        for (var index = 0; index < filesCount; index++) {
            var cachedFile = new CachedFile();
            cachedFile.read(stream);
            cachedFiles.put(cachedFile.getFullPath(), cachedFile);
        }
    }

    /**
     * Serialises the cache content form the specified {@link DataOutputStream}.
     *
     * @param stream the stream to serialise the cache content into.
     * @throws IOException if anything occurs while writing data to the specified stream.
     */
    public void write(DataOutputStream stream) throws IOException {
        stream.writeInt(cachedFiles.size());
        for (var file : cachedFiles.values()) {
            file.write(stream);
        }
    }

    /**
     * Compares this cache against the content of the specified source directory.
     *
     * @param sourceDirectory the source directory to resolve the files from.
     * @throws IOException if anything occurs walking through the path tree or while writing the changes to the disk.
     */
    public void diff(Path sourceDirectory) throws IOException {
        var paths = Files.walk(sourceDirectory).filter(Files::isRegularFile).collect(Collectors.toList());
        var modified = false;
        for (var path : paths) {
            var key = PathEx.normaliseToString(sourceDirectory, path);
            var cachedFile = cachedFiles.get(key);
            if (cachedFile == null) {
                cachedFile = new CachedFile();
                cachedFile.setPath(PathEx.normaliseToString(sourceDirectory, path.getParent()));
                cachedFile.setName(path.getFileName().toString());
                cachedFiles.put(key, cachedFile);
            }
            var diskData = Files.readAllBytes(path);
            var diskCrc = ChecksumUtil.calculateCrc32(diskData);
            if (cachedFile.getCrc() == diskCrc) {
                continue;
            }
            update(cachedFile, diskData);
            cachedFile.setCrc(diskCrc);
            modified = true;
        }
        if (modified) {
            project.saveCache();
        }
    }

    /**
     * Updates the specified cached file errors and contained scripts from the specified {@code content}.
     *
     * @param cachedFile the cached file which we want to update.
     * @param content    the content of the file on the local disk.
     */
    public void update(CachedFile cachedFile, byte[] content) throws IOException {
        cachedFile.getErrors().clear();
        cachedFile.getScripts().clear();
        try {
            var scripts = project.getCompiler().compile(content);
            for (var script : scripts) {
                cachedFile.getScripts().add(script.getInfo());
            }
        } catch (CompilerErrors errors) {
            for (var error : errors.getErrors()) {
                cachedFile.getErrors().add(new CachedFile.CachedError(error.getRange(), error.getMessage()));
            }
        }
    }

    /**
     * Updates the data for the cached file with the specified {@link Path path}.
     *
     * @param path    the path which we want to update the cached file for.
     * @param errors  the errors that we want to update the cached files wi th.
     * @param scripts the scripts that we want to update the cached file with.
     * @param data    the source code data of the cache.
     */
    public void updateData(Path path, CompilerErrors errors, CompiledScript[] scripts, byte[] data) {
        var key = PathEx.normaliseToString(project.getBuildPath().getSourceDirectory(), path);
        var cachedFile = cachedFiles.get(key);
        if (cachedFile == null) {
            cachedFile = new CachedFile();
            cachedFile.setPath(PathEx.normaliseToString(project.getBuildPath().getSourceDirectory(), path.getParent()));
            cachedFile.setName(path.getFileName().toString());
            cachedFiles.put(key, cachedFile);
        }
        cachedFile.setCrc(ChecksumUtil.calculateCrc32(data));
        updateData(cachedFile, errors, scripts);
        dirty = true;
    }

    /**
     * Updates the specified cache file errors and contained scripts.
     *
     * @param cachedFile the cached file which we want to update.
     * @param errors     the errors to update the cached file with.
     * @param scripts    the scripts to update the cached file with.
     */
    private void updateData(CachedFile cachedFile, CompilerErrors errors, CompiledScript[] scripts) {
        var errorsPath = cachedFile.getFullPath();
        cachedFile.getErrors().clear();
        cachedFile.getScripts().clear();
        if (scripts != null) {
            for (var script : scripts) {
                cachedFile.getScripts().add(script.getInfo());
            }
        }
        if (errors != null) {
            for (var error : errors.getErrors()) {
                cachedFile.getErrors().add(new CachedFile.CachedError(error.getRange(), error.getMessage()));
            }
        }
        project.updateErrors(errorsPath);
    }
}

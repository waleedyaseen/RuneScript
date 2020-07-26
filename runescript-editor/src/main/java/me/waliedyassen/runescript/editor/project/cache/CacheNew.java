/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.project.cache;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import me.waliedyassen.runescript.commons.Pair;
import me.waliedyassen.runescript.compiler.Input;
import me.waliedyassen.runescript.compiler.SourceFile;
import me.waliedyassen.runescript.compiler.ast.AstParameter;
import me.waliedyassen.runescript.compiler.ast.expr.AstExpression;
import me.waliedyassen.runescript.compiler.symbol.impl.ConfigInfo;
import me.waliedyassen.runescript.compiler.symbol.impl.script.ScriptInfo;
import me.waliedyassen.runescript.editor.file.FileTypeManager;
import me.waliedyassen.runescript.editor.job.WorkExecutor;
import me.waliedyassen.runescript.editor.project.Project;
import me.waliedyassen.runescript.editor.util.ex.PathEx;
import me.waliedyassen.runescript.type.Type;
import me.waliedyassen.runescript.util.ChecksumUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * A cache system that is for a specific project.
 *
 * @author Walied K. Yassen
 */
@Slf4j
public final class CacheNew {

    /**
     * A map of all the cache units that are stored in this
     */
    @Getter
    private final Map<String, CacheUnit> units = new HashMap<>();

    /**
     * The project which this cache is for.
     */
    private final Project project;

    /**
     * Whether or not the cache file is currently dirty.
     */
    private boolean dirtyCache;

    /**
     * Constructs a new {@link CacheNew} type object instance.
     *
     * @param project
     *         the project which owns this cache.
     */
    public CacheNew(Project project) {
        this.project = project;
        WorkExecutor.getSingleThreadScheduler().scheduleWithFixedDelay(this::performSaving, 0, 1, TimeUnit.MINUTES);
    }

    /**
     * Reads the content of the cache from the specified {@link DataInputStream stream}.
     *
     * @param stream
     *         the stream to read the content of the cache from.
     *
     * @throws IOException
     *         if anything occurs while reading the data from the stream.
     */
    public void deserialize(DataInputStream stream) throws IOException {
        var unitsCount = stream.readInt();
        for (int index = 0; index < unitsCount; index++) {
            var unit = new CacheUnit();
            unit.deserialize(stream, project.getCompilerEnvironment());
            units.put(unit.getNameWithPath(), unit);
        }
    }

    /**
     * Writes the content of the cache to the specified {@link DataOutputStream stream}.
     *
     * @param stream
     *         the stream to write the content of the cache to.
     *
     * @throws IOException
     *         if anything occurs while writing the data to the stream.
     */
    public void serialize(DataOutputStream stream) throws IOException {
        stream.writeInt(units.size());
        for (var file : units.values()) {
            file.write(stream);
        }
    }

    /**
     * Saves all of hte data in the cache to the local disk.
     */
    public void performSaving() {
        if (dirtyCache) {
            log.info("Found dirty cache.. saving cache");
            project.saveCache();
            dirtyCache = false;
        }
    }

    /**
     * Collects all of the changes of the compilable files in the source directory and compiles
     * the affected files.
     *
     * @throws IOException
     *         if anything occurs accessing the files on the local disk.
     */
    public void diff() throws IOException {
        var paths = Files.walk(project.getBuildPath().getSourceDirectory())
                .filter(path -> Files.isRegularFile(path) && FileTypeManager.isCompilable(PathEx.getExtension(path)))
                .collect(Collectors.toList());
        var changes = new ArrayList<Pair<Path, byte[]>>();
        var deletions = new HashMap<>(units);
        for (var path : paths) {
            var normalizedPath = PathEx.normalizeRelative(project.getBuildPath().getSourceDirectory(), path);
            var diskData = Files.readAllBytes(path);
            var unit = units.get(normalizedPath);
            deletions.remove(normalizedPath);
            if (unit != null && ChecksumUtil.calculateCrc32(diskData) == unit.getCrc()) {
                continue;
            }
            changes.add(Pair.of(path, diskData));
        }
        if (!deletions.isEmpty()) {
            log.info("Found {} deleted files", deletions.size());
            for (var deletedUnit : deletions.values()) {
                deletedUnit.undefineSymbols(project.getSymbolTable());
                units.remove(deletedUnit.getPath());
            }
        }
        if (!changes.isEmpty()) {
            log.info("Found {} changed files", changes.size());
            recompile(changes);
        }
    }

    /**
     * Re-compiles the content of the file at the specified {@link Path relative path}.
     *
     * @param path
     *         the path of the file to recompile relative to the source directory of the project.
     */
    @SneakyThrows
    public void recompile(Path path) {
        recompile(path, Files.readAllBytes(path));
    }

    /**
     * Re-compiles the content of the file at the specified {@link Path relative path}.
     *
     * @param path
     *         the relative path of the file that we want to recompile.
     */
    @SneakyThrows
    public void recompile(Path path, byte[] content) {
        recompile(Collections.singletonList(Pair.of(path, content)));
    }

    /**
     * Re-compiles the specified list of files.
     *
     * @param files
     *         the list of files that we want to recompile.
     */
    @SneakyThrows
    private void recompile(List<Pair<Path, byte[]>> files) {
        // TODO: Soft compilation mode that does not recompile all of the dependencies.
        var configInput = new Input();
        var scriptInput = new Input();
        for (var pair : files) {
            var path = pair.getKey();
            var content = pair.getValue();
            var key = PathEx.normalizeRelative(project.getBuildPath().getSourceDirectory(), path);
            var unit = units.get(key);
            if (unit == null) {
                unit = createCacheUnit(path);
            } else {
                unit.undefineSymbols(project.getSymbolTable());
                unit.clear();
            }
            if (unit.isClientScript() || unit.isServerScript()) {
                scriptInput.addSourceFile(SourceFile.of(path, content));
            } else {
                configInput.addSourceFile(SourceFile.of(path, content));
            }
        }
        var dirty = false;
        if (!configInput.getSourceFiles().isEmpty()) {
            var output = project.getConfigsCompiler().compile(configInput);
            for (var entry : output.getFiles().entrySet()) {
                var normalizedPath = PathEx.normalizeRelative(project.getBuildPath().getSourceDirectory(), Paths.get(entry.getKey()));
                var compiledFile = entry.getValue();
                var unit = units.get(normalizedPath);
                unit.setCrc(compiledFile.getCrc());
                for (var compiledUnit : compiledFile.getUnits()) {
                    var info = new ConfigInfo(compiledUnit.getConfig().getName().getText(), compiledUnit.getBinding().getGroup().getType(), compiledUnit.getConfig().getContentType());
                    unit.getConfigs().add(info);
                }
                unit.defineSymbols(project.getSymbolTable());
                for (var error : compiledFile.getErrors()) {
                    unit.getErrors().add(new CachedError(error.getRange(), error.getMessage()));
                }
                project.updateErrors(unit);
            }
            dirty = true;
        }
        if (!scriptInput.getSourceFiles().isEmpty()) {
            var output = project.getScriptsCompiler().compile(scriptInput);
            for (var entry : output.getFiles().entrySet()) {
                var normalizedPath = PathEx.normalizeRelative(project.getBuildPath().getSourceDirectory(), Paths.get(entry.getKey()));
                var compiledFile = entry.getValue();
                var unit = units.get(normalizedPath);
                unit.setCrc(compiledFile.getCrc());
                for (var compiledUnit : compiledFile.getUnits()) {
                    var scriptNode = compiledUnit.getScript();
                    var scriptName = AstExpression.extractNameText(compiledUnit.getScript().getName());
                    var triggerName = compiledUnit.getScript().getTrigger().getText();
                    var info = new ScriptInfo(Collections.emptyMap(), scriptName,
                            project.getCompilerEnvironment().lookupTrigger(triggerName),
                            scriptNode.getType(),
                            Arrays.stream(scriptNode.getParameters()).map(AstParameter::getType).toArray(Type[]::new),
                            null);
                    unit.getScripts().add(info);
                }
                unit.defineSymbols(project.getSymbolTable());
                for (var error : compiledFile.getErrors()) {
                    unit.getErrors().add(new CachedError(error.getRange(), error.getMessage()));
                }
                project.updateErrors(unit);
            }
            dirty = true;
        }
        if (dirty) {
            markCacheDirty();
        }
    }

    /**
     * Attempts to pack all of the files that needs packing in the project.
     *
     * @return <code>true</code> if the pack was successful otherwise <code>false</code>.
     */
    public boolean pack() {
        if (units.values().stream().anyMatch(unit -> unit.getErrors().size() > 0)) {
            return false;
        }
        for (var unit : units.values()) {
            if (unit.getCrc() == unit.getPackCrc()) {
                continue;
            }
            pack(unit);
        }
        return true;
    }

    private void pack(CacheUnit unit) {
        unit.setPackCrc(unit.getCrc());
    }

    /**
     * Creates a new {@link CacheUnit} object for the specified {@link Path relative path}.
     *
     * @param relativePath
     *         the relative path of the cache unit.
     *
     * @return the created {@link CacheUnit} object.
     */
    private CacheUnit createCacheUnit(Path relativePath) {
        var fullPath = PathEx.normalizeRelative(project.getBuildPath().getSourceDirectory(), relativePath);
        var unit = new CacheUnit(fullPath, relativePath.getFileName().toString());
        units.put(fullPath, unit);
        return unit;
    }

    /**
     * Marks the cache file as dirty.
     */
    private void markCacheDirty() {
        if (dirtyCache) {
            return;
        }
        dirtyCache = true;
    }
}

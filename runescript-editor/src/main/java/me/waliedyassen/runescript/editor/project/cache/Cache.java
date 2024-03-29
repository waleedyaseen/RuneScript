/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.project.cache;

import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.waliedyassen.runescript.commons.Pair;
import me.waliedyassen.runescript.compiler.CompiledFile;
import me.waliedyassen.runescript.compiler.CompiledScriptUnit;
import me.waliedyassen.runescript.compiler.Input;
import me.waliedyassen.runescript.compiler.SourceFile;
import me.waliedyassen.runescript.compiler.codegen.writer.bytecode.BytecodeCodeWriter;
import me.waliedyassen.runescript.editor.file.FileTypeManager;
import me.waliedyassen.runescript.editor.job.WorkExecutor;
import me.waliedyassen.runescript.editor.project.Project;
import me.waliedyassen.runescript.editor.project.cache.unit.CacheUnit;
import me.waliedyassen.runescript.editor.project.compile.CompileResult;
import me.waliedyassen.runescript.editor.project.compile.ProjectCompiler;
import me.waliedyassen.runescript.editor.util.ex.PathEx;
import me.waliedyassen.runescript.util.ChecksumUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A cache system that is for a specific project.
 *
 * @author Walied K. Yassen
 */
@Slf4j
public final class Cache {

    /**
     * The default compile options.
     */
    private static final CompileOptions DEFAULT_OPTIONS = new CompileOptions();

    /**
     * A map of all the cache units that are stored in this
     */
    @Getter
    private final Map<String, CacheUnit<?>> units = new HashMap<>();

    /**
     * The project which this cache is for.
     */
    private final Project project;

    /**
     * Whether or not the cache file is currently dirty.
     */
    private boolean dirtyCache;

    /**
     * Constructs a new {@link Cache} type object instance.
     *
     * @param project the project which owns this cache.
     */
    public Cache(Project project) {
        this.project = project;
        WorkExecutor.getSingleThreadScheduler().scheduleWithFixedDelay(this::performSaving, 0, 10, TimeUnit.SECONDS);
    }

    /**
     * Reads the content of the cache from the specified {@link DataInputStream stream}.
     *
     * @param stream the stream to read the content of the cache from.
     * @throws IOException if anything occurs while reading the data from the stream.
     */
    public void deserialize(DataInputStream stream) throws IOException {
        var unitsCount = stream.readInt();
        for (int index = 0; index < unitsCount; index++) {
            var path = stream.readUTF();
            var extension = PathEx.getExtension(path);
            var compiler = getCompiler(extension);
            var cacheUnit = compiler.createUnit(path, null);
            cacheUnit.read(stream);
            units.put(cacheUnit.getNameWithPath(), cacheUnit);
        }
    }

    /**
     * Writes the content of the cache to the specified {@link DataOutputStream stream}.
     *
     * @param stream the stream to write the content of the cache to.
     * @throws IOException if anything occurs while writing the data to the stream.
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
            log.trace("Found dirty cache.. saving cache");
            project.saveCache();
            dirtyCache = false;
        }
    }

    /**
     * Collects all of the changes of the compilable files in the source directory and compiles
     * the affected files.
     *
     * @throws IOException if anything occurs accessing the files on the local disk.
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
            dirtyCache = true;
        }
        if (!changes.isEmpty()) {
            log.info("Found {} changed files", changes.size());
            recompile(changes);
        }
    }

    /**
     * Re-compiles the content of the file at the specified {@link Path relative path}.
     *
     * @param path the path of the file to recompile relative to the source directory of the project.
     */
    @SneakyThrows
    public void recompile(Path path) {
        recompile(path, Files.readAllBytes(path));
    }

    /**
     * Re-compiles the content of the file at the specified {@link Path relative path}.
     *
     * @param path the relative path of the file that we want to recompile.
     * @return the result object of the compile call.
     */
    @SneakyThrows
    public CompileResult recompile(Path path, byte[] content) {
        return recompile(Collections.singletonList(Pair.of(path, content)));
    }

    /**
     * Re-compiles the specified list of files.
     *
     * @param files the list of files that we want to recompile.
     * @return the result object of the compile call.
     */
    @SneakyThrows
    private CompileResult recompile(List<Pair<Path, byte[]>> files) {
        return recompile(files, DEFAULT_OPTIONS);
    }

    /**
     * Re-compiles the specified list of files.
     *
     * @param files the list of files that we want to recompile.
     * @return the result object of the compile call.
     */
    @SneakyThrows
    @SuppressWarnings({"unchecked", "rawtypes"})
    private CompileResult recompile(List<Pair<Path, byte[]>> files, CompileOptions options) {
        var sourceDirectory = project.getBuildPath().getSourceDirectory();
        var inputs = new HashMap<ProjectCompiler<?, ?>, Input>();
        var result = new CompileResult();
        for (var pair : files) {
            var normalPath = pair.getKey();
            var normalizedPath = PathEx.normalizeRelative(sourceDirectory, normalPath);
            var content = pair.getValue();
            var compiler = getCompiler(PathEx.getExtension(normalPath));
            var input = inputs.computeIfAbsent(compiler, dummy -> options.createInput());
            var unit = units.get(normalizedPath);
            if (unit == null) {
                unit = createCacheUnit(normalPath);
            } else {
                unit.undefineSymbols(project.getSymbolTable());
                unit.clear();
            }
            input.addSourceFile(SourceFile.of(normalPath, content));
        }
        if (!inputs.isEmpty()) {
            var dirty = false;
            for (var inputEntry : inputs.entrySet()) {
                var compiler = inputEntry.getKey();
                var input = inputEntry.getValue();
                var output = compiler.compile(input);
                for (Map.Entry outputEntry : output.getFiles().entrySet()) {
                    var compiledFile = (CompiledFile<?, ?>) outputEntry.getValue();
                    var normalPath = Paths.get((String) outputEntry.getKey());
                    var normalizedPath = PathEx.normalizeRelative(sourceDirectory, normalPath);
                    var unit = units.get(normalizedPath);
                    unit.update((CompiledFile) compiledFile);
                    for (var compiledUnit : compiledFile.getUnits()) {
                        result.getSyntax().add(compiledUnit.getSyntax());
                        if (options.getOnUnitCompilation() != null) {
                            options.getOnUnitCompilation().accept(compiledUnit);
                        }
                    }
                    for (var error : compiledFile.getErrors()) {
                        unit.getErrors().add(new CachedError(error.getSpan(), 1, error.getMessage()));
                    }
                    unit.defineSymbols(project.getSymbolTable());
                    project.updateErrors(unit);
                    dirty = true;
                }
            }
            if (dirty) {
                markCacheDirty();
            }
        }
        return result;
    }

    /**
     * Attempts to pack all of the files that needs packing in the project.
     *
     * @return <code>true</code> if the pack was successful otherwise <code>false</code>.
     */
    @SneakyThrows
    public boolean pack(boolean forceAll) {
        if (units.values().stream().anyMatch(unit -> unit.getErrors().size() > 0)) {
            return false;
        }
        // TODO: We need to make sure all of the tabs are currently saved so we don't cause any issues in the symbol table.
        // TODO: Clean this function up.
        var scriptUnits = new ArrayList<CompiledScriptUnit>();
        var options = new CompileOptions();
        options.setRunCodeGeneration(true);
        options.setRunIdGeneration(true);
        options.setOnUnitCompilation(object -> {
            if (object instanceof CompiledScriptUnit) {
                scriptUnits.add((CompiledScriptUnit) object);
            } else {
                throw new IllegalArgumentException();
            }
        });
        var units = this.units.values().stream().filter(unit -> forceAll || unit.getCrc() != unit.getPackCrc()).toList();
        var files = new ArrayList<Pair<Path, byte[]>>();
        for (var unit : units) {
            var path = project.getBuildPath().getSourceDirectory().resolve(unit.getNameWithPath());
            files.add(Pair.of(path, Files.readAllBytes(path)));
        }
        recompile(files, options);
        var writer = new BytecodeCodeWriter(project.getIdManager(), project.isSupportsLongPrimitiveType());
        for (var scriptUnit : scriptUnits) {
            var binaryScript = scriptUnit.getBinaryScript();
            var name = scriptUnit.getBinaryScript().getName();
            var predefinedIdAnnotation = scriptUnit.getSyntax().findAnnotation("id");
            Integer id;
            if (predefinedIdAnnotation != null) {
                id = predefinedIdAnnotation.getValue().getValue();
            } else {
                id = project.getIdManager().findScript(name, binaryScript.getExtension());
            }
            var serialised = writer.write(binaryScript).encode();
            project.getPackManager().pack(binaryScript.getExtension(), id, name, serialised);
        }
        return true;
    }

    /**
     * Creates a new {@link CacheUnit} object for the specified {@link Path relative path}.
     *
     * @param relativePath the relative path of the cache unit.
     * @return the created {@link CacheUnit} object.
     */
    @SuppressWarnings("rawtypes")
    private CacheUnit createCacheUnit(Path relativePath) {
        var extension = PathEx.getExtension(relativePath);
        var compiler = getCompiler(extension);
        var fullPath = PathEx.normalizeRelative(project.getBuildPath().getSourceDirectory(), relativePath);
        var unit = compiler.createUnit(fullPath, relativePath.getFileName().toString());
        units.put(fullPath, unit);
        return unit;
    }

    /**
     * Returns the {@link ProjectCompiler} with the specified {@code extension} or {@code null}
     * if there was none found.
     *
     * @param extension the extension which we want to find the compiler for.
     * @return the found {@link ProjectCompiler} if found otherwise {@code null}.
     */
    private ProjectCompiler<?, ?> getCompiler(String extension) {
        return project.getCompilerProvider().get(extension);
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

    /**
     * Holds common options for the compilation process.
     *
     * @author Walied K. Yasen
     */
    @Data
    public static class CompileOptions {

        /**
         * Whether or not to run the code generation.
         */
        private boolean runCodeGeneration;

        /**
         * Whether or not we should run the id generation.
         */
        private boolean runIdGeneration;

        /**
         * A callback which gets called when we finish compiling a unit.
         */
        private Consumer<Object> onUnitCompilation;

        /**
         * Creates a base {@link Input} object with all the possible options that are present
         * in this {@link CompileOptions} instance.
         *
         * @return the created {@link Input} object.
         */
        public Input createInput() {
            var input = new Input();
            input.setRunCodeGeneration(runCodeGeneration);
            input.setRunIdGeneration(runIdGeneration);
            return input;
        }
    }
}

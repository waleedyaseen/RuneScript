/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler;

import lombok.Getter;
import lombok.var;
import me.waliedyassen.runescript.util.ChecksumUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The output object of a compiler.
 *
 * @param <U>
 *         the compiled unit type.
 *
 * @author Walied K. Yassen
 */
public final class Output<U> {

    /**
     * A map of all the {@link CompiledFile} objects.
     */
    @Getter
    private final Map<String, CompiledFile<U>> files = new HashMap<>();

    /**
     * Adds a compiled unit to this output object.
     *
     * @param sourceFile
     *         the source file the unit was compiled from.
     * @param unit
     *         the unit object that we compiled.
     */
    public void addUnit(SourceFile sourceFile, U unit) {
        var compiledFile = getOrCreateFile(sourceFile);
        compiledFile.addUnit(unit);
    }

    /**
     * Adds a compile error to this output object.
     *
     * @param sourceFile
     *         the source file the error was produced in.
     * @param error
     *         the error that was produced.
     */
    public void addError(SourceFile sourceFile, CompilerError error) {
        var compiledFile = getOrCreateFile(sourceFile);
        compiledFile.addError(error);
    }

    /**
     * Returns a list of all the {@link CompiledFile} objects.
     *
     * @return a {@link List} object.
     */
    public List<CompiledFile<U>> getCompiledFiles() {
        return new ArrayList<>(files.values());
    }

    /**
     * Returns the cached {@link CompiledFile} that corresponds to the specified {@link SourceFile}. If there
     * was nothing cached, we will create a new cached object and return it.
     *
     * @param sourceFile
     *         the source file object which we want it's the corresponding compiled file.
     *
     * @return the {@link CompiledFile} object.
     */
    private CompiledFile<U> getOrCreateFile(SourceFile sourceFile) {
        var fullName = sourceFile.getFullNameWithLocation();
        var file = files.get(fullName);
        if (file == null) {
            files.put(fullName, file = new CompiledFile<>(ChecksumUtil.calculateCrc32(sourceFile.getContent())));
        }
        return file;
    }
}

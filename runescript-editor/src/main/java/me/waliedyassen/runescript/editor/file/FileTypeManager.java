/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.file;

import lombok.var;
import me.waliedyassen.runescript.editor.file.impl.PlainFileType;
import me.waliedyassen.runescript.editor.file.impl.ProjectFileType;
import me.waliedyassen.runescript.editor.file.impl.ScriptFileType;

import java.util.HashMap;
import java.util.Map;

/**
 * The manager of {@link FileType} objects, it's responsible for registering and looking up file type objects.
 *
 * @author Walied K. Yassen
 */
public final class FileTypeManager {

    /**
     * The default type which we will return if no associated file type was found.
     */
    private static final PlainFileType DEFAULT_TYPE = new PlainFileType();


    /**
     * A map of all the registered file types associated by their extensions.
     */
    private static final Map<String, FileType> registeredTypes = new HashMap<>();

    // Register the default file types.
    static {
        register(new ProjectFileType());
        register(new ScriptFileType());
    }

    /**
     * Registers a new {@link FileType} in the file type manager.
     *
     * @param type the type to register in the file type mnager.
     */
    public static void register(FileType type) {
        for (var extension : type.getExtensions()) {
            registeredTypes.put(extension, type);
        }
    }

    /**
     * Returns the {@link FileType} that is associated with the specified {@code extension}.
     *
     * @param extension the extension that we want to look for it's associated file type.
     * @return the associated {@link FileType} object.
     */
    public static FileType lookup(String extension) {
        return registeredTypes.getOrDefault(extension, DEFAULT_TYPE);
    }

    private FileTypeManager() {
        // NOOP
    }
}

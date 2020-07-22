/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.project.build;

import lombok.Data;
import me.waliedyassen.runescript.editor.project.Project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The build path of a {@link Project}, it holds information about the source and the pack directories.
 *
 * @author Walied K. Yassen
 */
@Data
public final class BuildPath {

    /**
     * The source directory of the project.
     */
    private final Path sourceDirectory;

    /**
     * The pack directory of the project.
     */
    private final Path packDirectory;

    /**
     * Ensures that both the source and the pack directory exist.
     *
     * @throws IOException
     *         if anything occurs during the creation of the directories.
     */
    public void ensureExistence() throws IOException {
        ensureExistence(sourceDirectory);
        ensureExistence(packDirectory);
    }

    /**
     * Ensures the specified directory {@link Path path} exist on the local file system.
     *
     * @param directory
     *         the directory to check if it exists.
     *
     * @throws IOException
     *         if anything occurs during the creation of the directory.
     */
    private static void ensureExistence(Path directory) throws IOException {
        if (Files.exists(directory)) {
            if (!Files.isDirectory(directory)) {
                throw new IllegalArgumentException("The specified directory path is not a valid directory path");
            }
            return;
        }
        Files.createDirectory(directory);
    }
}

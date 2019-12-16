/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.project;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.waliedyassen.runescript.editor.project.build.BuildPath;
import me.waliedyassen.runescript.editor.util.JsonUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A very basic project system that provides basic information such as the name and the build path directories.
 *
 * @author Walied K. Yassen
 */
public final class Project {

    /**
     * The project information file name.
     */
    static final String FILE_NAME = ".rsproj";

    /**
     * The base directory {@link Path} of the project.
     */
    @Getter
    private final Path directory;

    /**
     * the name of the project.
     */
    @Getter
    @Setter
    private String name;

    /**
     * The build path of the project.
     */
    @Getter
    @Setter(AccessLevel.PACKAGE)
    private BuildPath buildPath;

    /**
     * Constructs a new {@link Project} type object instance.
     *
     * @param directory
     *         the root directory path of the project.
     */
    Project(Path directory) {
        this.directory = directory;
    }

    /**
     * Attempts to load the project information data from the local disk.
     *
     * @throws IOException
     *         if anything occurs during the loading procedure.
     */
    void loadData() throws IOException {
        // Read the node tree from the file.
        JsonNode root;
        try (var reader = Files.newBufferedReader(findProjectFile())) {
            root = JsonUtil.getMapper().reader().readTree(reader);
        }
        // Read the project general information.
        name = JsonUtil.getTextOrThrow(root, "name", "The project name cannot be null or empty");
        // Read the project build path.
        var object = JsonUtil.getObjectOrThrow(root, "build_path", "The build path object cannot be null");
        // Extract the build path properties.
        var sourcePath = JsonUtil.getTextOrThrow(object, "source", "The source directory cannot be null or empty");
        var packPath = JsonUtil.getTextOrThrow(object, "pack", "The pack directory cannot be null or empty");
        // Create the build path and ensure it's existence.
        buildPath = new BuildPath(directory.resolve(sourcePath), directory.resolve(packPath));
        buildPath.ensureExistence();
    }

    /**
     * Saves the information data of the project to the local disk.
     *
     * @throws IOException
     *         if anything occurs during the saving procedure.
     */
    void saveData() throws IOException {
        // Create the project root node.
        var root = JsonUtil.getMapper().createObjectNode();
        // Serialise the general information.
        root.put("name", name);
        // Serialise the build path information.
        var buildPath = root.putObject("build_path");
        buildPath.put("source", directory.relativize(this.buildPath.getSourceDirectory()).toString());
        buildPath.put("pack", directory.relativize(this.buildPath.getPackDirectory()).toString());
        // Write the serialised data into the project file.
        JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValue(findProjectFile().toFile(), root);
    }

    /**
     * Finds the {@code .rspoj} file of the project.
     *
     * @return the {@link Path} object which leads to that file.
     */
    private Path findProjectFile() {
        return directory.resolve(FILE_NAME);
    }
}

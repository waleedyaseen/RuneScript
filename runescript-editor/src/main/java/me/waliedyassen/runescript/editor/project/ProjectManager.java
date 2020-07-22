/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.project;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import me.waliedyassen.runescript.editor.Api;
import me.waliedyassen.runescript.editor.project.build.BuildPath;
import me.waliedyassen.runescript.editor.property.impl.BooleanProperty;
import me.waliedyassen.runescript.editor.property.impl.ReferenceProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The project manager system of the RuneScript Editor.
 *
 * @author Walied K. Yassen
 */
@Slf4j
public final class ProjectManager {

    /**
     * A property which will return {@link Boolean#TRUE} when there is an active project otherwise it will return {@link
     * Boolean#FALSE}.
     */
    @Getter
    private final BooleanProperty activeProperty = new BooleanProperty();

    /**
     * A negated property which will hold the opposite value of {@link #activeProperty} at all times.
     */
    @Getter
    private final BooleanProperty inactiveProperty = activeProperty.negate();

    /**
     * The project that is currently open and active.
     */
    @Getter
    private final ReferenceProperty<Project> currentProject = new ReferenceProperty<>();

    /**
     * Opens a new project from the local disk.
     *
     * @param path the path of the project on the local disk.
     * @return the opened {@link Project} object.
     * @throws ProjectException if there is currently an active or an open project or there was a problem with loading the project
     *                          information data from the local disk.
     */
    public Project open(Path path) {
        if (!currentProject.isEmpty()) {
            throw new ProjectException("Please use close() to close the current project before opening another");
        }
        try {
            currentProject.set(openProject(path));
            activeProperty.set(true);
            return currentProject.get();
        } catch (IOException e) {
            throw new ProjectException("Failed to open the project from the local disk", e);
        }
    }

    /**
     * Saves the current active or open project information data to the local disk.
     *
     * @throws ProjectException if there is currently no active or open project or there was a problem with saving the project
     *                          information data on the local disk.
     */
    public void save() {
        if (currentProject.isEmpty()) {
            throw new ProjectException("There is currently no active or open project to save");
        }
        try {
            currentProject.get().saveData();
        } catch (IOException e) {
            throw new ProjectException("Failed to save the project information data to the local disk", e);
        }
    }

    /**
     * Closes the current project or throws an exception if there is currently none open.
     *
     * @throws ProjectException if there is currently no active or open project or there was a problem whilst saving the project
     *                          information.
     */
    public void close() {
        if (currentProject.isEmpty()) {
            throw new ProjectException("There is currently no project opened");
        }
        if (Api.getApi().getEditorView().closeAllTabs()) {
            return;
        }
        var project = currentProject.get();
        try {
            save();
            project.closeVfs();
        } finally {
            currentProject.set(null);
            activeProperty.set(false);
        }
    }

    /**
     * Attempts to open the project that is located in the specified {@link Path directory}.
     *
     * @param directory the directory which contains the project.
     * @return the opened {@link Project} object.
     * @throws IOException if anything occurs during accessing the data of the projct on the local disk.
     */
    private static Project openProject(Path directory) throws IOException {
        if (!Files.exists(directory.resolve(Project.FILE_NAME))) {
            throw new ProjectException("The specified directory does not contain a project file");
        }
        var project = new Project(directory);
        project.loadData();
        return project;
    }

    /**
     * Attempts to create a new {@link Project} with the specified {@code name} at the specified {@link Path directory}.
     * After it is created ,it will write all of the data of the project to the local disk.
     *
     * @param name      the name of the project to create.
     * @param directory the root directory path of the project.
     * @return the created {@link Project} object.
     * @throws IOException if anything occurs during the writing the data of the project to the local disk.
     */
    private static Project createProject(String name, Path directory) throws IOException {
        if (Files.exists(directory.resolve(Project.FILE_NAME))) {
            throw new ProjectException("The specified directory already contains a project file");
        }
        var project = new Project(directory);
        project.setName(name);
        project.setBuildPath(new BuildPath(directory.resolve("src"), directory.resolve("pack")));
        project.getBuildPath().ensureExistence();
        project.saveData();
        return project;
    }
}

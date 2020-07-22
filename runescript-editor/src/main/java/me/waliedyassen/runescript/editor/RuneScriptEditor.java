/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import me.waliedyassen.runescript.editor.project.ProjectManager;
import me.waliedyassen.runescript.editor.settings.Settings;
import me.waliedyassen.runescript.editor.ui.EditorUI;
import me.waliedyassen.runescript.editor.util.LafUtil;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The main class for the RuneScript Editor systems.
 *
 * @author Walied K. Yassen
 */
@Slf4j
public final class RuneScriptEditor {

    /**
     * The path of the editor directory in the user file.
     */
    @Getter
    private static Path userDirectory;

    /**
     * The project manager of the editor.
     */
    @Getter
    private final ProjectManager projectManager = new ProjectManager();

    /**
     * The settings of the editor.
     */
    @Getter
    private Settings settings;

    /**
     * The user-interface of the editor.
     */
    @Getter
    private EditorUI ui;

    /**
     * Initialises the RuneScript Editor.
     */
    private void initialise() {
        initialiseSettings();
        initialiseUi();
    }

    /**
     * Initialises the editor settings.
     */
    private void initialiseSettings() {
        settings = new Settings(userDirectory.resolve("settings.json"));
        if (Files.exists(settings.getPath())) {
            settings.load();
        } else {
            settings.save();
        }
    }

    /**
     * Initialises the editor's user-interface.
     */
    private void initialiseUi() {
        ui = new EditorUI(this);
        ui.initialise();
    }

    /**
     * Shows the RuneScript Editor user-interface.
     */
    private void show() {
        ui.show();
    }

    /**
     * Initialises the editor user directory.
     */
    private static void initialiseDirectory() {
        userDirectory = Paths.get(System.getProperty("user.home")).resolve(".runescript");
        if (!Files.exists(userDirectory)) {
            try {
                Files.createDirectories(userDirectory);
            } catch (IOException e) {
                log.error("Failed to create the editor user directory: {}", userDirectory, e);
                System.exit(1);
            }
        }
        log.info("User directory is set to: {}", userDirectory);
    }

    /**
     * Initialises the Look And Feel System for the RuneScript Editor.
     */
    private static void initialiseLookAndFeel() {
        LafUtil.setup();
    }

    /**
     * Initialises the systems for the RuneScript Editor.
     */
    private static void initialiseSystem() {
        var editor = new RuneScriptEditor();
        Api.setApi(new Api(editor));
        editor.initialise();
        editor.show();
    }

    /**
     * The main entry point for each Java application, in this (our) application, we will start the RuneScript Editor
     * systems and display the user-interface for the user.
     *
     * @param args
     *         the command-line arguments that are passed to the application, currently ignored.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            initialiseDirectory();
            initialiseLookAndFeel();
            initialiseSystem();
        });
    }

    private RuneScriptEditor() {
        // NOOP
    }
}

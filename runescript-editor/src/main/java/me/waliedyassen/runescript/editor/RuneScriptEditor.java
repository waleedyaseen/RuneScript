/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor;

import lombok.Getter;
import me.waliedyassen.runescript.editor.project.ProjectManager;
import me.waliedyassen.runescript.editor.ui.EditorUI;
import me.waliedyassen.runescript.editor.util.LafUtil;

import javax.swing.*;

/**
 * The main class for the RuneScript Editor systems.
 *
 * @author Walied K. Yassen
 */
public final class RuneScriptEditor {

    /**
     * The project manager of the editor.
     */
    @Getter
    private final ProjectManager projectManager = new ProjectManager();

    /**
     * The user-interface of the editor.
     */
    @Getter
    private EditorUI ui;

    /**
     * Initialises the RuneScript Editor.
     */
    private void initialise() {
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
            initialiseLookAndFeel();
            initialiseSystem();
        });
    }

    private RuneScriptEditor() {
        // NOOP
    }
}

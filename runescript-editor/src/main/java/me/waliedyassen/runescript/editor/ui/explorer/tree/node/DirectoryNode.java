/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.explorer.tree.node;

import lombok.Getter;
import lombok.Setter;
import me.waliedyassen.runescript.editor.shortcut.Shortcut;
import me.waliedyassen.runescript.editor.shortcut.ShortcutManager;
import me.waliedyassen.runescript.editor.shortcut.common.CommonGroups;
import me.waliedyassen.runescript.editor.shortcut.common.CommonShortcuts;
import me.waliedyassen.runescript.editor.ui.dialog.DialogManager;
import me.waliedyassen.runescript.editor.ui.editor.EditorView;
import me.waliedyassen.runescript.editor.ui.explorer.tree.ExplorerNode;
import me.waliedyassen.runescript.editor.ui.explorer.tree.ExplorerTree;
import me.waliedyassen.runescript.editor.ui.explorer.tree.lazy.LazyLoading;
import me.waliedyassen.runescript.editor.ui.explorer.tree.lazy.LoadingNode;
import me.waliedyassen.runescript.editor.ui.menu.action.list.ActionList;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A directory node in the project explorer tree.
 *
 * @author Walied K. Yassen
 */
public class DirectoryNode extends ExplorerNode<Path> {

    /**
     * Whether or not the directory node has been already loaded.
     */
    @Getter
    @Setter
    private volatile boolean loaded;

    /**
     * Whether or not the directory node is currently being loaded.
     */
    @Getter
    @Setter
    private boolean loading;

    /**
     * Constructs a new {@link DirectoryNode} type object instance.
     *
     * @param value the path which leads to the directory.
     */
    public DirectoryNode(Path value) {
        super(value);
        setUserObject(value.getFileName());
        LazyLoading.setup(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateActions(ActionList actionList) {
        var newMenu = actionList.addMenu("New");
        newMenu.addAction("Directory", (source) -> {
            var folderName = DialogManager.askForName("Enter the name of the directory you wish to create:");
            if (folderName == null) {
                return;
            }
            var path = getValue().resolve(folderName);
            if (Files.exists(path)) {
                DialogManager.showErrorDialog("Error", "The specified directory already exists.");
                return;
            }
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                DialogManager.showErrorDialog("Error", "An I/O error occurred while creating the directory.");
            }
        });
        newMenu.addSeparator();
        newMenu.addAction("Script", (source) -> {
            var scriptName = DialogManager.askForName("Enter the name of the script you wish to create:");
            if (scriptName == null) {
                return;
            }
            var path = getValue().resolve(scriptName + ".rs2");
            if (Files.exists(path)) {
                DialogManager.showErrorDialog("Error", "The specified script file already exists");
                return;
            }
            try {
                Files.createFile(path);
            } catch (IOException e) {
                DialogManager.showErrorDialog("Error", "An I/O error occurred while creating the file.");
            }
        });
        actionList.addSeparator();
        actionList.addAction("Delete", CommonGroups.EXPLORER.lookup(CommonShortcuts.EXPLORER_DELETE));
    }

    static {
        ShortcutManager.getInstance().addShortcut(CommonGroups.EXPLORER, CommonShortcuts.EXPLORER_DELETE, KeyStroke.getKeyStroke("DELETE"), source -> {
            var explorerTree = (ExplorerTree) source;
            var paths = explorerTree.getSelectionPaths();
            if (paths == null || paths.length < 1) {
                return;
            }
            for (var path : paths) {
                var component = path.getLastPathComponent();
                if (component instanceof DirectoryNode) {
                    // TODO:
                } else if (component instanceof FileNode) {
                    // TODO:
                }
            }
        });
    }
}

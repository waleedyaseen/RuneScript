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
import lombok.var;
import me.waliedyassen.runescript.editor.Api;
import me.waliedyassen.runescript.editor.shortcut.ShortcutManager;
import me.waliedyassen.runescript.editor.shortcut.UiAction;
import me.waliedyassen.runescript.editor.shortcut.common.CommonGroups;
import me.waliedyassen.runescript.editor.shortcut.common.CommonShortcuts;
import me.waliedyassen.runescript.editor.ui.dialog.DialogManager;
import me.waliedyassen.runescript.editor.ui.explorer.tree.ExplorerNode;
import me.waliedyassen.runescript.editor.ui.explorer.tree.ExplorerTree;
import me.waliedyassen.runescript.editor.ui.explorer.tree.lazy.LazyLoading;
import me.waliedyassen.runescript.editor.ui.menu.action.list.ActionList;
import me.waliedyassen.runescript.editor.vfs.VFSFileListener;
import me.waliedyassen.runescript.type.PrimitiveType;

import javax.swing.*;
import javax.swing.tree.MutableTreeNode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A directory node in the project explorer tree.
 *
 * @author Walied K. Yassen
 */
public class DirectoryNode extends ExplorerNode<Path> implements VFSFileListener {

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
     * @param tree
     *         the owner tree of this explorer node.
     * @param value
     *         the path which leads to the directory.
     */
    public DirectoryNode(ExplorerTree tree, Path value) {
        super(tree, value);
        setUserObject(value.getFileName().toString());
        LazyLoading.setup(this);
        var file = Api.getApi().getProjectManager().getCurrentProject().get().getVfs().resolveFile(value);
        file.addListener(this);
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
        newMenu.addAction("Server Script", createFileAction("rs2", "server script"));
        newMenu.addAction("Client Script", createFileAction("cs2", "client script"));
        newMenu.addSeparator();
        for (var type : PrimitiveType.values()) {
            if (!type.isConfigType()) {
                continue;
            }
            newMenu.addAction("Config ." + type.getRepresentation(), createFileAction(type.getRepresentation(), type.getRepresentation() + " config"));
        }
        actionList.addSeparator();
        actionList.addAction("Delete", CommonGroups.EXPLORER.lookup(CommonShortcuts.EXPLORER_DELETE));
    }

    /**
     * Creates a create file action for the specified extension and title name.
     *
     * @param extension
     *         the extension of the file.
     * @param titleName
     *         the title name of the file.
     *
     * @return the create file action as {@link UiAction} object.
     */
    private UiAction createFileAction(String extension, String titleName) {
        return (source) -> {
            var scriptName = DialogManager.askForName("Enter the name of the " + titleName + " you wish to create:");
            if (scriptName == null) {
                return;
            }
            var path = getValue().resolve(scriptName + "." + extension);
            if (Files.exists(path)) {
                DialogManager.showErrorDialog("Error", "The specified " + titleName + " file already exists");
                return;
            }
            try {
                Files.createFile(path);
            } catch (IOException e) {
                DialogManager.showErrorDialog("Error", "An I/O error occurred while creating the file.");
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEntityCreate(Path path) {
        SwingUtilities.invokeLater(() -> {
            if (Files.isDirectory(path)) {
                add(new DirectoryNode(tree, path));
            } else {
                add(new FileNode(tree, path));
            }
            tree.getModel().nodesWereInserted(this, new int[]{getChildCount() - 1});
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEntityDelete(Path path) {
        SwingUtilities.invokeLater(() -> {
            var count = getChildCount();
            var name = path.getFileName().toString();
            for (var index = 0; index < count; index++) {
                var child = getChildAt(index);
                if (child instanceof ExplorerNode && ((ExplorerNode<?>) child).getUserObject().equals(name)) {
                    tree.getModel().removeNodeFromParent((MutableTreeNode) child);
                    break;
                }
            }
        });
    }

    static {
        ShortcutManager.getInstance().addShortcut(CommonGroups.EXPLORER, CommonShortcuts.EXPLORER_DELETE, KeyStroke.getKeyStroke("DELETE"), source -> {
            var explorerTree = (ExplorerTree) source;
            var paths = explorerTree.getSelectionPaths();
            if (paths == null || paths.length < 1) {
                return;
            }
            for (var path : paths) {
                var node = path.getLastPathComponent();
                if (node instanceof DirectoryNode) {
                    var directoryNode = (DirectoryNode) node;
                    // TODO:
                } else if (node instanceof FileNode) {
                    var fileNode = (FileNode) node;
                    // TODO:
                }
            }
        });
    }
}

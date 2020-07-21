/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.explorer.tree.node;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import me.waliedyassen.runescript.editor.Api;
import me.waliedyassen.runescript.editor.file.FileType;
import me.waliedyassen.runescript.editor.file.FileTypeManager;
import me.waliedyassen.runescript.editor.file.impl.ProjectFileType;
import me.waliedyassen.runescript.editor.ui.explorer.tree.ExplorerNode;
import me.waliedyassen.runescript.editor.ui.explorer.tree.ExplorerTree;
import me.waliedyassen.runescript.editor.ui.menu.action.list.ActionList;
import me.waliedyassen.runescript.editor.util.ex.PathEx;

import java.nio.file.Path;

/**
 * A file node in the project explorer tree.
 *
 * @author Walied K. Yassen
 */
@Slf4j
public final class FileNode extends ExplorerNode<Path> {

    /**
     * The file type of the node.
     */
    @Getter
    private final FileType fileType;

    /**
     * Constructs a new {@link FileNode} type object instance.
     *
     * @param tree
     *         the owner tree of this explorer node.
     * @param path
     *         the path which leads to the file.
     */
    public FileNode(ExplorerTree tree, Path path) {
        super(tree, path);
        fileType = FileTypeManager.lookup(PathEx.getExtension(path));
        setUserObject(path.getFileName());
        setAllowsChildren(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateActions(ActionList actionList) {
        actionList.addAction("Open", (source) -> openFile());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActionClick() {
        openFile();
    }

    /**
     * Opens this file in the editor.
     */
    private void openFile() {
        var editorView = Api.getApi().getEditorView();
        if (editorView.selectTab(getValue())) {
            return;
        }
        var editor = fileType.createEditor(getValue());
        if (editor == null) {
            return;
        }
        Api.getApi().getEditorView().addTab(editor);
    }

    /**
     * Checks whether or not the file node is for a protected file.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public boolean isProtectedFile() {
        return fileType instanceof ProjectFileType;
    }
}

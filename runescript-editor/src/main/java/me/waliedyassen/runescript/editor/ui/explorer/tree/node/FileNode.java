/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.explorer.tree.node;

import me.waliedyassen.runescript.editor.ui.explorer.tree.ExplorerNode;
import me.waliedyassen.runescript.editor.ui.menu.action.list.ActionList;

import java.nio.file.Path;

/**
 * A file node in the project explorer tree.
 *
 * @author Walied K. Yassen
 */
public final class FileNode extends ExplorerNode<Path> {

    /**
     * Constructs a new {@link FileNode} type object instance.
     *
     * @param file
     *         the path which leads to the file.
     */
    public FileNode(Path file) {
        super(file);
        setUserObject(file.getFileName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateActions(ActionList actionList) {
        // NOOP
    }
}

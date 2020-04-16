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
import me.waliedyassen.runescript.editor.ui.explorer.tree.ExplorerNode;
import me.waliedyassen.runescript.editor.ui.explorer.tree.lazy.LazyLoading;
import me.waliedyassen.runescript.editor.ui.explorer.tree.lazy.LoadingNode;
import me.waliedyassen.runescript.editor.ui.menu.action.list.ActionList;

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
     * @param value
     *         the path which leads to the directory.
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
        actionList.addAction("Delete", (source) -> {
        });
    }
}

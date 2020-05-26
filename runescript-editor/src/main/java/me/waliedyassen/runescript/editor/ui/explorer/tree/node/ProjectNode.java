/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.explorer.tree.node;

import lombok.Getter;
import me.waliedyassen.runescript.editor.project.Project;
import me.waliedyassen.runescript.editor.shortcut.ShortcutManager;
import me.waliedyassen.runescript.editor.shortcut.common.CommonGroups;
import me.waliedyassen.runescript.editor.shortcut.common.CommonShortcuts;
import me.waliedyassen.runescript.editor.ui.explorer.tree.ExplorerTree;
import me.waliedyassen.runescript.editor.ui.menu.action.list.ActionList;

import javax.swing.*;

/**
 * A project node in the project explorer tree.
 *
 * @author Walied K. Yassen
 */
public final class ProjectNode extends DirectoryNode {

    /**
     * The project which this node is for.
     */
    @Getter
    private final Project project;

    /**
     * Constructs a new {@link ProjectNode} type object instance.
     *
     * @param tree    the owner tree of the project node.
     * @param project the project which the node is for.
     */
    public ProjectNode(ExplorerTree tree, Project project) {
        super(tree, project.getDirectory());
        this.project = project;
        setUserObject(project.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateActions(ActionList actionList) {
        super.populateActions(actionList);
        actionList.addSeparator();
        actionList.addAction("Close Project", CommonGroups.EXPLORER.lookup(CommonShortcuts.EXPLORER_CLOSE_PROJECT));
    }

    static {
        ShortcutManager.getInstance().addShortcut(CommonGroups.EXPLORER, CommonShortcuts.EXPLORER_CLOSE_PROJECT, KeyStroke.getKeyStroke("alt shift Q"), (source) -> {

        });
    }
}

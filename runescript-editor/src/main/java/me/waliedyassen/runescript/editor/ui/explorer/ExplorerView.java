/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.explorer;

import lombok.Getter;
import me.waliedyassen.runescript.editor.ui.explorer.tree.ExplorerTree;
import me.waliedyassen.runescript.editor.ui.explorer.tree.node.DirectoryNode;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.nio.file.Paths;

/**
 * The explorer file tree docking view.
 *
 * @author Walied K. Yassen
 */
public final class ExplorerView extends JPanel {

    /**
     * The docking {@code ID} for the explorer docking component.
     */
    public static final String DOCK_ID = "explorer.dock";

    /**
     * The tree of the explorer.
     */
    @Getter
    private final ExplorerTree tree = new ExplorerTree();

    /**
     * Constructs a new {@link ExplorerView} type object instance.
     */
    public ExplorerView() {
        setLayout(new BorderLayout());
        add(tree, BorderLayout.CENTER);
    }
}

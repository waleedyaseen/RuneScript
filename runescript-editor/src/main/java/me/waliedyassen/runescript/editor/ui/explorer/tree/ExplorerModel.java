/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package me.waliedyassen.runescript.editor.ui.explorer.tree;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/**
 * The tree model of the project explorer tree.
 *
 * @author Walied K. Yassen
 */
public final class ExplorerModel extends DefaultTreeModel {

    /**
     * Constructs a new {@link ExplorerModel} type object instance.
     *
     * @param root
     *         the root node of the explorer.
     */
    public ExplorerModel(TreeNode root) {
        super(root);
    }
}

/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.explorer.tree;

import lombok.Getter;
import me.waliedyassen.runescript.editor.ui.explorer.tree.lazy.LazyLoading;
import me.waliedyassen.runescript.editor.ui.explorer.tree.node.DirectoryNode;
import me.waliedyassen.runescript.editor.ui.explorer.tree.render.ExplorerRenderer;
import me.waliedyassen.runescript.editor.ui.menu.action.ActionManager;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The tree type of the project explorer tree.
 *
 * @author Walied K. Yassen
 */
public final class ExplorerTree extends JTree implements TreeWillExpandListener {

    /**
     * The root node of the tree.
     */
    @Getter
    private final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Workspace");

    /**
     * Constructs a new {@link ExplorerTree} type object instance.
     */
    public ExplorerTree() {
        setRootVisible(false);
        setModel(new ExplorerModel(root));
        setCellRenderer(new ExplorerRenderer());
        addTreeWillExpandListener(this);
        addMouseListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!SwingUtilities.isRightMouseButton(e)) {
                    return;
                }
                var path = getPathForLocation(e.getX(), e.getY());
                if (path == null) {
                    return;
                }
                setSelectionPath(path);
                var node = (TreeNode) path.getLastPathComponent();
                if (node instanceof ExplorerNode) {
                    var list = ActionManager.getInstance().createList();
                    ((ExplorerNode<?>) node).populateActions(list);
                    if (!list.isEmpty()) {
                        list.createPopupMenu().show(ExplorerTree.this, e.getX(), e.getY());
                    }
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void treeWillExpand(TreeExpansionEvent event) {
        var component = event.getPath().getLastPathComponent();
        if (component instanceof DirectoryNode) {
            var node = (DirectoryNode) component;
            if (node.isLoaded()) {
                return;
            }
            LazyLoading.execute(this, node);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void treeWillCollapse(TreeExpansionEvent event) {
        // NOOP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExplorerModel getModel() {
        return (ExplorerModel) super.getModel();
    }

    /**
     * Clears all the children in the root node of the tree.
     */
    public void clear() {
        root.removeAllChildren();
    }
}

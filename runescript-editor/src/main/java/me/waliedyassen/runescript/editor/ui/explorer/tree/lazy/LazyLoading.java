/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.explorer.tree.lazy;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.editor.ui.explorer.tree.ExplorerNode;
import me.waliedyassen.runescript.editor.ui.explorer.tree.ExplorerTree;
import me.waliedyassen.runescript.editor.ui.explorer.tree.node.DirectoryNode;
import me.waliedyassen.runescript.editor.ui.explorer.tree.node.FileNode;

import javax.swing.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A swing-worker which loads all of the specified {@link DirectoryNode} child nodes in the background.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class LazyLoading extends SwingWorker<List<ExplorerNode<?>>, Void> {

    /**
     * The tree which owns the directory node.
     */
    private final ExplorerTree tree;

    /**
     * The directory node which requested this lazy loading.
     */
    private final DirectoryNode node;

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<ExplorerNode<?>> doInBackground() throws Exception {
        var files = Files.list(node.getValue()).sorted().collect(Collectors.toList());
        var list = new ArrayList<ExplorerNode<?>>(files.size());
        files.stream().filter(Files::isDirectory).map(path -> new DirectoryNode(tree, path)).forEach(list::add);
        files.stream().filter(Files::isRegularFile).map(path -> new FileNode(tree, path)).forEach(list::add);
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void done() {
        try {
            var children = get();
            node.removeAllChildren();
            children.forEach(node::add);
            node.setLoading(false);
            node.setLoaded(true);
            tree.getModel().nodeStructureChanged(node);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    /**
     * Sets-up the lazy loading for the specified {@link DirectoryNode}.
     *
     * @param node the node to set up the lazy loading for.
     * @throws IllegalArgumentException if the node has elements or been loaded before.
     */
    public static void setup(DirectoryNode node) {
        if (node.isLoaded() || node.getChildCount() > 0) {
            throw new IllegalArgumentException("The directory node needs to be empty before it can be lazily loaded");
        }
        node.add(new LoadingNode(node.getTree()));
    }

    /**
     * Executes lazy loading for children for the specified {@link DirectoryNode}.
     *
     * @param tree the tree which owns the directory node.
     * @param node the directory node which we want to load it's children lazily.
     */
    public static void execute(ExplorerTree tree, DirectoryNode node) {
        if (node.isLoaded() || node.isLoading() || node.getChildCount() > 1 || !(node.getChildAt(0) instanceof LoadingNode)) {
            throw new IllegalArgumentException("Please use LazyLoading.setup() before calling LazyLoading.execute()");
        }
        node.setLoading(true);
        var loading = new LazyLoading(tree, node);
        loading.execute();
    }
}
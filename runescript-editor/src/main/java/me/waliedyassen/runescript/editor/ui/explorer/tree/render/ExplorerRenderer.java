/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.explorer.tree.render;

import lombok.var;
import me.waliedyassen.runescript.editor.EditorIcons;
import me.waliedyassen.runescript.editor.ui.explorer.tree.node.DirectoryNode;
import me.waliedyassen.runescript.editor.ui.explorer.tree.node.FileNode;
import me.waliedyassen.runescript.editor.ui.explorer.tree.node.ProjectNode;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * The default cell renderer for the explorer tree, the current behaviour is the same as the {@link
 * DefaultTreeCellRenderer} except that his renderer also assigns all the possible node icons.
 *
 * @author Walied K. Yassen
 */
public final class ExplorerRenderer extends DefaultTreeCellRenderer {

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        var component = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (value instanceof ProjectNode) {
            // NOOP
        } else if (value instanceof DirectoryNode) {
            setIcon(EditorIcons.FILETYPE_FOLDER_ICON);
        } else if (value instanceof FileNode) {
            var fileType = ((FileNode) value).getFileType();
            var icon = fileType.getIcon();
            if (icon != null) {
                setIcon(icon);
            }
        }
        return component;
    }
}

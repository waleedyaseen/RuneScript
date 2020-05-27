/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor;

import me.waliedyassen.runescript.editor.resource.ResourceManager;

import javax.swing.*;

/**
 * Contains all of the useful icons for the editor components.
 *
 * @author Walied K. Yassen
 */
public final class EditorIcons {

    /**
     * The icon of the folder node in the explorer tree.
     */
    public static final Icon FILETYPE_FOLDER_ICON = ResourceManager.getInstance().loadIcon("/icons/tree/folder.png");

    /**
     * The icon of the file node in the explorer tree.
     */
    public static final Icon FILETYPE_FILE_ICON = ResourceManager.getInstance().loadIcon("/icons/tree/file.png");

    /**
     * The icon of the script node in the explorer tree.
     */
    public static final Icon FILETYPE_SCRIPT_ICON = ResourceManager.getInstance().loadIcon("/icons/tree/script.png");

    /**
     * The window control close button icon.
     */
    public static final Icon CONTROL_CLOSE_BUTTON = ResourceManager.getInstance().loadIcon("/icons/controls/close.png");

    /**
     * The window control close button hovered icon.
     */
    public static final Icon CONTROL_CLOSE_BUTTON_HOVER = ResourceManager.getInstance().loadIcon("/icons/controls/closeHover.png");

    /**
     * The window control minimise button icon.
     */
    public static final Icon CONTROL_MINIMISE_BUTTON = ResourceManager.getInstance().loadIcon("/icons/controls/minimize.png");

    /**
     * The window control minimise button hovered icon.
     */
    public static final Icon CONTROL_MINIMISE_BUTTON_HOVER = CONTROL_MINIMISE_BUTTON;

    /**
     * The window control maximise button icon.
     */
    public static final Icon CONTROL_MAXIMISE_BUTTON = ResourceManager.getInstance().loadIcon("/icons/controls/maximise.png");

    /**
     * The window control maximise button hovered icon.
     */
    public static final Icon CONTROL_MAXIMISE_BUTTON_HOVER = CONTROL_MAXIMISE_BUTTON;

    /**
     * The window control restore button icon.
     */
    public static final Icon CONTROL_RESTORE_BUTTON = ResourceManager.getInstance().loadIcon("/icons/controls/restore.png");

    /**
     * The window control restore button hovered icon.
     */
    public static final Icon CONTROL_RESTORE_BUTTON_HOVER = CONTROL_RESTORE_BUTTON;

    private EditorIcons() {
        // NOOP
    }
}

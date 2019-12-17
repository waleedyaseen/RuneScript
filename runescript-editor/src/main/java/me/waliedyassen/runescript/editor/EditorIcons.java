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
    public static final Icon FOLDER_ICON = ResourceManager.getInstance().loadIcon("/icons/tree/folder.png");

    /**
     * The icon of the file node in the explorer tree.
     */
    public static final Icon FILE_ICON =  ResourceManager.getInstance().loadIcon("/icons/tree/file.png");

    /**
     * The icon of the script node in the explorer tree.
     */
    public static final Icon SCRIPT_ICON =  ResourceManager.getInstance().loadIcon("/icons/tree/script.png");

    private EditorIcons() {
        // NOOP
    }
}

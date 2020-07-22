/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor;

import me.waliedyassen.runescript.editor.resource.ResourceManager;

import javax.swing.*;
import java.awt.*;

/**
 * Contains all of the useful icons for the editor components.
 *
 * @author Walied K. Yassen
 */
public final class EditorIcons {

    /**
     * The main icon of the editor.
     */
    public static final Image FAVICON = ResourceManager.getInstance().loadImage("/icons/favicon.png");

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
     * The icon of the config node in the explorer tree.
     */
    public static final Icon FILETYPE_CONFIG_ICON = ResourceManager.getInstance().loadIcon("/icons/tree/config.png");

    private EditorIcons() {
        // NOOP
    }
}

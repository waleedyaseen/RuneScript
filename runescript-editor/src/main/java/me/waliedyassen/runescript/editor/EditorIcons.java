/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor;

import javax.swing.*;
import java.util.Objects;

/**
 * Contains all of the useful icons for the editor components.
 *
 * @author Walied K. Yassen
 */
public final class EditorIcons {

    /**
     * The icon of the folder node in the explorer tree.
     */
    public static final Icon FOLDER_ICON = loadIcon("icons/tree/folder.png");

    /**
     * The icon of the file node in the explorer tree.
     */
    public static final Icon FILE_ICON = loadIcon("icons/tree/file.png");

    /**
     * The icon of the script node in the explorer tree.
     */
    public static final Icon SCRIPT_ICON = loadIcon("icons/tree/script.png");

    /**
     * Attempts to load an {@link Icon} object from the resources folder.
     *
     * @param name
     *         the name of the icon image.
     *
     * @return the loaded {@link Icon} object.
     * @throws NullPointerException
     *         if we could not locate the icon in the resources folder.
     */
    private static Icon loadIcon(String name) {
        return new ImageIcon(Objects.requireNonNull(EditorIcons.class.getResource("/" + name), "Failed to find icon resource with the name: " + name));
    }

    private EditorIcons() {
        // NOOP
    }
}

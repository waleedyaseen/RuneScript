/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor;

import com.formdev.flatlaf.extras.FlatSVGIcon;
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
    public static final Icon FILETYPE_FOLDER_ICON = pngIcon("tree/folder");

    /**
     * The icon of the file node in the explorer tree.
     */
    public static final Icon FILETYPE_FILE_ICON = pngIcon("tree/file");

    /**
     * The icon of the script node in the explorer tree.
     */
    public static final Icon FILETYPE_SCRIPT_ICON = pngIcon("tree/script");

    /**
     * The icon of the config node in the explorer tree.
     */
    public static final Icon FILETYPE_CONFIG_ICON = pngIcon("tree/config");

    /**
     * The icon of the settings menu item.
     */
    public static final Icon SETTINGS = ideaIcon("settings");
    public static final Icon CLOSE_BUTTON = ideaIcon("close") ;
    public static final Icon CLOSE_BUTTON_HOVER = ideaIcon("close_hovered") ;

    /**
     * Returns the {@code SVG} Intellij IDEA icon with the specified {@code name}.
     *
     * @param name the name of the icon.
     * @return the {@link Icon} object.
     */
    private static Icon ideaIcon(String name) {
        return svgIcon("idea/" + name);
    }

    /**
     * Returns the {@code SVG} icon with the specified {@code name}.
     *
     * @param name the name of the icon.
     * @return the {@link Icon} object.
     */
    private static Icon svgIcon(String name) {
        return new FlatSVGIcon("icons/" + name + ".svg");
    }

    /**
     * Returns the {@code PNG} icon with the specified {@code name}.
     *
     * @param name the name of the icon.
     * @return the {@link Icon} object.
     */
    private static Icon pngIcon(String name) {
        return ResourceManager.getInstance().loadIcon("/icons/" + name + ".png");
    }

    private EditorIcons() {
        // NOOP
    }
}

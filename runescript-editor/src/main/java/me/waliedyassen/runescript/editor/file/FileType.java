/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.file;

import me.waliedyassen.runescript.editor.ui.editor.Editor;

import javax.swing.*;
import java.nio.file.Path;

/**
 * Represents a file type in our editor.
 *
 * @author Walied K. Yassen
 */
public interface FileType {

    /**
     * Creates an {@link Editor} for this file type.
     *
     * @param path the path of the file we are creating the editor for.
     * @return the created {@link Editor} type object instance.
     */
    Editor<?> createEditor(Path path);

    /**
     * Returns the name of the file type.
     *
     * @return the name of the file type.
     */
    String getName();

    /**
     * Returns the description of the file type.
     *
     * @return the description of the file type.
     */
    String getDescription();

    /**
     * Returns the extensions that are supported by this file type.
     *
     * @return the extensions that are supported by this file type.
     */
    String[] getExtensions();

    /**
     * Returns the icon of the file type.
     *
     * @return the icon of the file type.
     */
    Icon getIcon();
}

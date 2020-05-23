/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.file.impl;

import me.waliedyassen.runescript.editor.EditorIcons;
import me.waliedyassen.runescript.editor.file.FileType;
import me.waliedyassen.runescript.editor.ui.editor.Editor;

import javax.swing.*;
import java.nio.file.Path;

/**
 * The default file type that will be used in-case no file type was found.
 *
 * @author Walied K. Yassen
 */
public final class PlainFileType implements FileType {

    /**
     * {@inheritDoc}
     */
    @Override
    public Editor<?> createEditor(Path path) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Plain Text File";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Plain Text File";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getExtensions() {
        return new String[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Icon getIcon() {
        return EditorIcons.FILETYPE_FILE_ICON;
    }
}

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
import me.waliedyassen.runescript.editor.ui.editor.code.CodeEditor;

import javax.swing.*;
import java.nio.file.Path;

/**
 * Represent the RuneScript Source File file type.
 *
 * @author Walied K. Yassen
 */
public final class ScriptFileType implements FileType {

    /**
     * {@inheritDoc}
     */
    @Override
    public Editor<?> createEditor(Path path) {
        return new CodeEditor(this, path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "RuneScript Source File";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "RuneScript Source File";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getExtensions() {
        return new String[]{"rs2", "cs2"};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Icon getIcon() {
        return EditorIcons.FILETYPE_SCRIPT_ICON;
    }
}

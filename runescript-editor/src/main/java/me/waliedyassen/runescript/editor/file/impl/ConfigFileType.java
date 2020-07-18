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
import me.waliedyassen.runescript.type.PrimitiveType;

import javax.swing.*;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Represent the RuneScript Config file type.
 *
 * @author Walied K. Yassen
 */
public final class ConfigFileType implements FileType {

    /**
     * An array of all the extensions this type su
     */
    private static final String[] EXTENSIONS = Arrays
            .stream(PrimitiveType.values())
            .filter(PrimitiveType::isConfigType)
            .map(PrimitiveType::getRepresentation)
            .toArray(String[]::new);

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
        return "RuneScript Config File";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "RuneScript Config File";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getExtensions() {
        return EXTENSIONS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Icon getIcon() {
        return EditorIcons.FILETYPE_CONFIG_ICON;
    }
}

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.file.impl;

import lombok.var;
import me.waliedyassen.runescript.editor.Api;
import me.waliedyassen.runescript.editor.EditorIcons;
import me.waliedyassen.runescript.editor.file.FileType;
import me.waliedyassen.runescript.editor.ui.editor.Editor;
import me.waliedyassen.runescript.editor.ui.editor.project.ProjectEditor;

import javax.swing.*;
import java.nio.file.Path;

/**
 * Represents the RuneScript Project File file type.
 *
 * @author Walied K. Yassen
 */
public final class ProjectFileType implements FileType {

    /**
     * {@inheritDoc}
     */
    @Override
    public Editor<?> createEditor(Path path) {
        // TODO: Maybe use the path to confirm we are opening the modifying project.
        var currentProject = Api.getApi().getProjectManager().getCurrentProject().get();
        return new ProjectEditor(currentProject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "RuneScript Project File";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "RuneScript Project File";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getExtensions() {
        return new String[]{"rsproj"};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Icon getIcon() {
        return EditorIcons.FILETYPE_FILE_ICON;
    }
}

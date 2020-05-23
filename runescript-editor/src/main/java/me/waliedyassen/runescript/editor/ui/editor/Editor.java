/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor;

import javax.swing.*;

/**
 * The base class for all of the file editors in our system.
 *
 * @param <K> the key type of the editor.
 * @author Walied K. Yassen
 */
public abstract class Editor<K> {

    /**
     * Reloads the content of the editor.
     */
    public abstract void reload();

    /**
     * Saves the content of the editor.
     */
    public abstract void save();

    /**
     * Returns the view component of the editor.
     *
     * @return the view component of the editor.
     */
    public abstract JComponent getViewComponent();

    /**
     * Returns the icon of the editor.
     *
     * @return the icon of th4 editor.
     */
    public abstract Icon getIcon();

    /**
     * Returns the title of the editor.
     *
     * @return the title of the editor.
     */
    public abstract String getTitle();

    /**
     * Returns the tooltip of the editor.
     *
     * @return the tooltip of the editor.
     */
    public abstract String getTooltip();

    /**
     * Whether or not the editor has been modified.
     *
     * @return <code>true</code> if the editor is modified otherwise <code>false</code>.
     */
    public abstract boolean isModified();

    /**
     * Returns the key object which we use to reference the editor in collection.
     *
     * @return the key of the editor.
     */
    public abstract K getKey();
}

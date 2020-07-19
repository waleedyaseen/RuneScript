/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package me.waliedyassen.runescript.editor.ui.editor.tab;

import lombok.Getter;
import lombok.var;
import me.waliedyassen.runescript.editor.Api;
import me.waliedyassen.runescript.editor.shortcut.ShortcutManager;
import me.waliedyassen.runescript.editor.shortcut.common.CommonGroups;
import me.waliedyassen.runescript.editor.shortcut.common.CommonShortcuts;
import me.waliedyassen.runescript.editor.ui.dialog.DialogManager;
import me.waliedyassen.runescript.editor.ui.dialog.DialogResult;
import me.waliedyassen.runescript.editor.ui.editor.Editor;
import me.waliedyassen.runescript.editor.ui.menu.action.ActionSource;
import me.waliedyassen.runescript.editor.ui.menu.action.list.ActionList;

import javax.swing.*;

/**
 * A tab component for a single {@link Editor} object.
 *
 * @author Walied K. Yassen
 */
public final class EditorTab implements ActionSource {

    /**
     * The editor which this tab is for.
     */
    @Getter
    private final Editor<?> editor;

    /**
     * Constructs a new {@link EditorTab} type object instance.
     *
     * @param editor
     *         the editor which this tab is for.
     */
    public EditorTab(Editor<?> editor) {
        this.editor = editor;
        ShortcutManager.getInstance().bindShortcuts(CommonGroups.EDITOR, editor.getViewComponent(), this);
        reload();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateActions(ActionList actionList) {
        actionList.addAction("Save", CommonGroups.EDITOR.lookup(CommonShortcuts.EDITOR_SAVE_FILE))
                .withPredicate(action -> isModified());
        actionList.addSeparator();
        actionList.addAction("Close", CommonGroups.EDITOR.lookup(CommonShortcuts.EDITOR_CLOSE_FILE));
    }

    /**
     * Requests a close action for this tab.
     *
     * @return <code>true</code> if the tab was closed otherwise <code>false</code>.
     */
    public boolean requestClose() {
        if (checkModifySave()) {
            return false;
        }
        if (isModified()) {
            editor.restoreModification();
        }
        Api.getApi().getEditorView().removeTab(editor.getKey());
        return true;
    }

    /**
     * Asks the user to save the changes of the editor to the local disk if there was any changes.
     *
     * @return <code>true</code> if the the operation should be cancelled otherwise <code>false</code>.
     */
    public boolean checkModifySave() {
        if (isModified()) {
            var result = DialogManager.showCloseDialog("This file has unsaved changes. Do you want to save your changes before closing?");
            if (result == DialogResult.CANCEL) {
                return true;
            }
            if (result == DialogResult.YES) {
                save();
            }
        }
        return false;
    }

    /**
     * Checks whether or not the editor tab content has been modified compared to the local disk.
     *
     * @return <code>true</code> if it has been modified otherwise <code>false</code>.
     */
    private boolean isModified() {
        return editor.isModified();
    }

    /**
     * Reloads the content of the tab from the file from the local disk.
     */
    public void reload() {
        editor.reload();
    }

    /**
     * Saves the content of this file to the disk.
     */
    public void save() {
        editor.save();
    }

    /**
     * Returns the cached component of this editor tab or create a new one if there is none cached.
     *
     * @return the cached or created {@link JComponent} object.
     */
    public JComponent getViewComponent() {
        return editor.getViewComponent();
    }
}

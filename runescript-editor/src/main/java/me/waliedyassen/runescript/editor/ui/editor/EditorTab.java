/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package me.waliedyassen.runescript.editor.ui.editor;

import lombok.Getter;
import lombok.SneakyThrows;
import me.waliedyassen.runescript.editor.Api;
import me.waliedyassen.runescript.editor.shortcut.ShortcutManager;
import me.waliedyassen.runescript.editor.shortcut.common.CommonGroups;
import me.waliedyassen.runescript.editor.shortcut.common.CommonShortcuts;
import me.waliedyassen.runescript.editor.ui.dialog.DialogManager;
import me.waliedyassen.runescript.editor.ui.dialog.DialogResult;
import me.waliedyassen.runescript.editor.ui.menu.action.ActionSource;
import me.waliedyassen.runescript.editor.ui.menu.action.list.ActionList;
import me.waliedyassen.runescript.editor.util.MD5Util;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

/**
 * A single code editor tab in the editor view.
 *
 * @author Walied K. Yassen
 */
public final class EditorTab implements ActionSource {

    /**
     * The code area which contains the content of the file.
     */
    @Getter
    private final CodeArea codeArea;

    /**
     * The file path which the editor tab is editing.
     */
    @Getter
    private final Path path;

    /**
     * The disk MD5 checksum of the tab.
     */
    private byte[] diskChecksum;

    /**
     * Constructs a new {@link EditorTab} type object instance.
     *
     * @param path the file path on the local disk which this tab is for.
     * @throws IOException if anything occurs during the loading of the editor tab content.
     */
    public EditorTab(Path path) throws IOException {
        this.path = path;
        codeArea = new CodeArea(path);
        ShortcutManager.getInstance().bindShortcuts(CommonGroups.EDITOR, codeArea, this);
        ShortcutManager.getInstance().bindShortcuts(CommonGroups.EDITOR, getViewComponent(), this);
        reload();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateActions(ActionList actionList) {
        actionList.addAction("Save", CommonGroups.EDITOR.lookup(CommonShortcuts.EDITOR_SAVE_FILE))
                .withPredicate(action -> modified());
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
        Api.getApi().getEditorView().closeTab(path);
        return true;
    }

    /**
     * Asks the user to save the changes of the editor to the local disk if there was any changes.
     *
     * @return <code>true</code> if the the operation should be cancelled otherwise <code>false</code>.
     */
    public boolean checkModifySave() {
        if (modified()) {
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
    private boolean modified() {
        return !Arrays.equals(MD5Util.calculate(getCodeArea().getText().getBytes()), diskChecksum);
    }

    /**
     * Reloads the content of the tab from the file from the local disk.
     *
     * @throws IOException if anything occurs during reading the content of the file from the local disk.
     */
    public void reload() throws IOException {
        var data = Files.readAllBytes(path);
        try (var reader = new InputStreamReader(new ByteArrayInputStream(data))) {
            codeArea.read(reader, null);
        }
        diskChecksum = MD5Util.calculate(data);
    }

    /**
     * Saves the content of this file to the disk.
     */
    @SneakyThrows
    public void save() {
        Files.writeString(path, codeArea.getText(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        diskChecksum = MD5Util.calculate(codeArea.getText().getBytes());
    }

    /**
     * Returns the cached component of this editor tab or create a new one if there is none cached.
     *
     * @return the cached or created {@link JComponent} object.
     */
    public JComponent getViewComponent() {
        return codeArea.getViewPane();
    }
}

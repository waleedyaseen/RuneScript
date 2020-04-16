/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.dialog;

import me.waliedyassen.runescript.editor.Api;

import javax.swing.*;

/**
 * The dialog manager of the UI of the editor.
 *
 * @author Walied K. Yassen
 */
public final class DialogManager {

    /**
     * Shows a close dialog at the top level component.
     *
     * @param text
     *         the text of the dialog message.
     *
     * @return the {@link DialogResult result} of the dialog.
     */
    public static DialogResult showCloseDialog(String text) {
        var result = JOptionPane.showConfirmDialog(
                Api.getApi().getUi().getFrame(),
                text,
                "Close",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.ERROR_MESSAGE
        );
        return toDialogResult(result);
    }

    /**
     * Converts the specified AWT dialog {@code result} constant to a {@link DialogResult} enum constant.
     *
     * @param result
     *         the AWT dialog result constant.
     *
     * @return the {@link DialogResult} enum constant.
     */
    private static DialogResult toDialogResult(int result) {
        switch (result) {
            case JOptionPane.NO_OPTION:
                return DialogResult.NO;
            case JOptionPane.YES_OPTION:
                return DialogResult.YES;
            case JOptionPane.CANCEL_OPTION:
                return DialogResult.CANCEL;
            default:
                throw new IllegalStateException();
        }
    }

    private DialogManager() {
        // NOOP
    }
}

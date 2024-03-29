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
     * Asks the user to enter a directory or a file name.
     *
     * @param message the message to display for the user.
     * @return the name the user entered or {@code null} if the user did not enter anything.
     */
    public static String askForName(String message) {
        var result = JOptionPane.showInputDialog(
                Api.getApi().getUi().getFrame(),
                message);
        return result;
    }

    /**
     * Shows a close dialog at the top level component.
     *
     * @param text the text of the dialog message.
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
     * Shows a dialog with an error message at the top level component.
     *
     * @param title   the title of the dialog.
     * @param message the error message of the dialog.
     */
    public static void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(
                Api.getApi().getUi().getFrame(),
                message,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Converts the specified AWT dialog {@code result} constant to a {@link DialogResult} enum constant.
     *
     * @param result the AWT dialog result constant.
     * @return the {@link DialogResult} enum constant.
     */
    private static DialogResult toDialogResult(int result) {
        switch (result) {
            case JOptionPane.NO_OPTION:
                return DialogResult.NO;
            case JOptionPane.YES_OPTION:
                return DialogResult.YES;
            case JOptionPane.CANCEL_OPTION:
            case JOptionPane.CLOSED_OPTION:
                return DialogResult.CANCEL;
            default:
                throw new IllegalStateException("" + result);
        }
    }

    private DialogManager() {
        // NOOP
    }
}

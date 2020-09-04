/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.util;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.util.SystemInfo;
import lombok.extern.slf4j.Slf4j;
import me.waliedyassen.runescript.editor.settings.SettingsManager;

import javax.swing.*;

/**
 * Contains various utilities that are helpful and useful by our Look and Feel system.
 *
 * @author Walied K. Yassen
 */
@Slf4j
public final class LafUtil {

    /**
     * Sets-up the editor's Look and Feel system.
     */
    public static void setup() {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("You can only call setup() from the AWT events dispatch thread. Current thread is: " + Thread.currentThread().getName());
        }
        if (SystemInfo.isMacOS && System.getProperty("apple.laf.useScreenMenuBar") == null) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        try {
            UIManager.setLookAndFeel(SettingsManager.getLookAndFeelSettings().getPersistentState().getLookAndFeelName());
        } catch (Throwable e) {
            log.error("Failed to set the default Look and Feel", e);
        }
    }

    /**
     * Attempts to change the current Look and Feel of the UI to the specified Look and Feel class name. This function
     * will attempt to use animation for switching the look and feel and does nothing if the Look and Feel failed
     * to be changed or failed to be found.
     *
     * @param className the Look and Feel class name.
     */
    public static void changeLaf(String className) {
        FlatAnimatedLafChange.showSnapshot();
        try {
            UIManager.setLookAndFeel(className);
        } catch (Throwable ex) {
            log.error("Failed to apply the Look & Feel", ex);
        }
        FlatLaf.updateUI();
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    private LafUtil() {
        // NOOP
    }
}

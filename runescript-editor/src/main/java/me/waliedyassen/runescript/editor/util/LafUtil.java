/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.util;

import com.alee.laf.WebLookAndFeel;

import javax.swing.*;

/**
 * Contains various utilities that are helpful and useful by our Look and Feel system.
 *
 * @author Walied K. Yassen
 */
public final class LafUtil {

    /**
     * Sets-up the editor's Look and Feel system.
     */
    public static void setup() {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("You can only call setup() from the AWT events dispatch thread. Current thread is: " + Thread.currentThread().getName());
        }
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        WebLookAndFeel.install();
    }

    private LafUtil() {
        // NOOP
    }
}

/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui;

import com.alee.managers.style.StyleId;
import lombok.Getter;
import me.waliedyassen.runescript.editor.property.impl.StringProperty;

import javax.swing.*;

/**
 * The user-interface of the RuneScript Editor.
 *
 * @author Walied K. Yassen
 */
public final class EditorUI {

    /**
     * The main frame of the user-interface.
     */
    private final JFrame frame = new JFrame();

    /**
     * The current frame title property.
     */
    @Getter
    private final StringProperty title = new StringProperty("");

    /**
     * Initialises the user-interface.
     */
    public void initialise() {
        initialiseComponents();
        initialiseProperties();
    }

    /**
     * Initialises the components of the editor.
     */
    private void initialiseComponents() {
        title.addListener(frame::setTitle);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        // This makes it so it won't style the frame using the WebLAF style.
        frame.getRootPane().putClientProperty(StyleId.STYLE_PROPERTY, StyleId.frame);
    }

    /**
     * Initialises the properties of the editor.
     */
    private void initialiseProperties() {
        title.set("RuneScript Editor");
    }

    /**
     * Shows the user-interface if it is not visible.
     */
    public void show() {
        if (frame.isVisible()) {
            return;
        }
        frame.setVisible(true);
    }
}

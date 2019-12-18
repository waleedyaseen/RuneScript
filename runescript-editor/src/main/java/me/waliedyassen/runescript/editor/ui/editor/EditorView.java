/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.editor;

import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;

/**
 * The editor main-view component.
 *
 * @author Walied K. Yassen
 */
public final class EditorView extends JPanel {

    /**
     * The docking {@code ID} for the editor docking component.
     */
    public static final String DOCK_ID = "editor.dock";

    /**
     * The documents pane for the code areas.
     */
    private final JTabbedPane tabbedPane = new JTabbedPane();

    /**
     * Constructs a new {@link EditorView} type object instance.
     */
    public EditorView() {
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        tabbedPane.addTab("File 1", new RTextScrollPane(new CodeArea()));
        tabbedPane.addTab("File 2", new RTextScrollPane(new CodeArea()));
    }
}

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package me.waliedyassen.runescript.editor.ui.editor;

import me.waliedyassen.runescript.editor.ui.tabbedpane.TabComponent;

import javax.swing.*;
import java.awt.*;

/**
 * @author Walied K. Yassen
 */
public final class EditorTabComponent extends TabComponent {

    /**
     * The editor tab which this component is for.
     */
    private final EditorTab tab;

    /**
     * The title label component of the tab component.
     */
    private final JLabel label;

    /**
     * Constructs a new {@link EditorTab} type object instance.
     *
     * @param tab the tab which the tab component is for.
     */
    public EditorTabComponent(EditorTab tab) {
        super(new BorderLayout(2, 0));
        this.tab = tab;
        // Create and add the title  button.
        label = new JLabel(tab.getPath().getFileName().toString());
        add(label, BorderLayout.CENTER);
        // Create and add the close button.
        var button = new JButton("X");
        button.addActionListener(evt -> tab.requestClose());
        button.setToolTipText("Close");
        add(button, BorderLayout.EAST);
        // Set the tooltip of the tab component.
        setTooltip(tab.getPath().toString());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setTitle(String title) {
        label.setText(title);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTooltip(String tooltip) {
        setToolTipText(tooltip);
    }
}

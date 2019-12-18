/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.frame.top;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * The top ui of the main frame.
 *
 * @author Walied K. Yassen
 */
public final class TopUi extends JPanel {

    /**
     * The title bar of the main frame.
     */
    @Getter
    private final TitleBar titleBar;

    /**
     * The tool bar of the main frame.
     */
    @Getter
    private final ToolBar toolBar;

    /**
     * Constructs a new {@link TopUi} type object instance.
     *
     * @param titleBar
     *         the title bar of the main frame.
     * @param toolBar
     *         the tool bar of the main frame.
     */
    public TopUi(TitleBar titleBar, ToolBar toolBar) {
        this.titleBar = titleBar;
        this.toolBar = toolBar;
        initialiseUi();
    }

    /**
     * Initialises the UI.
     */
    private void initialiseUi() {
        setLayout(new BorderLayout());
        setupToolBar();
        add(titleBar, BorderLayout.NORTH);
        add(toolBar, BorderLayout.SOUTH);
    }

    /**
     * Sets-up the tool bar.
     */
    private void setupToolBar() {
        toolBar.add(new JButton("Cut"));
        toolBar.add(new JButton("Copy"));
        toolBar.add(new JButton("Paste"));
        toolBar.setFloatable(false);
    }
}

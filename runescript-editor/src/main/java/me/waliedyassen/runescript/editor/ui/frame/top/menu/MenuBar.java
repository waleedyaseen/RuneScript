/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.frame.top.menu;

import javax.swing.*;
import java.awt.*;

/**
 * The menu bar of the main frame.
 *
 * @author Walied K. Yassen
 */
public final class MenuBar extends JMenuBar {

    /**
     * The height of the menu bar.
     */
    public static final int HEIGHT = 28;

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getMinimumSize() {
        var size = super.getMinimumSize();
        return new Dimension(size.width, HEIGHT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getMaximumSize() {
        var size = super.getMaximumSize();
        return new Dimension(size.width, HEIGHT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize() {
        var size = super.getPreferredSize();
        return new Dimension(size.width, HEIGHT);
    }
}

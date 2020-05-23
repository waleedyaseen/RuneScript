/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.tabbedpane;

import javax.swing.*;
import java.awt.*;

/**
 * Represents a tab component for the tabbed pane.
 *
 * @author Walied K. Yassen
 */
public abstract class TabComponent extends JPanel {

    /**
     * Constructs a new {@link TabComponent} type object instance.
     *
     * @param layout the layout manager the component follows.
     */
    public TabComponent(LayoutManager layout) {
        super(layout);
    }

    /**
     * Updates the title of the tab component.
     *
     * @param title the new tile of the tab component.
     */
    public abstract void setTitle(String title);

    /**
     * Updates tooltip of the tab component.
     *
     * @param tooltip the new tooltip of the tab component.
     */
    public abstract void setTooltip(String tooltip);
}

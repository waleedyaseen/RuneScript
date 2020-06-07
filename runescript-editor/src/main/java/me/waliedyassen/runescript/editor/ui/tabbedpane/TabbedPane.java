/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package me.waliedyassen.runescript.editor.ui.tabbedpane;

import lombok.var;

import javax.swing.*;

/**
 * Represents a UI tabbed pane implementation.
 *
 * @author Walied K. Yassen
 */
public final class TabbedPane extends JTabbedPane {

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTitleAt(int index, String title) {
        var component = getTabComponentAt(index);
        if (component instanceof TabComponent) {
            ((TabComponent) component).setTitle(title);
        } else {
            super.setTitleAt(index, title);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setToolTipTextAt(int index, String tooltip) {
        var component = getTabComponentAt(index);
        if (component instanceof TabComponent) {
            ((TabComponent) component).setTooltip(tooltip);
        } else {
            super.setToolTipTextAt(index, tooltip);
        }
    }
}

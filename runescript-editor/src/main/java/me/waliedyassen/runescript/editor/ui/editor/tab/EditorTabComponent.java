/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package me.waliedyassen.runescript.editor.ui.editor.tab;

import lombok.var;
import me.waliedyassen.runescript.editor.EditorIcons;
import me.waliedyassen.runescript.editor.ui.tabbedpane.TabComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * A {@link TabComponent} implementation of the editor view.
 *
 * @author Walied K. Yassen
 */
public final class EditorTabComponent extends TabComponent {

    /**
     * The title label component of the tab component.
     */
    private final JLabel label;

    /**
     * Constructs a new {@link EditorTab} type object instance.
     *
     * @param tab the tab which the tab component is for.
     */
    public EditorTabComponent(EditorTab tab, MouseListener tabsMouseListener) {
        super(new BorderLayout(2, 0));
        // Create and add the title  button.
        label = new JLabel(tab.getEditor().getTitle());
        var icon = tab.getEditor().getIcon();
        if (icon != null) {
            label.setIcon(icon);
        }
        label.addMouseListener(tabsMouseListener);
        add(label, BorderLayout.CENTER);
        // Create and add the close button.
        var button = new EditorTabIcon(EditorIcons.CLOSE_BUTTON, EditorIcons.CLOSE_BUTTON_HOVER, tab::requestClose);
        button.setToolTipText("Close");
        button.addMouseListener(tabsMouseListener);
        add(button, BorderLayout.EAST);
        // Set the tooltip of the tab component.
        setTooltip(tab.getEditor().getTooltip());
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

    /**
     * Icon component for the editor tab component.
     *
     * @author Walied K. Yassen
     */
    private static final class EditorTabIcon extends JLabel implements MouseListener, FocusListener {

        /**
         * The icon of the normal state.
         */
        private final Icon icon;

        /**
         * The icon of the hovered state.
         */
        private final Icon hoverIcon;

        /**
         * The action to perform when the mouse is pressed.
         */
        private final Runnable action;

        /**
         * Whether or not the icon is currently hovered.
         */
        private boolean hovered;

        /**
         * Whether or not the icon is currently pressed.
         */
        private boolean pressed;

        /**
         * Constructs a new {@link EditorTabComponent} type object instance.
         *
         * @param icon      the icon of the normal state.
         * @param hoverIcon the icon of the hovered state.
         * @param action    the action to perform when the icon is pressed.
         */
        public EditorTabIcon(Icon icon, Icon hoverIcon, Runnable action) {
            setOpaque(false);
            this.icon = icon;
            this.hoverIcon = hoverIcon;
            this.action = action;
            addMouseListener(this);
            addFocusListener(this);
            updateIcon();
        }

        /**
         * Updates the icon of the tab component.
         */
        private void updateIcon() {
            setIcon(hovered || pressed ? hoverIcon : icon);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            // NOOP
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mousePressed(MouseEvent e) {
            pressed = true;
            updateIcon();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            pressed = false;
            updateIcon();
            if (contains(e.getPoint())) {
                action.run();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseEntered(MouseEvent e) {
            hovered = true;
            updateIcon();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseExited(MouseEvent e) {
            hovered = false;
            updateIcon();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void focusGained(FocusEvent e) {
            updateIcon();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void focusLost(FocusEvent e) {
            hovered = pressed = false;
            updateIcon();
        }
    }
}

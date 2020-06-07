/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.frame.top.control;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.editor.ui.frame.top.menu.MenuBar;

import javax.accessibility.AccessibleContext;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * A styled control button component for a window.
 *
 * @author Walied K. Yassen
 */
public final class ControlButton extends JButton {

    /**
     * The type of the control button.
     */
    @Getter
    private ControlButtonType type;

    /**
     * The style handler of the control button.
     */
    private final StyleHandler styleHandler;

    /**
     * Constructs a new {@link ControlButton} type object instance.
     *
     * @param type   the type of the control button.
     * @param action the action of the control button.
     */
    public ControlButton(ControlButtonType type, Action action) {
        super((String) null);
        this.type = type;
        setAction(action);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setPreferredSize(new Dimension(47, MenuBar.HEIGHT));
        addMouseListener(styleHandler = new StyleHandler(this));
        putClientProperty(AccessibleContext.ACCESSIBLE_NAME_PROPERTY, type.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paint(Graphics g) {
        styleHandler.update();
        super.paint(g);
    }

    /**
     * Updates the type of the control button.
     *
     * @param type the type of the control button.
     */
    public void setType(ControlButtonType type) {
        this.type = type;
        repaint();
    }

    /**
     * The style handler of the {@link ControlButton} component.
     *
     * @author Walied K. Yassen
     */
    @RequiredArgsConstructor
    // TODO: Extend this to be a global style handler.
    static final class StyleHandler implements MouseListener {

        /**
         * The button which this style handler is for.
         */
        private final ControlButton button;

        /**
         * Whether or not the button is currently hovered.
         */
        private boolean hovered;

        /**
         * Whether or not the button is currently pressed.
         */
        private boolean pressed;

        /**
         * Updates the control button style.
         */
        private void update() {
            final var colorCode = button.getType().getColor();
            Color color;
            if (pressed) {
                color = colorCode == null ? button.getParent().getBackground().brighter() : colorCode.darker();
            } else if (hovered) {
                color = colorCode == null ? button.getParent().getBackground().brighter() : colorCode;
            } else {
                color = button.getParent().getBackground();
            }
            button.setIcon(hovered || pressed ? button.getType().getHoverIcon() : button.getType().getIcon());
            button.setBackground(color);
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
            update();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            pressed = false;
            update();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseEntered(MouseEvent e) {
            hovered = true;
            update();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseExited(MouseEvent e) {
            hovered = false;
            update();
        }
    }
}

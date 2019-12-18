/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.frame.top;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.editor.property.impl.StringProperty;
import me.waliedyassen.runescript.editor.ui.frame.top.control.ControlButton;
import me.waliedyassen.runescript.editor.ui.frame.top.control.ControlButtonType;
import me.waliedyassen.runescript.editor.ui.frame.top.menu.MenuBar;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A title bar with a menu bar for dialogs and frames.
 *
 * @author Walied K. Yassen
 */
public final class TitleBar extends JPanel {

    /**
     * The current text of the title bar.
     */
    @Getter
    private final StringProperty text = new StringProperty();

    /**
     * The menu bar which is attached to the title bar.
     */
    @Getter
    private final MenuBar menuBar = new MenuBar();

    /**
     * The window which the title bar is for.
     */
    private final JFrame frame;

    /**
     * Constructs a new {@link TitleBar} type object instance.
     *
     * @param frame
     *         the frame which the title bar is for.
     */
    public TitleBar(JFrame frame) {
        this.frame = frame;
        setLayout(new MigLayout("fill, ins 0", "[][center, grow][]"));
        setupMenuBar();
        setupTitle();
        setupControls();
        WindowMoveHandler.install(this, frame);
    }

    /**
     * Sets-up the menu bar.
     */
    private void setupMenuBar() {
        menuBar.setBorderPainted(false);
        menuBar.setMinimumSize(new Dimension());
        add(menuBar);
    }

    /**
     * Sets-up the title.
     */
    private void setupTitle() {
        var titleLabel = new JLabel();
        text.bind(titleLabel::setText);
        add(titleLabel);
    }

    /**
     * Sets-up the title controls.
     */
    private void setupControls() {
        var panel = new JPanel(new MigLayout("gap 0, ins 0"));
        panel.add(new ControlButton(ControlButtonType.MINIMISE, new AbstractAction() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setExtendedState(frame.getExtendedState() | JFrame.ICONIFIED);
            }
        }));
        panel.add(new ControlButton(ControlButtonType.MAXIMISE, new AbstractAction() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                if ((frame.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
                    frame.setExtendedState(frame.getExtendedState() & ~JFrame.MAXIMIZED_BOTH);
                } else {
                    frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
                }
            }
        }));
        panel.add(new ControlButton(ControlButtonType.CLOSE, new AbstractAction() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        }));
        add(panel);
    }


    /**
     * A window move handler, it gives a specific component(s) the ability to move the window when they are dragged.
     *
     * @author Walied K. Yassen
     */
    @RequiredArgsConstructor
    static final class WindowMoveHandler extends MouseAdapter {

        /**
         * The window which will be moved.
         */
        private final Window window;

        /**
         * The origin point
         */
        private Point origin;

        /**
         * Installs the movement handler on the specified component.
         *
         * @param component
         *         the component to install the handler on.
         */
        void install(JComponent component) {
            component.addMouseListener(this);
            component.addMouseMotionListener(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mousePressed(MouseEvent e) {
            if (origin != null) {
                return;
            }
            origin = e.getPoint();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            origin = null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseDragged(MouseEvent e) {
            window.setBounds(window.getX() + e.getX() - origin.x, window.getY() + e.getY() - origin.y, window.getWidth(), window.getHeight());
        }

        /**
         * Installs the {@link WindowMoveHandler} on the specified {@link JComponent component}.
         *
         * @param component
         *         the component to install the movement handler on.
         * @param frame
         *         the frame which will be moved by the movement handler.
         */
        static void install(JComponent component, JFrame frame) {
            new WindowMoveHandler(frame).install(component);
        }
    }
}

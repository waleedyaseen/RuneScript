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
import lombok.var;
import me.waliedyassen.runescript.editor.property.impl.StringProperty;
import me.waliedyassen.runescript.editor.ui.frame.top.control.ControlButton;
import me.waliedyassen.runescript.editor.ui.frame.top.control.ControlButtonType;
import me.waliedyassen.runescript.editor.ui.frame.top.menu.MenuBar;
import me.waliedyassen.runescript.editor.util.ex.SwingUtilitiesEx;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A title bar with a menu bar for dialogs and frames.
 *
 * @author Walied K. Yassen
 */
public final class TitleBar extends JPanel implements ComponentListener {

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
     * The panel which contains all of the controls;.
     */
    private final JPanel controlsPanel = new JPanel();

    /**
     * The bounds of the hit test spots of the title bar.
     */
    @Getter
    private final List<Rectangle> hitTestBounds = new CopyOnWriteArrayList<>();

    /**
     * The window which the title bar is for.
     */
    private final JFrame frame;

    /**
     * Constructs a new {@link TitleBar} type object instance.
     *
     * @param frame the frame which the title bar is for.
     */
    public TitleBar(JFrame frame) {
        this.frame = frame;
        setLayout(new MigLayout("fill, ins 0", "[][center, grow][]"));
        setupMenuBar();
        setupTitle();
        setupControls();
    }

    /**
     * Sets-up the menu bar.
     */
    private void setupMenuBar() {
        menuBar.setBorderPainted(false);
        menuBar.setMinimumSize(new Dimension());
        add(menuBar);
        addComponentListener(this);
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
        controlsPanel.setLayout(new MigLayout("gap 0, ins 0"));
        controlsPanel.add(new ControlButton(ControlButtonType.MINIMISE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setExtendedState(frame.getExtendedState() | JFrame.ICONIFIED);
            }
        }));
        controlsPanel.add(new ControlButton(ControlButtonType.MAXIMISE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                var controlButton = (ControlButton) e.getSource();
                if (controlButton.getType() == ControlButtonType.MAXIMISE) {
                    controlButton.setType(ControlButtonType.RESTORE);
                    frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
                } else {
                    controlButton.setType(ControlButtonType.MAXIMISE);
                    frame.setExtendedState(frame.getExtendedState() & ~JFrame.MAXIMIZED_BOTH);
                }
            }
        }));
        controlsPanel.add(new ControlButton(ControlButtonType.CLOSE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        }));
        add(controlsPanel);
    }

    /**
     * Updates the hit test spots boundaries.
     */
    public void updateHitTestBounds() {
        hitTestBounds.clear();
        hitTestBounds.add(SwingUtilitiesEx.getComponentHitTestBounds(menuBar));
        hitTestBounds.add(SwingUtilitiesEx.getComponentHitTestBounds(controlsPanel));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void componentResized(ComponentEvent e) {
        updateHitTestBounds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void componentMoved(ComponentEvent e) {
        updateHitTestBounds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void componentShown(ComponentEvent e) {
        updateHitTestBounds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void componentHidden(ComponentEvent e) {
        updateHitTestBounds();
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
         * @param component the component to install the handler on.
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
         * @param component the component to install the movement handler on.
         * @param frame     the frame which will be moved by the movement handler.
         */
        static void install(JComponent component, JFrame frame) {
            new WindowMoveHandler(frame).install(component);
        }
    }
}


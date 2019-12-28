/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.status;

import lombok.Getter;
import me.waliedyassen.runescript.editor.property.impl.StringProperty;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * The RuneScript Editor status bar.
 *
 * @author Walied K. Yassen
 */
public final class StatusBar extends JPanel {

    /**
     * The current status text of the status bar.
     */
    @Getter
    private final StringProperty text = new StringProperty();

    /**
     * Constructs a new {@link StatusBar} type object instance.
     */
    public StatusBar() {
        setLayout(new MigLayout("fill, insets 3", "[grow][]"));
        setupLabel();
        setupMemory();
    }

    /**
     * Sets up the label at teh start of the status bar.
     */
    private void setupLabel() {
        var label = new JLabel();
        text.addListener(label::setText);
        add(label);
    }

    /**
     * Sets-up the memory bar at the ned of the status bar.
     */
    private void setupMemory() {
        add(new MemoryBar());
    }

    /**
     * A memory bar component which indicates how much memory the application is currently using.
     *
     * @author Walied K. Yassen
     */
    static final class MemoryBar extends JComponent implements MouseListener, Runnable {

        /**
         * The interval (in milliseconds) between each <code>repaint()</code> calls.
         */
        private static final int PAINT_INTERVAL = 1000;

        /**
         * The interval (in milliseconds) between each <code>System.gc()</code> calls.
         */
        private static final int GC_INTERVAL = 30000;

        /**
         * The tooltip template of the component.
         */
        private static final String TOOLTIP = "Max Memory: %dM\nAllocated Memory: %dM\nUsed Memory: %dM\n\nClick to force a Garbage Collection";

        /**
         * the last updated total memory of the application.
         */
        @Getter
        private final long maxMemory;

        /**
         * the last updated total memory of the application.
         */
        @Getter
        private volatile long totalMemory;

        /**
         * The last updated used memory of the application.
         */
        @Getter
        private volatile long usedMemory;

        /**
         * Whether or not a garbage collection was requested.
         */
        private volatile boolean gcRequested;

        /**
         * The last time the garbage collect was performed.
         */
        private long lastGcTime;

        /**
         * Whether or not the mouse is currently pressed on the memory bar.
         */
        private boolean held;

        /**
         * Constructs a new {@link MemoryBar} type object instance.
         */
        public MemoryBar() {
            // Cache the maximum memory allowed in the JVM.
            maxMemory = Runtime.getRuntime().maxMemory();
            // Attach the mouse listener to the component.
            addMouseListener(this);
            // Create the thread which is responsible for updating the status bar.
            var thread = new Thread(this, "MemoryBarUpdater");
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.setDaemon(true);
            thread.start();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("InfiniteLoopStatement")
        public void run() {
            while (true) {
                totalMemory = Runtime.getRuntime().totalMemory();
                usedMemory = totalMemory - Runtime.getRuntime().freeMemory();
                setToolTipText(String.format(TOOLTIP, maxMemory / 1048576L, totalMemory / 1048576L, usedMemory / 1048576L));
                repaint();
                if (gcRequested || lastGcTime + GC_INTERVAL < System.currentTimeMillis()) {
                    System.gc();
                    lastGcTime = System.currentTimeMillis();
                    gcRequested = false;
                }
                try {
                    Thread.sleep(PAINT_INTERVAL);
                } catch (InterruptedException ignored) {
                    // NOOP
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void paint(Graphics g) {
            if (totalMemory == 0) {
                return;
            }
            final var font = UIManager.getFont("ProgressBar.font");
            final var background = held ? UIManager.getColor("Panel.background").darker() : UIManager.getColor("Panel.background");
            final var foreground = held ? UIManager.getColor("Panel.foreground").darker() : UIManager.getColor("Panel.foreground");
            final var paintOffset = 1;
            final var paintWidth = getWidth() - paintOffset * 2;
            final var paintHeight = getHeight() - paintOffset * 2;
            // Paint the border of the memory bar.
            g.setColor(background.darker());
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            // Paint the background of the memory bar.
            g.setColor(background);
            g.fillRect(paintOffset, paintOffset, paintWidth, paintHeight);
            // Paint the fill of the memory bar.
            g.setColor(background.brighter());
            g.fillRect(paintOffset, paintOffset, (int) ((usedMemory * paintWidth) / totalMemory), paintHeight);
            // Paint the text of the memory bar.
            g.setFont(font);
            g.setColor(foreground);
            var text = usedMemory / 1048576L + " of " + totalMemory / 1048576L + "M";
            g.drawString(text, paintWidth / 2 - g.getFontMetrics().stringWidth(text) / 2, 15);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(100, 22);
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
            held = true;
            repaint();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            held = false;
            gcRequested = true;
            repaint();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseEntered(MouseEvent e) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseExited(MouseEvent e) {
            setCursor(Cursor.getDefaultCursor());
        }
    }

}

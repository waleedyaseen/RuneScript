/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.frame;

import lombok.Getter;
import me.waliedyassen.runescript.editor.ui.frame.nativeSupport.NativeWindowSupport;
import me.waliedyassen.runescript.editor.ui.frame.top.TitleBar;

import javax.swing.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * A decorated {@link JFrame} with custom title bar.
 *
 * @author Walied K. Yassen
 */
public final class TitledFrame extends JFrame implements ComponentListener {

    /**
     * The resize border thickness of the titled frame.
     */
    public static final int RESIZE_BORDER_THICKNESS = 2;

    /**
     * The native support of the titled frame.
     */
    private final NativeWindowSupport nativeSupport;

    /**
     * The title bar of the frame.
     */
    @Getter
    private final TitleBar titleBar;

    /**
     * Constructs a new {@link TitledFrame} type object instance.
     */
    public TitledFrame() {
        nativeSupport = NativeWindowSupport.createNativeWindowSupport(this);
        titleBar = new TitleBar(this);
        addComponentListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (nativeSupport != null) {
            nativeSupport.onSetVisible(visible);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void componentResized(ComponentEvent e) {
        titleBar.updateHitTestBounds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void componentMoved(ComponentEvent e) {
        titleBar.updateHitTestBounds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void componentShown(ComponentEvent e) {
        titleBar.updateHitTestBounds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void componentHidden(ComponentEvent e) {
        titleBar.updateHitTestBounds();
    }
}

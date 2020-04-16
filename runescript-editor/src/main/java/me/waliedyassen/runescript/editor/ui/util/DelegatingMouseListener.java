/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.util;

import lombok.RequiredArgsConstructor;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * A delegating {@link MouseListener} implementation that delegates all of the calls to another specific {@link
 * MouseListener} object.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public abstract class DelegatingMouseListener implements MouseListener {

    /**
     * The delegate listener which we want to call.
     */
    private final MouseListener delegate;

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        delegate.mouseClicked(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(MouseEvent e) {
        delegate.mousePressed(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        delegate.mouseReleased(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        delegate.mouseEntered(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(MouseEvent e) {
        delegate.mouseExited(e);
    }
}
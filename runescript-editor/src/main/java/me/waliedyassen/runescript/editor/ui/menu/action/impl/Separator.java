/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.menu.action.impl;

import me.waliedyassen.runescript.editor.ui.menu.action.Action;

import javax.swing.*;

/**
 * An {@link Action} implementation which creates a {@link JPopupMenu.Separator} object.
 *
 * @author Walied K. Yassen
 */
public final class Separator implements Action {

    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent createComponent() {
        return new JPopupMenu.Separator();
    }
}

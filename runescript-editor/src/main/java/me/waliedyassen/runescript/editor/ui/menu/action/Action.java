/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.menu.action;

import javax.swing.*;

/**
 * The base class for every menu action in our system.
 *
 * @author Walied K. Yassen
 */
public interface Action {

    /**
     * Creates a new {@link JComponent} object which represents the action.
     *
     * @return the created {@link JComponent} object.
     */
    JComponent createComponent();
}

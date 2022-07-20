/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.menu.action.impl;

import me.waliedyassen.runescript.editor.ui.menu.action.Action;
import me.waliedyassen.runescript.editor.ui.menu.action.list.ActionList;

import javax.swing.*;

/**
 * An {@link Action} implementation which creates a {@link JMenu} object.
 *
 * @author Walied K. Yassen
 */
public final class Menu extends ActionList implements Action {

    /**
     * The title of the menu.
     */
    private final String title;

    /**
     * Constructs a new {@link Menu} type object instance.
     *
     * @param source
     */
    public Menu(Object source, String title) {
        super(source);
        this.title = title;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent createComponent() {
        var menu = new JMenu(title);
        createComponents().forEach(menu::add);
        return menu;
    }
}

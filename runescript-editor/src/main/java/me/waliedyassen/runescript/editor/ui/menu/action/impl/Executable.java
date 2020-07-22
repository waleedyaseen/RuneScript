/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.menu.action.impl;

import lombok.Data;
import lombok.var;
import me.waliedyassen.runescript.editor.shortcut.UiAction;
import me.waliedyassen.runescript.editor.ui.menu.action.Action;

import javax.swing.*;
import java.util.function.Predicate;

/**
 * An executable {@link Action} implementation, this implementation creates a {@link JMenuItem} component.
 *
 * @author Walied K. Yassen
 */
@Data
public final class Executable implements Action {

    /**
     * The title of the action.
     */
    private final String title;

    /**
     * T he shortcut of the action.
     */
    private final KeyStroke keyStroke;

    /**
     * A callback which is called when the action is executed.
     */
    private final UiAction callback;

    /**
     * The source of the UI executable action.
     */
    private final Object source;

    /**
     * Whether or not this executable action is disabled (grayed out).
     */
    private Predicate<Executable> predicate;

    /**
     * Sets the enabled predicate of the executable action.
     *
     * @param predicate the predicate which determines the enabld status of the executable.
     */
    public void withPredicate(Predicate<Executable> predicate) {
        this.predicate = predicate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent createComponent() {
        var item = new JMenuItem(title);
        var enabled = predicate == null || predicate.test(this);
        item.setEnabled(enabled);
        if (keyStroke != null) {
            item.setAccelerator(keyStroke);
        }
        item.addActionListener((evt) -> {
            if (!enabled) {
                return;
            }
            callback.execute(source);
        });
        return item;
    }
}

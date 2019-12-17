/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.menu.action.list;

import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.editor.ui.menu.action.Action;
import me.waliedyassen.runescript.editor.ui.menu.action.impl.Executable;
import me.waliedyassen.runescript.editor.ui.menu.action.impl.Separator;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An action list which simply holds a bunch of {@link Action} objects with additional utilities to utilise these these
 * actions and convert them into component form.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class ActionList {

    /**
     * A list of all the actions that are added to this list.
     */
    private final List<Action> actions = new ArrayList<>();

    /**
     * Creates a {@link JPopupMenu} of all the actions that are currently in this actions list.
     *
     * @return the created {@link JPopupMenu} component.
     */
    public JPopupMenu createPopupMenu() {
        var popup = new JPopupMenu();
        createComponents().forEach(popup::add);
        return popup;
    }

    /**
     * Adds a new separator action to the actions list.
     */
    public void addSeparator() {
        add(new Separator());
    }

    /**
     * Adds a new executable action to the actions list.
     *
     * @param title
     *         the title of the action to display on the menu component.
     * @param callback
     *         the callback of the action which is called when the action is executed.
     */
    public void addAction(String title, Runnable callback) {
        add(new Executable(title, callback));
    }

    /**
     * Adds a new {@link Action} to this actions list.
     *
     * @param action
     *         the action to add to this actions list.
     */
    public void add(Action action) {
        if (actions.contains(action)) {
            throw new IllegalArgumentException("The specified action is already added to this list");
        }
        actions.add(action);
    }

    /**
     * Loops through all of the {@link Action action}s in this list and execute {@link Action#createComponent()} on each
     * one of them, then collect them all into one {@link List}.
     *
     * @return A {@link List} object which holds all of the created {@link JComponent} objects.
     */
    private List<JComponent> createComponents() {
        return actions.stream().map(Action::createComponent).collect(Collectors.toList());
    }

    /**
     * Checks whether or not this actions list is currently empty.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public boolean isEmpty() {
        return actions.isEmpty();
    }
}

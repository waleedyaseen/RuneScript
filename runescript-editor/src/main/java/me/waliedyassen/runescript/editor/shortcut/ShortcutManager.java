/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.shortcut;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * The shortcut manager of the RuneScript Editor.
 *
 * @author Walied K. Yassen
 */
@Slf4j
public final class ShortcutManager {

    /**
     * The singleton instance of the shortcut manager type.
     */
    @Getter
    private static final ShortcutManager instance = new ShortcutManager();

    /**
     * A map of all the registered shortcuts (the shortcut name is the key, the shortcut object is the value).
     */
    private final Map<ShortcutGroup<?>, Map<String, Shortcut>> shortcuts = new HashMap<>();

    /**
     * Binds all of the shortcuts in the specified {@link ShortcutGroup} to the specified {@link JComponent}.
     *
     * @param group
     *         the group to bind all of its shortcuts.
     * @param component
     *         the component to bind the shortcuts to.
     * @param source
     *         the source of the shortcut to pass to the shortcut action.
     */
    public void bindShortcuts(ShortcutGroup<?> group, JComponent component, Object source) {
        var shortcuts = this.shortcuts.get(group);
        if (shortcuts == null) {
            return;
        }
        if (group.getListeningComponents().contains(component)) {
            throw new IllegalArgumentException("The specified component is already listening to that shortcut group");
        }
        group.getListeningComponents().add(component);
        shortcuts.values().forEach(shortcut -> bindShortcut(group, shortcut, component, source));
    }

    /**
     * Binds the specified {@link Shortcut} to the specified {@link JComponent}.
     *
     * @param group
     *         the owner shortcut group of the shortcut.
     * @param shortcut
     *         the shortcut which we want to bind.
     * @param component
     *         the component which we want to bind the shortcut to.
     * @param source
     *         the source of the shortcut to pass to the shortcut action.
     */
    private void bindShortcut(ShortcutGroup<?> group, Shortcut shortcut, JComponent component, Object source) {
        var actionKey = "shortcut/" + group.getKey() + "/" + shortcut.getName();
        component.getInputMap().put(shortcut.getKeyStroke(), actionKey);
        component.getActionMap().put(actionKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shortcut.getAction().execute(source);
            }
        });
    }

    /**
     * Creates a new {@link ShortcutGroup} with the specified key {@code value}.
     *
     * @param value
     *         the key value of the shortcut group.
     * @param <T>
     *         the shortcut group key value type.
     *
     * @return the created {@link ShortcutGroup} object.
     * @throws IllegalArgumentException
     *         if another shortcut with the same key value was already created before.
     */
    public <T> ShortcutGroup<T> createGroup(@NonNull T value) {
        var group = new ShortcutGroup<>(value);
        if (shortcuts.containsKey(group)) {
            throw new IllegalArgumentException("There is a shortcut group with the same specified key already created");
        }
        log.trace("Creating shortcut group: " + value);
        shortcuts.put(group, new HashMap<>());
        return group;
    }

    /**
     * Adds a new shortcut to the shortcut manager.
     *
     * @param group
     *         the group object of the shortcut.
     * @param name
     *         the unique name of the shortcut.
     * @param keyStroke
     *         the key stroke of the shortcut.
     * @param action
     *         the action to execute when the shortcut is pressed.
     *
     * @return the created {@link Shortcut} object.
     * @throws IllegalArgumentException
     *         if the shortcut group does not belong to this manager or the specified shortcut name is not unique.
     */
    public Shortcut addShortcut(ShortcutGroup<?> group, String name, KeyStroke keyStroke, UiAction action) {
        var map = getShortcuts(group);
        if (map == null) {
            throw new IllegalArgumentException("The specified shortcut group does not belong to this manager. Please use createGroup() to create a shortcut group");
        }
        if (map.containsKey(name)) {
            throw new IllegalArgumentException("The specified shortcut name must be unique (Duplicate name: " + name + ")");
        }
        var shortcut = new Shortcut(name, keyStroke, action);
        map.put(name, shortcut);
        group.getListeningComponents().forEach(component -> bindShortcut(group, shortcut, component, action));
        return shortcut;
    }

    /**
     * Returns a map of all the {@link Shortcut} objects that are registered in the specified {@link ShortcutGroup}.
     *
     * @param group
     *         the shortcut group which we want to get its shortcuts.
     *
     * @return a {@link Map} object of all the shortcuts if they were present otherwise {@code null}.
     */
    public Map<String, Shortcut> getShortcuts(ShortcutGroup<?> group) {
        return shortcuts.get(group);
    }
}

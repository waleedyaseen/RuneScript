/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.shortcut;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A shortcut group, it is used to group or identify where a shortcut came from and is meant for what.
 *
 * @param <K>
 *         the key type of the shortcut.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode
public final class ShortcutGroup<K> {

    /**
     * The components that are currently listening to this shortcut group.
     */
    @Getter
    @EqualsAndHashCode.Exclude
    private final List<JComponent> listeningComponents = new ArrayList<>();

    /**
     * The key value of the shortcut group.
     */
    @Getter
    private final K key;

    /**
     * Looks-up for the {@link Shortcut} that is registered for this group and has the specified {@code name}.
     *
     * @param name
     *         the name of the shortcut to look-up for.
     *
     * @return the {@link Shortcut} object if found otherwise {@code null}.
     */
    public Shortcut lookup(String name) {
        var map = ShortcutManager.getInstance().getShortcuts(this);
        if (map == null) {
            throw new IllegalStateException("Illegal shortcut group. Please use createGroup() to create a shortcut group.");
        }
        return map.get(name);
    }
}

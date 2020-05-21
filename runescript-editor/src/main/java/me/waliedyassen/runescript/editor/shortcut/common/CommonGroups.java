/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.shortcut.common;

import me.waliedyassen.runescript.editor.shortcut.ShortcutGroup;
import me.waliedyassen.runescript.editor.shortcut.ShortcutManager;

/**
 * Holds all of the common shortcut groups.
 *
 * @author Walied K. Yassen
 */
public interface CommonGroups {
    /**
     * The explorer tree shortcuts.
     */
    ShortcutGroup<String> EXPLORER = ShortcutManager.getInstance().createGroup("Explorer Shortcuts");

    /**
     * The editor view shortcuts.
     */
    ShortcutGroup<String> EDITOR = ShortcutManager.getInstance().createGroup("Editor Shortcuts");

    /**
     * The errors view shortcuts.
     */
    ShortcutGroup<String> ERRORS = ShortcutManager.getInstance().createGroup("Errors Shortcuts");
}

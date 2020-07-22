/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.shortcut;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.swing.*;

/**
 * A single shortcut in the shortcut system.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class Shortcut {

    /**
     * The name of the shortcut.
     */
    @Getter
    private final String name;

    /**
     * The key stroke of the shortcut.
     */
    @Getter
    private final KeyStroke keyStroke;

    /**
     * The action to perform when the callback is executed.
     */
    @Getter
    private final UiAction action;
}

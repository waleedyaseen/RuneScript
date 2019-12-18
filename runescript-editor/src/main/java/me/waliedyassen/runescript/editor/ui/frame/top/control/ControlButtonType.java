/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.frame.top.control;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.editor.EditorIcons;

import javax.swing.*;

/**
 * A frame control button type, holds the style of the button and basic shared information about it.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public enum ControlButtonType {
    /**
     * The close control button type.
     */
    CLOSE("Close", 0xe81123, EditorIcons.CONTROL_CLOSE_BUTTON),

    /**
     * The minimise control button type.
     */
    MINIMISE("Minimise", -1, EditorIcons.CONTROL_MINIMISE_BUTTON),

    /**
     * The maximise control button type.
     */
    MAXIMISE("Maximise", -1, EditorIcons.CONTROL_MAXIMISE_BUTTON);

    /**
     * The name of the control button.
     */
    @Getter
    private final String name;

    /**
     * The hover color of the control button.
     */
    @Getter
    private final int color;

    /**
     * The icon of the control button.
     */
    @Getter
    private final Icon icon;
}
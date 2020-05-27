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
import me.waliedyassen.runescript.editor.ui.editor.Editor;

import javax.swing.*;
import java.awt.*;

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
    CLOSE("Close", new Color(232, 17, 35), EditorIcons.CONTROL_CLOSE_BUTTON, EditorIcons.CONTROL_CLOSE_BUTTON_HOVER),

    /**
     * The minimise control button type.
     */
    MINIMISE("Minimise", null, EditorIcons.CONTROL_MINIMISE_BUTTON, EditorIcons.CONTROL_MINIMISE_BUTTON_HOVER),

    /**
     * The restore control button type.
     */
    RESTORE("Restore", null, EditorIcons.CONTROL_RESTORE_BUTTON, EditorIcons.CONTROL_RESTORE_BUTTON_HOVER),

    /**
     * The maximise control button type.
     */
    MAXIMISE("Maximise", null, EditorIcons.CONTROL_MAXIMISE_BUTTON, EditorIcons.CONTROL_MAXIMISE_BUTTON_HOVER);

    /**
     * The name of the control button.
     */
    @Getter
    private final String name;

    /**
     * The hover color of the control button.
     */
    @Getter
    private final Color color;

    /**
     * The icon of the control button when is not hovered.
     */
    @Getter
    private final Icon icon;

    /**
     * The icon of the control button when is hovered.
     */
    @Getter
    private final Icon hoverIcon;
}
/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.status;

import com.alee.api.data.BoxOrientation;
import com.alee.api.data.CompassDirection;
import com.alee.extended.behavior.ComponentResizeBehavior;
import com.alee.extended.canvas.WebCanvas;
import com.alee.extended.memorybar.WebMemoryBar;
import com.alee.extended.overlay.AlignedOverlay;
import com.alee.extended.overlay.WebOverlay;
import com.alee.extended.statusbar.WebStatusBar;
import com.alee.laf.label.WebLabel;
import com.alee.managers.style.StyleId;
import lombok.Getter;
import me.waliedyassen.runescript.editor.property.impl.StringProperty;

import java.awt.*;

/**
 * The RuneScript Editor status bar.
 *
 * @author Walied K. Yassen
 */
public final class StatusBar extends WebStatusBar {

    /**
     * The current status text of the status bar.
     */
    @Getter
    private final StringProperty text = new StringProperty();

    /**
     * Constructs a new {@link StatusBar} type object instance.
     */
    public StatusBar() {
        setupLabel();
        setupMemory();
    }

    /**
     * Sets up the label at teh start of the status bar.
     */
    private void setupLabel() {
        var label = new WebLabel();
        text.addListener(label::setText);
        add(label);
    }

    /**
     * Sets-up the memory bar at the ned of the status bar.
     */
    private void setupMemory() {
        // Taken from the Web LaF example.
        var memoryBarOverlay = new WebOverlay();
        memoryBarOverlay.setContent(new WebMemoryBar().setPreferredWidth(150));
        WebCanvas resizeCorner = new WebCanvas(StyleId.canvasGripperSE);
        new ComponentResizeBehavior(resizeCorner, CompassDirection.southEast).install();
        memoryBarOverlay.addOverlay(new AlignedOverlay(resizeCorner, BoxOrientation.right, BoxOrientation.bottom, new Insets(0, 0, -1, -1)));
        addToEnd(memoryBarOverlay);
    }
}

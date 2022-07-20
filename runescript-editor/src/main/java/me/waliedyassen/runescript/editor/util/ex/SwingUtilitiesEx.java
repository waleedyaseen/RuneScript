/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.util.ex;


import javax.swing.*;
import java.awt.*;

/**
 * An extension class for {@link SwingUtilities} utility class.
 *
 * @author Walied K. Yassen
 */
public final class SwingUtilitiesEx {

    /**
     * Returns the hit test spot boundary of the specified {@link Component component}.
     *
     * @param component the component which we want the the hit test spot boundary for.
     * @return the hit test spot boundary of the specified component as a {@link Rectangle} object.
     */
    public static Rectangle getComponentHitTestBounds(Component component) {
        var location = component.getLocationOnScreen();
        return new Rectangle(Math.max(location.x - 2, 0), location.y, component.getWidth(), component.getHeight());
    }

    private SwingUtilitiesEx() {
        // NOOP
    }
}

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.frame.nativeSupport;

import com.sun.jna.Platform;
import me.waliedyassen.runescript.editor.ui.frame.TitledFrame;
import me.waliedyassen.runescript.editor.ui.frame.nativeSupport.win.WinNativeWindowSupport;

/**
 * An interface which represents a native support for a specific {@link TitledFrame}, the native support can be anything
 * to make the application feel more native such as snap assisting for windows.
 *
 * @author Walied K. Yassen
 */
public interface NativeWindowSupport {

    /**
     * Gets called when the window visibility have been changed.
     *
     * @param visible whether or not the window is currently visible.
     */
    void onSetVisible(boolean visible);

    /**
     * Creates a {@link NativeWindowSupport} implementation object based on the current operation system we are using.
     *
     * @param titledFrame the titled frame which the native window support is for.
     * @return the created {@link NativeWindowSupport} implementation object.
     */
    static NativeWindowSupport createNativeWindowSupport(TitledFrame titledFrame) {
        if (Platform.isWindows()) {
            return new WinNativeWindowSupport(titledFrame);
        }
        return null;
    }
}

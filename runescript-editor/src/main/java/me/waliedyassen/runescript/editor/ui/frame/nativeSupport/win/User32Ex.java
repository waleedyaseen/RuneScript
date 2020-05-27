/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.frame.nativeSupport.win;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.win32.W32APIOptions;

/**
 * An extension to the {@link User32} native library interface.
 *
 * @author Walied K. Yassen
 */
public interface User32Ex extends User32 {

    /**
     * The loaded native library object of this native library interface.
     */
    User32Ex EX_INSTANCE = Native.load("User32", User32Ex.class, W32APIOptions.DEFAULT_OPTIONS);

    /**
     * Changes an attribute of the specified window. The function also sets a value at the specified offset in the extra
     * window memory.
     */
    int GWLP_WNDPROC = -4;

    /**
     * @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-setwindowlongptra">Microsoft Doc</a>}
     */
    LONG_PTR SetWindowLongPtr(HWND hWnd, int nIndex, LONG_PTR dwNewLong);

    /**
     * @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-setwindowlongptra">Microsoft Doc</a>}
     */
    LONG_PTR SetWindowLongPtr(HWND hWnd, int nIndex, WindowProc dwNewProc);

    /**
     * @see <a href="http://www.docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-callwindowproca">Microsoft Doc</a>}
     */
    LRESULT CallWindowProc(LONG_PTR proc, HWND hWnd, int uMsg, WPARAM wParam, LPARAM lParam);
}

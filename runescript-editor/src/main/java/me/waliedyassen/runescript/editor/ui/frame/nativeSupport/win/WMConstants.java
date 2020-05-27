/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package me.waliedyassen.runescript.editor.ui.frame.nativeSupport.win;

/**
 * Contains all of the window messaging constants.
 *
 * @author Walied K. Yassen
 */
public interface WMConstants {

    /**
     * Sent when the size and position of a window's client area must be calculated. By processing this message, an
     * application can control the content of the window's client area when the size or position of the window changes.
     */
    int WM_NCCALCSIZE = 0x0083;

    /**
     * Sent to a window in order to determine what part of the window corresponds to a particular screen coordinate.
     * This can happen, for example, when the cursor moves, when a mouse button is pressed or released, or in response
     * to a call to a function such as WindowFromPoint. If the mouse is not captured, the message is sent to the window
     * beneath the cursor. Otherwise, the message is sent to the window that has captured the mouse.
     */
    int WM_NCHITTEST = 0x0084;

    /**
     * In the border of a window that does not have a sizing border.
     */
    int HTBORDER = 18;

    /**
     * In the lower-horizontal border of a resizable window (the user can click the mouse to resize the window
     * vertically).
     */
    int HTBOTTOM = 15;

    /**
     * In the lower-left corner of a border of a resizable window (the user can click the mouse to resize the window
     * diagonally).
     */
    int HTBOTTOMLEFT = 16;

    /**
     * In the lower-right corner of a border of a resizable window (the user can click the mouse to resize the window
     * diagonally).
     */
    int HTBOTTOMRIGHT = 17;

    /**
     * In a title bar.
     */
    int HTCAPTION = 2;

    /**
     * In a client area.
     */
    int HTCLIENT = 1;

    /**
     * In a Close button.
     */
    int HTCLOSE = 20;

    /**
     * On the screen background or on a dividing line between windows (same as HTNOWHERE, except that the DefWindowProc
     * function produces a system beep to indicate an error).
     */
    int HTERROR = -2;

    /**
     * In a size box (same as HTSIZE).
     */
    int HTGROWBOX = 4;

    /**
     * In a Help button.
     */
    int HTHELP = 21;

    /**
     * In a horizontal scroll bar.
     */
    int HTHSCROLL = 6;

    /**
     * In the left border of a resizable window (the user can click the mouse to resize the window horizontally).
     */
    int HTLEFT = 10;

    /**
     * In a menu.
     */
    int HTMENU = 5;

    /**
     * In a Maximize button.
     */
    int HTMAXBUTTON = 9;

    /**
     * In a Minimize button.
     */
    int HTMINBUTTON = 8;

    /**
     * On the screen background or on a dividing line between windows.
     */
    int HTNOWHERE = 0;

    /**
     * In a Minimize button.
     */
    int HTREDUCE = 8;

    /**
     * In the right border of a resizable window (the user can click the mouse to resize the window horizontally).
     */
    int HTRIGHT = 11;

    /**
     * In a size box (same as HTGROWBOX).
     */
    int HTSIZE = 4;

    /**
     * In a window menu or in a Close button in a child window.
     */
    int HTSYSMENU = 3;

    /**
     * In the upper-horizontal border of a window.
     */
    int HTTOP = 12;

    /**
     * In the upper-left corner of a window border.
     */
    int HTTOPLEFT = 13;

    /**
     * In the upper-right corner of a window border.
     */
    int HTTOPRIGHT = 14;

    /**
     * In a window currently covered by another window in the same thread (the message will be sent to underlying windows
     * in the same thread until one of them returns a code that is not HTTRANSPARENT).
     */
    int HTTRANSPARENT = -1;

    /**
     * In the vertical scroll bar.
     */
    int HTVSCROLL = 7;

    /**
     * In a Maximize button.
     */
    int HTZOOM = 9;
}

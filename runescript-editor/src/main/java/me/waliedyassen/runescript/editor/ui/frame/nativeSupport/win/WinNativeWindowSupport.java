/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.frame.nativeSupport.win;

import com.sun.jna.Native;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.editor.ui.frame.TitledFrame;
import me.waliedyassen.runescript.editor.ui.frame.nativeSupport.NativeWindowSupport;

import static com.sun.jna.platform.win32.BaseTSD.LONG_PTR;
import static com.sun.jna.platform.win32.WinDef.*;
import static com.sun.jna.platform.win32.WinUser.*;
import static me.waliedyassen.runescript.editor.ui.frame.nativeSupport.win.WMConstants.*;

/**
 * Represents the Window OS implementation of the {@link NativeWindowSupport} interface.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class WinNativeWindowSupport implements NativeWindowSupport, WindowProc {

    /**
     * The owner frame of the native window support.
     */
    private final TitledFrame frame;

    /**
     * A pointer to the {@link WindowProc} of the owner frame.
     */
    private LONG_PTR frameWindowProc;

    /**
     * {@inheritDoc}
     */
    @Override
    public LRESULT callback(HWND hWnd, int uMsg, WPARAM wParam, LPARAM lParam) {
        switch (uMsg) {
            case WM_NCCALCSIZE:
                return new LRESULT(0);
            case WM_NCHITTEST:
                return wmHitTest(hWnd, uMsg, wParam, lParam);
            case WM_DESTROY:
                return wmDestroy(hWnd, uMsg, wParam, lParam);
            default:
                return User32Ex.EX_INSTANCE.CallWindowProc(frameWindowProc, hWnd, uMsg, wParam, lParam);
        }
    }

    /**
     * Processes the hit test window message.
     *
     * @param hWnd   the native handle of the window.
     * @param uMsg   the type of the message.
     * @param wParam the word parameter of the message.
     * @param lParam the long parameter of the message.
     * @return the result of the message.
     */
    private LRESULT wmHitTest(HWND hWnd, int uMsg, WPARAM wParam, LPARAM lParam) {
        var mouseInScreen = new POINT();
        var bounds = new RECT();
        User32Ex.EX_INSTANCE.GetCursorPos(mouseInScreen);
        User32Ex.EX_INSTANCE.GetWindowRect(hWnd, bounds);
        var hitTestBounds = frame.getTitleBar().getHitTestBounds();
        var checkDelegateResize = hitTestBounds.stream().noneMatch(hitTestBound -> hitTestBound.contains(mouseInScreen.x, mouseInScreen.y));
        if (checkDelegateResize) {
            final var resizeBorderSize = TitledFrame.RESIZE_BORDER_THICKNESS;
            var width = bounds.right - bounds.left;
            var height = bounds.bottom - bounds.top;
            var mouseX = mouseInScreen.x - bounds.left;
            var mouseY = mouseInScreen.y - bounds.top;
            if (mouseX <= resizeBorderSize) {
                if (mouseY <= resizeBorderSize) {
                    return new LRESULT(HTTOPLEFT);
                } else if (mouseY >= height - resizeBorderSize) {
                    return new LRESULT(HTBOTTOMLEFT);
                } else {
                    return new LRESULT(HTLEFT);
                }
            } else if (mouseX >= width - resizeBorderSize) {
                if (mouseY <= resizeBorderSize) {
                    return new LRESULT(HTTOPRIGHT);
                } else if (mouseY >= height - resizeBorderSize) {
                    return new LRESULT(HTBOTTOMRIGHT);
                } else {
                    return new LRESULT(HTRIGHT);
                }
            } else if (mouseY <= resizeBorderSize) {
                return new LRESULT(HTTOP);
            } else if (mouseY >= height - resizeBorderSize) {
                return new LRESULT(HTBOTTOM);
            } else if (mouseY <= frame.getTitleBar().getHeight()) {
                return new LRESULT(HTCAPTION);
            }
        }
        return User32Ex.EX_INSTANCE.CallWindowProc(frameWindowProc, hWnd, uMsg, wParam, lParam);
    }

    /**
     * Processes the destroy window message.
     *
     * @param hWnd   the native handle of the window.
     * @param uMsg   the type of the message.
     * @param wParam the word parameter of the message.
     * @param lParam the long parameter of the message.
     * @return the result of the message.
     */
    private LRESULT wmDestroy(HWND hWnd, int uMsg, WPARAM wParam, LPARAM lParam) {
        User32Ex.EX_INSTANCE.SetWindowLongPtr(hWnd, User32Ex.GWLP_WNDPROC, frameWindowProc);
        return new LRESULT(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSetVisible(boolean visible) {
        if (!visible) {
            return;
        }
        var hWnd = new HWND(Native.getComponentPointer(frame));
        frameWindowProc = User32Ex.EX_INSTANCE.SetWindowLongPtr(hWnd, User32Ex.GWLP_WNDPROC, this);
        User32Ex.EX_INSTANCE.SetWindowPos(hWnd, hWnd, 0, 0, 0, 0, SWP_NOMOVE | SWP_NOSIZE | SWP_NOZORDER | SWP_FRAMECHANGED);
    }
}

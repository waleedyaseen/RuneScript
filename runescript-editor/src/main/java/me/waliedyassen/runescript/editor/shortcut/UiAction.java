/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.shortcut;

/**
 * A functional interface that represents an UI action.
 *
 * @author Walied K. Yassen
 */
@FunctionalInterface
public interface UiAction {

    /**
     * Gets called when the ui action is being executed.
     *
     * @param source
     *         the source UI attachment object.
     */
    void execute(Object source);
}

/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.menu.action;

import lombok.Getter;
import me.waliedyassen.runescript.editor.ui.menu.action.list.ActionList;

/**
 * The action manager of the context and popup menu(s) of the editor.
 *
 * @author Walied K. Yassen
 */
public final class ActionManager {

    /**
     * The singleton instance of the {@link ActionManager} type.
     */
    @Getter
    private static final ActionManager instance = new ActionManager();

    /**
     * Prevent the creation of this type outside this class.
     */
    private ActionManager() {
        // NOOP
    }

    /**
     * Creates anew {@link ActionList} type object instance.
     *
     * @param source
     *         the source of the action list.
     *
     * @return the created {@link ActionList} object.
     */
    public ActionList createList(Object source) {
        return new ActionList(source);
    }
}

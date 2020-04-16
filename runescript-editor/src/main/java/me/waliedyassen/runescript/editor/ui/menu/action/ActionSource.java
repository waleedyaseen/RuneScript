/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.ui.menu.action;

import me.waliedyassen.runescript.editor.ui.menu.action.list.ActionList;

/**
 * An interface which should be implemented in components they represent an action source which is basically any
 * component that should have right click action menu.
 *
 * @author Walied K. Yassen
 */
public interface ActionSource {

    /**
     * Populates the actions of the action source into the specified {@link ActionList}.
     *
     * @param actionList
     *         the actions list to populate the actions into.
     */
    void populateActions(ActionList actionList);
}

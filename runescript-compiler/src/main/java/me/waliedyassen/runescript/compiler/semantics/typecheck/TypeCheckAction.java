/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.semantics.typecheck;

/**
 * Represents the action that should be performed when returned from a child type checking.
 *
 * @author Walied K. Yassen
 */
public enum TypeCheckAction {

    /**
     * Skip the type checking for the parent node and make the parent node skip if necessary.
     */
    SKIP,

    /**
     * Continue the type checking normally.
     */
    CONTINUE;

    /**
     * Checks whether or not this action is a continue action.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public boolean isContinue() {
        return this == CONTINUE;
    }
}
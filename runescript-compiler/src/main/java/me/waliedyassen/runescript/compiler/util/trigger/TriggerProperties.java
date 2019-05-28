/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.util.trigger;

/**
 * Contains all the possible properties that a {@link TriggerType} element can have.
 *
 * @author Walied K. Yassen
 */
public interface TriggerProperties {

    /**
     * Tells that the trigger can be called using '~' operator.
     */
    int CALLABLE = 0x1;

    /**
     * Tells that the trigger can contain a return value.
     */
    int RETURNING = 0x2;
}
/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.util;

/**
 * @author Walied K. Yassen
 */
public enum TriggerType {

    /**
     * The clientscript trigger type.
     */
    CLIENTSCRIPT,

    /**
     * The procedure trigger type.
     */
    PROC;

    /**
     * Gets the {@link TriggerType} with the specified code {@code representation}.
     *
     * @param representation the code representation of the trigger type.
     *
     * @return the {@link TriggerType} if it was present otherwise {@code null}.
     */
    public static TriggerType forRepresentation(String representation) {
        switch (representation) {
            case "clientscript":
                return CLIENTSCRIPT;
            case "proc":
                return PROC;
            default:
                return null;
        }
    }
}

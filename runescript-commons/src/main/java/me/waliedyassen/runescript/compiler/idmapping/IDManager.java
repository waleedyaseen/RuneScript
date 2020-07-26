/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.idmapping;

import me.waliedyassen.runescript.type.Type;

/**
 * An interface which is responsible for providing ids for configs or scripts that are being code generated.
 *
 * @author Walied K. Yassen
 */
public interface IDManager {

    /**
     * Attempts to find the id for the script with the specified {@code name}.
     *
     * @param name
     *         the name of the script that we want to find the id for.
     *
     * @return the id of the script that we found.
     *
     * @throws IllegalArgumentException
     *         if we failed to find an id for the specified {@code name}.
     */
    int findScript(String name) throws IllegalArgumentException;


    /**
     * Attempts to find the id for the config with the specified {@code name} and {@link Type type}.
     *
     * @param type
     *         the type of the config that we want to find the id for.
     * @param name
     *         the name of the config that we want to find the id for.
     *
     * @return the id of the config that we found.
     *
     * @throws IllegalArgumentException
     *         if we failed to find an id for the config specified {@code name}.
     */
    int findConfig(Type type, String name) throws IllegalArgumentException;
}

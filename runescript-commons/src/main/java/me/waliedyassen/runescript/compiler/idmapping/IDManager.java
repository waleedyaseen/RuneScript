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
     * Attempts to find the script ID with the specified {@code name} and contained by the file with the specified {@code extension}.
     * If no script is found, we will create a new ID and assign it to that script.
     *
     * @param name
     *         the name of the script.
     * @param extension
     *         the extension of the file  which contains the script.
     *
     * @return the id of the script.
     */
    int findOrCreateScript(String name, String extension);

    /**
     * Attempts to find the config ID with the specified {@code name} and the specified {@link Type type}.
     * If no config is found, we will create a new ID and assign it to that config.
     *
     * @param type
     *         the type of the config.
     * @param name
     *         the name of the config.
     *
     * @return the id of the config.
     */
    int findOrCreateConfig(Type type, String name);

    /**
     * Attempts to find the id for the script with the specified {@code name}.
     *
     * @param name
     *         the name of the script that we want to find the id for.
     * @param extension
     *
     * @return the id of the script that we found.
     *
     * @throws IllegalArgumentException
     *         if we failed to find an id for the specified {@code name}.
     */
    int findScript(String name, String extension) throws IllegalArgumentException;


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

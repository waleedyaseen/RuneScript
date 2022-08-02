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
    int findOrCreateScriptId(String name, String extension);

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
}

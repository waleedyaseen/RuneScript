/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.pack;

import lombok.Data;

/**
 * Contains the data about a specific file that we want to pack.
 *
 * @author Walied K. Yassen
 */
@Data
public final class PackFile {

    /**
     * The id that was assigned to the file.
     */
    private final int id;

    /**
     * The name of the file.
     */
    private final String name;

    /**
     * The binary data of file.
     */
    private final byte[] data;
}

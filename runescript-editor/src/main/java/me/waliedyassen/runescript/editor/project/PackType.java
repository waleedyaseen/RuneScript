/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.project;

import me.waliedyassen.runescript.editor.pack.provider.PackProvider;
import me.waliedyassen.runescript.editor.pack.provider.impl.FlatPackProvider;
import me.waliedyassen.runescript.editor.pack.provider.impl.SQLitePackProvider;

import java.nio.file.Path;

/**
 * The packing type of a project.
 *
 * @author Walied K. Yassen
 */
public enum PackType {
    /**
     * Stores the output as files in the pack directory.
     */
    FLATFILE {
        @Override
        public PackProvider newInstance(Path directory) {
            return new FlatPackProvider(directory);
        }
    },

    /**
     * STore the output as entries in a sqlite database
     */
    SQLITE {
        @Override
        public PackProvider newInstance(Path directory) {
            return new SQLitePackProvider(directory);
        }
    };

    /**
     * Creates new {@link PackProvider} instance object.
     *
     * @param directory the directory which the packed database will be placed in.
     * @return the created {@link PackProvider} object.
     */
    public abstract PackProvider newInstance(Path directory);
}

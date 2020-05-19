/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.pack.provider.impl;

import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.editor.pack.Pack;
import me.waliedyassen.runescript.editor.pack.impl.SQLitePack;
import me.waliedyassen.runescript.editor.pack.provider.PackProvider;

import java.nio.file.Path;

/**
 * A {@link PackProvider} implementation that is responsible for providing {@link SQLitePack}.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class SQLitePackProvider implements PackProvider {

    /**
     * The path which leads to the root directory that contains the SQLite databases.
     */
    private final Path path;

    /**
     * {@inheritDoc}
     */
    @Override
    public Pack create(String extension) {
        if (extension.contentEquals("rs2")) {
            return new SQLitePack(path.resolve("serverscripts.db").toAbsolutePath().toString());
        }
        return null;
    }
}

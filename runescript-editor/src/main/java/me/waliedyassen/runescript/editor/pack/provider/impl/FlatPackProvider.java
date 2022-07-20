/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.pack.provider.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.waliedyassen.runescript.editor.pack.impl.FlatPack;
import me.waliedyassen.runescript.editor.pack.provider.PackProvider;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A {@link PackProvider} implementation that is responsible for providing {@link FlatPack}.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class FlatPackProvider implements PackProvider {

    /**
     * The path which leads to the root directory that contains the pack databases.
     */
    private final Path path;

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public FlatPack create(String packName) {
        var directory = path.resolve(packName);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException();
        }
        return new FlatPack(directory);
    }
}

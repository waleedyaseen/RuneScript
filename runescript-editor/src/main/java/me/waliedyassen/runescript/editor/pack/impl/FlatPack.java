/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.pack.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import me.waliedyassen.runescript.editor.pack.Pack;
import me.waliedyassen.runescript.editor.pack.PackFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;


/**
 * Represents a {@link Pack} implementation that packs to a directory
 *
 * @author Walied K. Yassen
 */
@Slf4j
@RequiredArgsConstructor
public final class FlatPack implements Pack {

    /**
     * The path of hte directory we will be packing in.
     */
    private final Path path;

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public void pack(PackFile file) {
        var path = this.path.resolve(String.format("%d-%s", file.getId(), file.getName()));
        Files.write(path, file.getData(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package me.waliedyassen.runescript.editor.pack.manager;

import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.editor.pack.Pack;
import me.waliedyassen.runescript.editor.pack.PackFile;
import me.waliedyassen.runescript.editor.pack.provider.PackProvider;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the pack manager which is responsible for packing anything in a specific project.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class PackManager {

    /**
     * The cached {@link Pack} objects by their associated extensions.
     */
    private final Map<String, Pack> packByExtension = new HashMap<>();

    /**
     * The {@link Pack} object provider.
     */
    private final PackProvider provider;

    /**
     * Attempts to pack the specified {@code data} for the file with the specified {@link Path path}.
     *
     * @param relativePath the path which the data was produced from.
     * @param name         the name of the entity we are packing.
     * @param data         the encoded data of the file to pack.
     */
    public void pack(Path relativePath, String name, byte[] data) {
        var formattedPath = (String) null;
        var fullName = (String) null;
        for (var index = 0; index < relativePath.getNameCount(); index++) {
            var part = relativePath.getName(index).toString();
            if (index == relativePath.getNameCount() - 1) {
                fullName = part;
            }
            if (formattedPath == null) {
                formattedPath = part;
            } else {
                formattedPath += '/' + part;
            }
        }
        if (formattedPath == null) {
            formattedPath = "";
        }
        if (fullName == null) {
            throw new IllegalArgumentException("The specified file path does not lead to file");
        }
        var dot = fullName.lastIndexOf('.');
        if (dot == -1) {
            throw new IllegalArgumentException("The specified file path does not have an extension");
        }
        var extension = fullName.substring(dot + 1);
        var pack = getPack(extension);
        if (pack == null) {
            throw new IllegalArgumentException("Could not find a suitable packer for the specified file path extension");
        }
        pack.pack(new PackFile(formattedPath, name, extension, data));
    }

    /**
     * Returns the {@link Pack} for the specified {@code extension}.
     *
     * @param extension the extension to grab the pack object for.
     * @return the {@link Pack} object if found or {@code null} if failed to create after not finding.
     */
    private Pack getPack(String extension) {
        var pack = packByExtension.get(extension);
        if (pack != null) {
            return pack;
        }
        pack = provider.create(extension);
        if (pack != null) {
            packByExtension.put(extension, pack);
        }
        return pack;
    }
}

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
     * The cached {@link Pack} objects by their associated names.
     */
    private final Map<String, Pack> packs = new HashMap<>();

    /**
     * The {@link Pack} object provider.
     */
    private final PackProvider provider;

    /**
     * Attempts to pack the specified {@code data} for the file with the specified {@link Path path}.
     *
     * @param packName
     *         the pack name which the unit is contained in.
     * @param id
     *         the id of the entity we are packing.
     * @param name
     *         the name of the entity we are packing.
     * @param data
     *         the encoded data of the file to pack.
     */
    public void pack(String packName, int id, String name, byte[] data) {
        var pack = getPack(packName);
        if (pack == null) {
            throw new IllegalArgumentException("Could not find a suitable packer for the specified name");
        }
        pack.pack(new PackFile(id, name, data));
    }

    /**
     * Returns the {@link Pack} for the specified {@code name}.
     *
     * @param name
     *         the pack name to grab the pack object for.
     *
     * @return the {@link Pack} object if found or {@code null} if failed to create after not finding.
     */
    private Pack getPack(String name) {
        var pack = packs.get(name);
        if (pack != null) {
            return pack;
        }
        pack = provider.create(name);
        if (pack != null) {
            packs.put(name, pack);
        }
        return pack;
    }
}

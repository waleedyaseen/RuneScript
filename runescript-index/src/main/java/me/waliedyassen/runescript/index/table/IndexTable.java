/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.index.table;

import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import lombok.Setter;
import lombok.var;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * An index table, it holds bunch of index entries that are related to one index key.
 *
 * @author Walied K. Yassen
 */
public final class IndexTable {

    /**
     * A map of all the entries in the index table.
     */
    private final Map<String, Integer> entries = new HashMap<>();

    /**
     * A set of all the removed ids that are currently available for use.
     */
    private final IntAVLTreeSet free = new IntAVLTreeSet();

    /**
     * The ID cursor the index table is currently at.
     */
    @Setter
    private int cursor;

    /**
     * Writes the {@link IndexTable} content from to specified {@link DataOutputStream stream}.
     *
     * @param stream the stream to write the index table content to.
     * @throws IOException if anything occurs while writing to the specified stream.
     */
    public void write(DataOutputStream stream) throws IOException {
        stream.writeInt(cursor);
        stream.writeInt(entries.size());
        for (var entry : entries.entrySet()) {
            stream.writeUTF(entry.getKey());
            stream.writeInt(entry.getValue());
        }
        stream.writeInt(free.size());
        for (var id : free) {
            stream.writeInt(id);
        }
    }

    /**
     * Reads the {@link IndexTable} content from the specified {@link DataInputStream stream}.
     *
     * @param stream the stream to read the index table from.
     * @throws IOException if anything occurs while reading from the specified stream.
     */
    public void read(DataInputStream stream) throws IOException {
        cursor = stream.readInt();
        entries.clear();
        var entryCount = stream.readInt();
        for (var index = 0; index < entryCount; index++) {
            entries.put(stream.readUTF(), stream.readInt());
        }
        free.clear();
        var freeCount = stream.readInt();
        for (var index = 0; index < freeCount; index++) {
            free.add(stream.readInt());
        }
    }

    /**
     * Attempts to find the id with the specified {@code name}, if the name is not associated with any id
     * a new id wi ll be assigned and returned.
     *
     * @param name the name which we want to find or create the index table.
     * @return the {@code id} that was found or created.
     */
    public int findOrCreate(String name) {
        var value = entries.get(name);
        if (value == null) {
            if (free.isEmpty()) {
                value = cursor++;
            } else {
                value = free.firstInt();
                free.remove(value.intValue());
            }
            entries.put(name, value);
        }
        return value;
    }

    /**
     * Finds the id that is associated with the specified {@code name}.
     *
     * @param name the name to find the id for.
     * @return the {@code id} if found otherwise {@code null}.
     */
    public Integer find(String name) {
        return entries.get(name);
    }

    /**
     * Removes the specified {@code name} from the index table and add the associated id, if found, to the
     * free ids list.
     *
     * @param name the name to remove from the index table.
     */
    public void remove(String name) {
        var id = entries.remove(name);
        if (id != null) {
            free.add(id.intValue());
        }
    }
}

/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.index;

import lombok.Getter;
import lombok.NonNull;
import me.waliedyassen.runescript.index.table.IndexTable;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an index table container.
 *
 * @param <K> the key type of the index.
 * @author Walied K. Yassen
 */
public final class Index<K> {

    /**
     * A map of all the tables that are in this index.
     */
    @Getter
    private final Map<K, IndexTable> tables = new HashMap<>();

    /**
     * Attempts to create a new {@link IndexTable} with the specified {@link K key}.
     *
     * @param key the key of the index table to create.
     * @return the created {@link IndexTable} object.
     * @throws IllegalArgumentException if the specified key is already taken by another index table.
     */
    public IndexTable create(@NonNull K key) {
        if (tables.containsKey(key)) {
            throw new IllegalArgumentException("The specified key is already taken by another table in the index");
        }
        var table = new IndexTable();
        tables.put(key, table);
        return table;
    }

    /**
     * Attempts to get the {@link IndexTable} with the specified {@link K key}.
     *
     * @param key the key of the index table to get.
     * @return the {@link IndexTable} object if it was present otherwise {@code null}.
     */
    public IndexTable get(@NonNull K key) {
        return tables.get(key);
    }
}

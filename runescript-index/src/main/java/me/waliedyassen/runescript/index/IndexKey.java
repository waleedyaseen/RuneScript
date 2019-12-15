/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.index;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * An index key, it represents a key for looking-up an index table or doing operations to it, the key value must be
 * unique in an index.
 *
 * @param <K>
 *         the key value type.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
@EqualsAndHashCode
public final class IndexKey<K> {

    /**
     * The value of the index key.
     */
    @NonNull
    private final K key;

    /**
     * Creates a new {@link IndexKey} type object instance.
     *
     * @param value
     *         the value of the key.
     * @param <T>
     *         the type of the key.
     *
     * @return the created {@link IndexKey} object.
     */
    public static <T> IndexKey<T> of(T value) {
        return new IndexKey<>(value);
    }
}

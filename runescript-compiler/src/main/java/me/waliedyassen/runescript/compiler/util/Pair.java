/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A utility type that holds two objects (the {@code key} and the {@code value}).
 *
 * @param <K> the type of the key.
 * @param <V> the type of the value.
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class Pair<K, V> {

    /**
     * The value of the key.
     */
    @Getter
    private final K key;

    /**
     * The value of the value.
     */
    @Getter
    private final V value;

    /**
     * Creates a new {@link Pair} object instance with the specified {@code key} and {@code value}.
     *
     * @param key   the first value of the pair (key).
     * @param value the second value of the pair (value).
     * @param <K>   the type of the key.
     * @param <V>   the type of the value.
     * @return the created {@link Pair} object instance.
     */
    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }
}

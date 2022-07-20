/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package me.waliedyassen.runescript.editor.ui.editor.code.tokenMaker.factory;

import org.fife.ui.rsyntaxtextarea.TokenMaker;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Represents the main implementation of the {@link TokenMakerFactory} for our IDE language support.
 *
 * @author Walied K. Yasssen
 */
public final class TokenMakerFactoryImpl extends TokenMakerFactory {

    /**
     * The singleton instance of the {@link TokenMakerFactoryImpl} type.
     */
    public static final TokenMakerFactoryImpl SINGLETON = new TokenMakerFactoryImpl();

    /**
     * A map of all the registered {@link TokenMaker} suppliers mapped by the syntax name.
     */
    private final Map<String, Supplier<? extends TokenMaker>> suppliers = new HashMap<>();

    private TokenMakerFactoryImpl() {
        // NOOP
    }

    static {
        setDefaultInstance(SINGLETON);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TokenMaker getTokenMakerImpl(String key) {
        var supplier = suppliers.get(key);
        if (supplier == null) {
            return null;
        }
        return supplier.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> keySet() {
        return suppliers.keySet();
    }

    /**
     * Registers a new supplier for {@link TokenMaker} for the syntax with the specified {@code name}.
     *
     * @param name     the name of the syntax to register for.
     * @param supplier the supplier of the {@link TokenMaker} objects.
     */
    public static void register(String name, Supplier<? extends TokenMaker> supplier) {
        SINGLETON.suppliers.put(name, supplier);
    }
}

/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.compiler;

import lombok.var;
import me.waliedyassen.runescript.config.binding.ConfigBinding;
import me.waliedyassen.runescript.config.lexer.token.Kind;
import me.waliedyassen.runescript.lexer.table.LexicalTable;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the RuneScript configurations compiler.
 *
 * @author Walied K. Yassen
 */
public final class ConfigCompiler {

    /**
     * The registered bindings in this compiler.
     */
    private final Map<String, ConfigBinding<?>> bindings = new HashMap<>();

    /**
     * Registers a new configuration binding into this compiler.
     *
     * @param extension
     *         the configuration file extension.
     * @param binding
     *         the configuration binding.
     */
    public void registerBinding(String extension, ConfigBinding<?> binding) {
        extension = extension.toLowerCase();
        if (bindings.containsKey(extension)) {
            throw new IllegalArgumentException("The specified binding extension is already registered: " + extension);
        }
        bindings.put(extension, binding);
    }

    /**
     * Create a new {@link LexicalTable} object and then register all of the lexical symbols for our configurations
     * syntax.
     *
     * @return the created {@link LexicalTable} object.
     */
    public static LexicalTable<Kind> createLexicalTable() {
        var table = new LexicalTable<Kind>();
        table.registerSeparator('[', Kind.LBRACKET);
        table.registerSeparator(']', Kind.RBRACKET);
        table.registerSeparator('=', Kind.EQUAL);
        table.registerSeparator(',', Kind.COMMA);
        return table;
    }
}

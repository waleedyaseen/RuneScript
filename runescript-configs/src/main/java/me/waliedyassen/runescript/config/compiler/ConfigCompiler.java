/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.compiler;

import me.waliedyassen.runescript.config.lexer.token.Kind;
import me.waliedyassen.runescript.lexer.table.LexicalTable;

/**
 * Represents the RuneScript configurations compiler.
 *
 * @author Walied K. Yassen
 */
public final class ConfigCompiler {

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
        return table;
    }
}

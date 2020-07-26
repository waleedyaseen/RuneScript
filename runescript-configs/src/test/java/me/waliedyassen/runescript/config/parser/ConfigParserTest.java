/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.parser;

import lombok.var;
import me.waliedyassen.runescript.commons.stream.BufferedCharStream;
import me.waliedyassen.runescript.compiler.lexer.table.LexicalTable;
import me.waliedyassen.runescript.config.compiler.ConfigCompiler;
import me.waliedyassen.runescript.config.lexer.Lexer;
import me.waliedyassen.runescript.config.lexer.Tokenizer;
import me.waliedyassen.runescript.config.lexer.token.Kind;
import org.junit.jupiter.api.BeforeAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;

class ConfigParserTest {

    private static LexicalTable<Kind> table;

    @BeforeAll
    static void setupLexicalTable() {
        table = ConfigCompiler.createLexicalTable();
    }

    private ConfigParser fromString(String source) throws IOException {
        var stream = new BufferedCharStream(new ByteArrayInputStream(source.getBytes()));
        var tokenizer = new Tokenizer(table, stream);
        var lexer = new Lexer(tokenizer);
        return new ConfigParser(lexer);
    }
}
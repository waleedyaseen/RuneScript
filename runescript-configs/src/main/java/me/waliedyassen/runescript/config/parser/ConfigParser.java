/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.parser;

import me.waliedyassen.runescript.config.lexer.Lexer;
import me.waliedyassen.runescript.config.lexer.token.Kind;
import me.waliedyassen.runescript.parser.ParserBase;

/**
 * Represents the configuration grammar Abstract-Syntax-Tree parser.
 *
 * @author Walied K. Yassen
 */
public final class ConfigParser extends ParserBase<Kind> {

    /**
     * Constructs a new {@link ConfigParser} type object instance.
     *
     * @param lexer
     *         the lexical parser to use for tokens.
     */
    public ConfigParser(Lexer lexer) {
        super(lexer);
    }
}

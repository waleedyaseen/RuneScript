/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.lexer;

import lombok.var;
import me.waliedyassen.runescript.compiler.lexer.LexerBase;
import me.waliedyassen.runescript.compiler.lexer.token.Token;
import me.waliedyassen.runescript.config.lexer.token.Kind;
import me.waliedyassen.runescript.config.lexer.token.SyntaxToken;

/**
 * Represents the configuration parser {@link LexerBase} implementation.
 *
 * @author Walied K. Yassen
 */
public final class Lexer extends LexerBase<Kind, SyntaxToken> {

    /**
     * Constructs a new {@link Lexer} type object instance.
     *
     * @param tokenizer
     *         the tokenizer which we will take all the {@link Token} objects from.
     */
    public Lexer(Tokenizer tokenizer) {
        super(tokenizer.range());
        tokens:
        do {
            var token = tokenizer.parse();
            switch (token.getKind()) {
                case EOF:
                    break tokens;
                case COMMENT:
                    continue tokens;
                default:
                    tokens.add(token);
            }
        } while (true);
    }
}

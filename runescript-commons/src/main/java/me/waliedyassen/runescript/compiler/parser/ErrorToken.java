/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.parser;

import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.lexer.token.Token;

/**
 * Represents an erroneous input token.
 *
 * @param <K> the kind of the token.
 * @author Walied K. Yassen
 */
public class ErrorToken<K> extends Token<K> {

    /**
     * Constructs a new {@link ErrorToken} type object instance.
     *
     * @param range the range of the token.
     * @param kind  the kind of the token.
     */
    public ErrorToken(Range range, K kind) {
        super(kind, range, null);
    }
}

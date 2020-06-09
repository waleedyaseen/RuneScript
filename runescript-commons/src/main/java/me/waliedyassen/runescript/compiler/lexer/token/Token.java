/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.lexer.token;

import lombok.Data;
import me.waliedyassen.runescript.commons.document.Element;
import me.waliedyassen.runescript.commons.document.Range;

/**
 * Represents a single token in any of our parsers.
 *
 * @author Walied K. Yassen
 */
@Data
public class Token<K> implements Element {

    /**
     * The token kind.
     */
    private final K kind;

    /**
     * The token source code range.
     */
    private final Range range;

    /**
     * The token lexeme value.
     */
    private final String lexeme;
}

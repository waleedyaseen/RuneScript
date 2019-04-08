/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.util;

import me.waliedyassen.runescript.compiler.lexer.token.Kind;

/**
 * Represents a variable scope, it tells from where the variable can be accessed.
 *
 * @author Walied K. Yassen
 */
public enum VariableScope {

    /**
     * The variable is only available within the script its declared in.
     */
    LOCAL,

    /**
     * The variable is available in any script.
     */
    GLOBAL;


    /**
     * Attempts to find the {@link VariableScope} that is associated with the specified token {@link Kind kind}.
     *
     * @param kind
     *         the scope token kind.
     *
     * @return the {@link VariableScope} if it was found otherwise {@code null}.
     */
    public static VariableScope forKind(Kind kind) {
        switch (kind) {
            case DOLLAR:
                return LOCAL;
            case MODULO:
                return GLOBAL;
            default:
                return null;
        }
    }
}

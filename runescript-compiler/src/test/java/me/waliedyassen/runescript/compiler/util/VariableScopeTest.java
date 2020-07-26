/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *  
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.util;

import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VariableScopeTest {

    @Test
    void testLookup(){
        assertEquals(VariableScope.LOCAL, VariableScope.forKind(Kind.DOLLAR));
        assertEquals(VariableScope.GLOBAL, VariableScope.forKind(Kind.MOD));
    }
}
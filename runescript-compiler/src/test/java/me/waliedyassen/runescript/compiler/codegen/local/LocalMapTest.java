/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *  
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.local;

import me.waliedyassen.runescript.type.primitive.PrimitiveType;
import me.waliedyassen.runescript.type.stack.StackType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocalMapTest {

    @Test
    void testParameters() {
        var map = new LocalMap();
        map.registerParameter("param0", PrimitiveType.INT.INSTANCE);
        map.registerParameter("param1", PrimitiveType.STRING.INSTANCE);
        map.registerParameter("param2", PrimitiveType.INT.INSTANCE);
        assertEquals(2, map.getParametersList(StackType.INT).size());
        assertEquals(1, map.getParametersList(StackType.STRING).size());
        assertEquals(0, map.getParametersList(StackType.LONG).size());
        assertEquals("param0", map.lookup("param0").getName());
        assertEquals(PrimitiveType.INT.INSTANCE, map.lookup("param0").getType());
        assertEquals(PrimitiveType.STRING.INSTANCE, map.lookup("param1").getType());
    }

    @Test
    void testVariables() {
        var map = new LocalMap();
        map.registerVariable("var0", PrimitiveType.INT.INSTANCE);
        map.registerVariable("var1", PrimitiveType.STRING.INSTANCE);
        map.registerVariable("var2", PrimitiveType.INT.INSTANCE);
        assertEquals(2, map.getVariablesList(StackType.INT).size());
        assertEquals(1, map.getVariablesList(StackType.STRING).size());
        assertEquals(0, map.getVariablesList(StackType.LONG).size());
        assertEquals("var0", map.lookup("var0").getName());
        assertEquals(PrimitiveType.INT.INSTANCE, map.lookup("var0").getType());
        assertEquals(PrimitiveType.STRING.INSTANCE, map.lookup("var1").getType());
    }

    @Test
    void testReset() {
        var map = new LocalMap();
        map.registerVariable("var0", PrimitiveType.INT.INSTANCE);
        map.registerParameter("param0", PrimitiveType.INT.INSTANCE);
        map.reset();
        assertEquals(0, map.getVariablesList(StackType.INT).size());
        assertEquals(0, map.getVariablesList(StackType.STRING).size());
        assertEquals(0, map.getVariablesList(StackType.LONG).size());
    }
}
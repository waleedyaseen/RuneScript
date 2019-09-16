/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.semantics.scope;

import me.waliedyassen.runescript.compiler.type.primitive.PrimitiveType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScopeTest {

    @Test
    void testDeclareLocalVariable() {
        var scope = new Scope(null);
        scope.declareLocalVariable("first", PrimitiveType.INT);
        scope.declareLocalVariable("second", PrimitiveType.INT);
        assertNotNull(scope.getLocalVariable("first"));
        assertNotNull(scope.getLocalVariable("second"));
    }

    @Test
    void testGetLocalVariable() {
        var parentScope = new Scope(null);
        var childScope = parentScope.createChild();
        parentScope.declareLocalVariable("parent_var", PrimitiveType.INT);
        assertNotNull(childScope.getLocalVariable("parent_var"));
        childScope.declareLocalVariable("child_var", PrimitiveType.INT);
        assertNull(parentScope.getLocalVariable("child_var"));
        assertNotNull(childScope.getLocalVariable("child_var"));
        assertEquals(PrimitiveType.INT, childScope.getLocalVariable("parent_var").getType());
        assertEquals(PrimitiveType.INT, childScope.getLocalVariable("child_var").getType());
    }

    @Test
    void testDeclareArray() {
        var scope = new Scope(null);
        scope.declareArray("first", PrimitiveType.INT);
        scope.declareArray("second", PrimitiveType.INT);
        assertEquals(2, scope.getArrayCount());
        assertNotNull(scope.getArray("first"));
        assertNotNull(scope.getArray("second"));
        var childScope = scope.createChild();
        for (var index = 0; index < 3; index++) {
            childScope.declareArray("array" + index, PrimitiveType.INT);
        }
        assertThrows(IllegalStateException.class, () -> childScope.declareArray("invalid", PrimitiveType.INT));
    }

    @Test
    void testGetArray() {
        var parentScope = new Scope(null);
        var childScope = parentScope.createChild();
        parentScope.declareArray("parent_array", PrimitiveType.INT);
        assertNotNull(childScope.getArray("parent_array"));
        childScope.declareArray("child_array", PrimitiveType.INT);
        assertNull(parentScope.getArray("child_array"));
        assertNotNull(childScope.getArray("child_array"));
        assertEquals(PrimitiveType.INT, childScope.getArray("parent_array").getType());
        assertEquals(PrimitiveType.INT, childScope.getArray("child_array").getType());
    }

    @Test
    void testParent() {
        var parentScope = new Scope(null);
        assertEquals(parentScope.createChild().getParent(), parentScope);
    }
}
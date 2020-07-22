/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TupleTypeTest {

    private static final TupleType TUPLE = new TupleType(new TupleType(PrimitiveType.INT, PrimitiveType.STRING), PrimitiveType.INT, new TupleType(PrimitiveType.STRING, PrimitiveType.BOOLEAN));
    private static final PrimitiveType[] TYPES = new PrimitiveType[]{PrimitiveType.INT, PrimitiveType.STRING, PrimitiveType.INT, PrimitiveType.STRING, PrimitiveType.BOOLEAN};

    @Test
    void testFlattening() {
        assertArrayEquals(TYPES, TUPLE.getFlattened());
    }


    @Test
    void testEquals() {
        assertEquals(TUPLE, new TupleType(TYPES));
        assertNotEquals(TUPLE, new Object());
        assertNotEquals(null, TUPLE);
    }

    @Test
    void testRepresentation() {
        assertEquals("int,int", new TupleType(PrimitiveType.INT, PrimitiveType.INT).getRepresentation());
        assertEquals("int,string,string", new TupleType(PrimitiveType.INT, PrimitiveType.STRING, new TupleType(PrimitiveType.STRING)).getRepresentation());
    }

    @Test
    void testNulls() {
        assertNull(TUPLE.getStackType());
        assertNull(TUPLE.getDefaultValue());
    }
}
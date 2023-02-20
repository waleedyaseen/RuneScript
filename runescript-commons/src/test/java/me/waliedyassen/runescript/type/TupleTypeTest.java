/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.type;

import me.waliedyassen.runescript.type.primitive.PrimitiveType;
import me.waliedyassen.runescript.type.tuple.TupleType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TupleTypeTest {

    private static final TupleType TUPLE = new TupleType(new TupleType(PrimitiveType.INT.INSTANCE, PrimitiveType.STRING.INSTANCE), PrimitiveType.INT.INSTANCE, new TupleType(PrimitiveType.STRING.INSTANCE, PrimitiveType.BOOLEAN.INSTANCE));
    private static final PrimitiveType<?>[] TYPES = new PrimitiveType[]{PrimitiveType.INT.INSTANCE, PrimitiveType.STRING.INSTANCE, PrimitiveType.INT.INSTANCE, PrimitiveType.STRING.INSTANCE, PrimitiveType.BOOLEAN.INSTANCE};

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
        assertEquals("int,int", new TupleType(PrimitiveType.INT.INSTANCE, PrimitiveType.INT.INSTANCE).getRepresentation());
        assertEquals("int,string,string", new TupleType(PrimitiveType.INT.INSTANCE, PrimitiveType.STRING.INSTANCE, new TupleType(PrimitiveType.STRING.INSTANCE)).getRepresentation());
    }

    @Test
    void testNulls() {
        assertNull(TUPLE.getStackType());
        assertNull(TUPLE.getDefaultValue());
    }
}
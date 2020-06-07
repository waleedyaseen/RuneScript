/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen;

import lombok.var;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LabelGeneratorTest {

    @Test
    void testGenerate() {
        var generator = new LabelGenerator();
        assertEquals(0, generator.generate("test").getId());
        assertEquals(1, generator.generate("test").getId());
        assertEquals(2, generator.generate("dummy").getId());
        assertEquals("test_2", generator.generate("test").getName());
    }

    @Test
    void testReset() {
        var generator = new LabelGenerator();
        assertEquals(0, generator.generate("test").getId());
        generator.reset();
        assertEquals(0, generator.generate("test").getId());
    }
}
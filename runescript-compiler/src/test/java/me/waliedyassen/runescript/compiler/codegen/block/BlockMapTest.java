/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *  
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.block;

import lombok.var;
import me.waliedyassen.runescript.compiler.codegen.LabelGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BlockMapTest {

    static LabelGenerator labelGenerator;

    @BeforeAll
    static void setupLabelGenerator() {
        labelGenerator = new LabelGenerator();
    }

    @Test
    void testGenerate() {
        var map = new BlockMap();
        var label = labelGenerator.generate("test");
        assertEquals(map.generate(label).getLabel(), label);
        label = labelGenerator.generate("test");
        assertNotNull(map.newBlock(label));
        assertEquals(map.generate(label).getLabel(), label);
        assertEquals(2, map.getBlocks().size());

    }

    @Test
    void resetReset() {
        var map = new BlockMap();
        map.generate(labelGenerator.generate("test"));
        map.reset();
        assertEquals(0, map.getBlocks().size());
    }
}
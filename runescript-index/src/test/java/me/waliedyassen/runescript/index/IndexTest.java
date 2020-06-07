/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package me.waliedyassen.runescript.index;

import lombok.var;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IndexTest {

    @Test
    void testCreation() {
        Index<String> index = new Index<>();
        var table = index.create("test");
        assertEquals(table, index.get("test"));
        assertThrows(IllegalArgumentException.class, () -> index.create("test"));
    }
}
/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.binding;

import me.waliedyassen.runescript.config.annotation.ConfigArray;
import me.waliedyassen.runescript.config.annotation.ConfigProps;
import me.waliedyassen.runescript.config.var.ConfigVar;
import me.waliedyassen.runescript.config.var.ConfigVarArray;
import me.waliedyassen.runescript.config.var.ConfigVarType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unused")
class ConfigBindingTest {

    @Test
    void testPopulateVars() {
        var binding = new ConfigBinding<>(TestCasePopulateVars.class, null);
        assertEquals(binding.getVariables().size(), 6);
        assertVarEquals(binding.getVariables().get("field"), 1, "field", false, ConfigVarType.INT, null);
        assertVarEquals(binding.getVariables().get("array1"), 2, "array", true, ConfigVarType.INT, new ConfigVarArray(0, 5));
        assertThrows(IllegalStateException.class, () -> new ConfigBinding<>(TestCaseArrayError.class, null));
    }

    void assertVarEquals(ConfigVar var, int opcode, String name, boolean required, ConfigVarType type, ConfigVarArray array) {
        assertEquals(opcode, var.getOpcode());
        assertEquals(name, var.getName());
        assertEquals(required, var.isRequired());
        assertEquals(type, var.getType());
        assertEquals(array, var.getArray());
    }

    private static final class TestCaseArrayError {

        @ConfigProps(opcode = 1, name = "array", type = ConfigVarType.INT)
        int[] array;
    }

    private static final class TestCasePopulateVars {

        static int ignoredStaticField;

        int ignoredField;

        @ConfigProps(opcode = 1, name = "field", type = ConfigVarType.INT)
        int integerField;

        @ConfigProps(opcode = 2, name = "array", required = true, type = ConfigVarType.INT)
        @ConfigArray(size = 5)
        int[] integerArray;
    }
}
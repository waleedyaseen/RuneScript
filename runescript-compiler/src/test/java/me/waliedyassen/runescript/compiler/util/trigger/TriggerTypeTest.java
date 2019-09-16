/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.util.trigger;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TriggerTypeTest {

    @Test
    void testForRepresentation() {
        for (var trigger : TriggerType.values()) {
            assertEquals(trigger, TriggerType.forRepresentation(trigger.getRepresentation()));
        }
    }

    @Test
    void testPropertyInvoke(){
        assertFalse(TriggerType.CLIENTSCRIPT.hasProperty(TriggerProperties.PROPERTY_INVOKE));
        assertTrue(TriggerType.PROC.hasProperty(TriggerProperties.PROPERTY_INVOKE));
    }

    @Test
    void testPropertyReturn(){
        assertFalse(TriggerType.CLIENTSCRIPT.hasProperty(TriggerProperties.PROPERTY_RETURN));
        assertTrue(TriggerType.PROC.hasProperty(TriggerProperties.PROPERTY_RETURN));
    }
}
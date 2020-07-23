/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *  
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.env;

import lombok.Data;
import lombok.var;
import me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.util.trigger.TriggerType;
import me.waliedyassen.runescript.type.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompilerEnvironmentTest {

    CompilerEnvironment environment;

    @BeforeEach
    void setupEnvironment() {
        environment = new CompilerEnvironment();
    }

    @Test
    void testTriggerDuplicateRepresentation() {
        final var TRIGGER_TYPE1 = new BasicTrigger("test", Kind.TILDE);
        final var TRIGGER_TYPE2 = new BasicTrigger("test", Kind.AT);
        environment.registerTrigger(TRIGGER_TYPE1);
        assertThrows(IllegalArgumentException.class, () -> environment.registerTrigger(TRIGGER_TYPE2));
    }

    @Test
    void testTriggerDuplicateOpcode() {
        final var TRIGGER_TYPE1 = new BasicTrigger("test1", Kind.AT);
        final var TRIGGER_TYPE2 = new BasicTrigger("test2", Kind.AT);
        environment.registerTrigger(TRIGGER_TYPE1);
        assertThrows(IllegalArgumentException.class, () -> environment.registerTrigger(TRIGGER_TYPE2));
    }

    @Test
    void testTriggerLookup() {
        final var TRIGGER_TYPE1 = new BasicTrigger("test1", Kind.TILDE);
        final var TRIGGER_TYPE2 = new BasicTrigger("test2", Kind.AT);
        environment.registerTrigger(TRIGGER_TYPE1);
        environment.registerTrigger(TRIGGER_TYPE2);
        assertEquals(TRIGGER_TYPE1, environment.lookupTrigger(Kind.TILDE));
        assertEquals(TRIGGER_TYPE2, environment.lookupTrigger("test2"));
    }

    @Data
    static final class BasicTrigger implements TriggerType {

        private final String representation;
        private final Kind operator;

        @Override
        public CoreOpcode getOpcode() {
            return null;
        }

        @Override
        public boolean hasArguments() {
            return false;
        }

        @Override
        public Type[] getArgumentTypes() {
            return new Type[0];
        }

        @Override
        public boolean hasReturns() {
            return false;
        }

        @Override
        public Type[] getReturnTypes() {
            return new Type[0];
        }
    }
}
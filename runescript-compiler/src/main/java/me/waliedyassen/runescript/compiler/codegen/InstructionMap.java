/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode;
import me.waliedyassen.runescript.compiler.codegen.opcode.Opcode;

import java.util.EnumMap;

/**
 * Represents the instructions map for our code generator, it is mainly used to remap core instructions to the target
 * RuneScript Assembly version.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class InstructionMap {

    /**
     * The core opcodes map.
     */
    @Getter
    private final EnumMap<CoreOpcode, MappedOpcode> coreMap = new EnumMap<>(CoreOpcode.class);

    /**
     * Checks whether or not this instruction map is ready. A ready state means all of the core opcodes are registered
     * and ready to be used.
     *
     * @return <code>true</code> if the map is ready otherwise <code>false</code>.
     */
    public boolean isReady() {
        for (var opcode : CoreOpcode.values()) {
            if (!coreMap.containsKey(opcode)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Registers the specified {@code id} for the given {@link CoreOpcode}.
     *
     * @param opcode
     *         the core opcode to register for.
     * @param large
     *         whether or not the opcode has a large operand.
     * @param code
     *         the opcode code to register.
     */
    public void registerCore(CoreOpcode opcode, int code, boolean large) {
        coreMap.put(opcode, new MappedOpcode(opcode, code, large));
    }

    /**
     * Looks-up for the {@link MappedOpcode} for the specified {@link CoreOpcode}.
     *
     * @param opcode
     *         the {@link CoreOpcode} object to look for its {@link MappedOpcode} equivalent .
     *
     * @return the {@link MappedOpcode} object.
     */
    public MappedOpcode lookup(CoreOpcode opcode) {
        return coreMap.get(opcode);
    }

    /**
     * Represents a mapped {@link CoreOpcode} which means it is a {@link CoreOpcode} with a mapped {@code code} number.
     *
     * @author Walied K. Yassen
     */
    @RequiredArgsConstructor
    @EqualsAndHashCode
    private final class MappedOpcode implements Opcode {

        /**
         * The core opcode we are remapping.
         */
        private final CoreOpcode opcode;

        /**
         * The opcode code number.
         */
        @Getter
        private final int code;

        /**
         * Whether or not the opcode has a large operand.
         */
        @Getter
        private final boolean large;

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return opcode.name();
        }
    }
}

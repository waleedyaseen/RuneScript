/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.runtime.executor.instruction;

import java.util.HashMap;
import java.util.Map;

/**
 * A map of {@link InstructionExecutor} objects.
 *
 * @author Walied K. Yassen
 */
@SuppressWarnings("rawtypes")
public final class InstructionExecutorMap {

    /**
     * A look-up for all of the {@link InstructionExecutor instruction executor}s by their associated instruction id.
     */
    private final Map<Integer, InstructionExecutor> executorsByOpcode = new HashMap<>();

    /**
     * Registers a new {@link InstructionExecutor} into the map.
     *
     * @param opcode
     *         the opcode to register the executor for.
     * @param executor
     *         the executor object to register.
     */
    public void register(int opcode, InstructionExecutor executor) {
        if (executorsByOpcode.containsKey(opcode)) {
            throw new IllegalArgumentException("The specified opcode is already registered for another InstructionExecutor");
        }
        executorsByOpcode.put(opcode, executor);
    }

    /**
     * Looks-up for the {@link InstructionExecutor} object which is registered for the specified {@code opcode}.
     *
     * @param opcode
     *         the opcode which the object is registered for.
     *
     * @return the {@link InstructionExecutor} object if it was present otherwise {@code null}.
     */
    public InstructionExecutor lookup(int opcode) {
        return executorsByOpcode.get(opcode);
    }
}

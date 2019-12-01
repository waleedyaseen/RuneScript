/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.env;

import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.util.trigger.TriggerType;

import java.util.HashMap;
import java.util.Map;

/**
 * A compiler environment, it contains all of the data that a user can or should share with the compiler in-order to
 * compile all the scripts successfully, this can be seen as a bridge between the user and the symbol table.
 *
 * @author Walied K. Yassen
 */
public final class CompilerEnvironment {

    /**
     * A look-up map of all the registered triggers by their representation.
     */
    private final Map<String, TriggerType> triggerByRepresentation = new HashMap<>();

    /**
     * A look-up map of all the registered triggers by their operator.
     */
    private final Map<Kind, TriggerType> triggerByOperator = new HashMap<>();

    /**
     * Registers a new trigger type into the environment.
     *
     * @param triggerType
     *         the trigger type to register.
     */
    public void registerTrigger(TriggerType triggerType) {
        if (triggerByRepresentation.containsKey(triggerType.getRepresentation())) {
            throw new IllegalArgumentException("The representation of the specified trigger type is already registered");
        }
        if (triggerType.getOperator() != null && triggerByOperator.containsKey(triggerType.getOperator())) {
            throw new IllegalArgumentException("The operator of the specified trigger type is already registered");
        }
        triggerByRepresentation.put(triggerType.getRepresentation(), triggerType);
        if (triggerType.getOperator() != null) {
            triggerByOperator.put(triggerType.getOperator(), triggerType);
        }
    }

    /**
     * Looks-up for the {@link TriggerType} with the specified {@code representation}.
     *
     * @param representation
     *         the representation of the trigger type.
     *
     * @return the {@link TriggerType} if it was found otherwise {@code null}.
     */
    public TriggerType lookupTrigger(String representation) {
        return triggerByRepresentation.get(representation);
    }

    /**
     * Looks-up for the {@link TriggerType} with the specified operator {@link Kind kind}.
     *
     * @param kind
     *         the kind of the operator of the trigger type.
     *
     * @return the {@link TriggerType} if it was found otherwise {@code null}.
     */
    public TriggerType lookupTrigger(Kind kind) {
        return triggerByOperator.get(kind);
    }
}

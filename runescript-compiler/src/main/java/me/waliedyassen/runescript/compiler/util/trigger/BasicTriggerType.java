/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.util.trigger;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.type.Type;

/**
 * A basic implementation nof the {@link TriggerType} interface type.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class BasicTriggerType implements TriggerType {

    /**
     * The representation of the trigger.
     */
    @Getter
    private final String representation;

    /**
     * The operator of the trigger.
     */
    @Getter
    private final Kind operator;

    /**
     * The opcode of the trigger.
     */
    @Getter
    private final CoreOpcode opcode;

    /**
     * Whether or not the the trigger supports argument values.
     */
    private final boolean supportArguments;

    /**
     * The argument types of the trigger.
     */
    @Getter
    private final Type[] argumentTypes;

    /**
     * Whether or not the trigger supports return values.
     */
    private final boolean supportReturns;

    /**
     * The return types of the trigger.
     */
    @Getter
    private final Type[] returnTypes;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasArguments() {
        return supportArguments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasReturns() {
        return supportReturns;
    }
}

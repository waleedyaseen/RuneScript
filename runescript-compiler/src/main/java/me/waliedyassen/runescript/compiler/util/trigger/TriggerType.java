/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.util.trigger;

import me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.type.Type;

/**
 * A trigger type of a script, it contains various information about how scripts should be or behave when it is marked
 * with the trigger type as well information about the symbols of the trigger type.
 *
 * @author Walied K. Yassen
 */
public interface TriggerType {

    /**
     * Gets the source code representation of the trigger type.
     *
     * @return the source code representation.
     */
    String getRepresentation();

    /**
     * Returns the operator {@link Kind} which is used in the source code for calling scripts with the this trigger
     * type, or alternatively returns {@code null} if the script cannot be called using an operator.
     *
     * @return the operator {@link Kind} of the trigger type if it can be called using an operator otherwise {@code
     * null}.
     */
    Kind getOperator();

    /**
     * Returns the instruction {@link CoreOpcode opcode} of the trigger type, this requires the {@link #getOperator()}
     * to not return a {@code null} value.
     *
     * @return the {@link CoreOpcode} of the trigger type it was present otherwise {@code null}.
     */
    CoreOpcode getOpcode();

    /**
     * Checks whether or not the trigger type can have arguments.
     *
     * @return <code>true</code> if it does otherwise <code>false</code>.
     */
    boolean hasArguments();

    /**
     * Returns an array of {@link Type} that represent the argument value types that a script with this trigger type
     * should have.
     *
     * @return an array {@link Type} if any is specified otherwise {@code null}.
     */
    Type[] getArgumentTypes();

    /**
     * Checks whether or not the trigger type can have returns.
     *
     * @return <code>true</code> if it does otherwise <code>false</code>.
     */
    boolean hasReturns();

    /**
     * Return an array of {@link Type} that represent the return value types that a script with this trigger type should
     * be returning.
     *
     * @return an array {@link Type} if any is specified otherwise {@code null}.
     */
    Type[] getReturnTypes();
}

/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.symbol.impl.script;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.symbol.Symbol;
import me.waliedyassen.runescript.compiler.util.trigger.TriggerType;
import me.waliedyassen.runescript.type.Type;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a declared script symbol information.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class ScriptInfo extends Symbol {

    /**
     * The list of annotation of the script.
     */
    @Getter
    private final Map<String, Annotation> annotations;

    /**
     * The name of the script.
     */
    @Getter
    private final String name;

    /**
     * The trigger of the script.
     */
    @Getter
    private final TriggerType trigger;

    /**
     * The type of the script.
     */
    @Getter
    private final Type type;

    /**
     * The arguments which the script takes in.
     */
    @Getter
    private final Type[] arguments;

    /**
     * The predefined id of the script.
     */
    @Getter
    private final Integer predefinedId;

    /**
     * Returns the full name of the script with the trigger.
     *
     * @return the full name of the script with the trigger.
     */
    public String getFullName() {
        return String.format("[%s,%s]", trigger.getRepresentation(), name);
    }

    /**
     * Checks whether not the signature is equal between (return and arugment types) this script and the specified
     * {@code other} script.
     *
     * @param other the other script to check this script's signature against.
     * @return <code>true</code> if the signature is equal otherwise <code>false</code>.
     */
    public boolean equalSignature(ScriptInfo other) {
        return Objects.equals(type, other.type) && Arrays.equals(arguments, other.arguments);
    }
}

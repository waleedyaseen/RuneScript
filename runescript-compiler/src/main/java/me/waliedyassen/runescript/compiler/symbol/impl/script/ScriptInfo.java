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
import lombok.ToString;
import me.waliedyassen.runescript.type.Type;
import me.waliedyassen.runescript.compiler.util.trigger.TriggerType;

import java.util.Map;

/**
 * Represents a declared script symbol information.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public final class ScriptInfo {

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
     * Returns the full name of the script with the trigger.
     *
     * @return the full name of the script with the trigger.
     */
    public String getFullName() {
        return String.format("[%s,%s]", trigger.getRepresentation(), name);
    }
}

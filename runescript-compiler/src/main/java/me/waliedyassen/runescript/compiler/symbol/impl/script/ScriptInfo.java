/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.symbol.impl.script;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.type.Type;
import me.waliedyassen.runescript.compiler.util.trigger.TriggerType;

import java.util.Map;

/**
 * Represents a declared script symbol information.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
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
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return name;
    }
}

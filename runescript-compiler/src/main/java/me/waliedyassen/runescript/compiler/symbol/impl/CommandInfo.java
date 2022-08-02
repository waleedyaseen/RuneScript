/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.symbol.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.codegen.opcode.Opcode;
import me.waliedyassen.runescript.type.Type;

/**
 * Represents command information.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class CommandInfo {

    /**
     * The opcode of this command.
     */
    @Getter
    private final Opcode opcode;

    /**
     * The name of the command.
     */
    @Getter
    private final String name;

    /**
     * The argument type(s) of the command.
     */
    @Getter
    private final Type[] arguments;

    /**
     * The return type of the command.
     */
    @Getter
    private final Type type;

    /**
     * The type of transmits the hook must have if present.
     */
    @Getter
    private final Type transmits;

    /**
     * Whether this command can be alternative.
     */
    @Getter
    private final boolean dot;

    /**
     * Checks whether or not this command information is for the enum command.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public boolean isEnum() {
        return name.equals("enum");
    }

    /**
     * Checks whether or not this command information is for the param commands.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public boolean isParam() {
        return name.equals("oc_param") || name.equals("lc_param") || name.equals("nc_param") || name.equals("struct_param");
    }
}

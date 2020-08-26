/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.waliedyassen.runescript.compiler.syntax.ScriptSyntax;
import me.waliedyassen.runescript.compiler.codegen.script.BinaryScript;

/**
 * Represents a single compiled unit of a script source file.
 *
 * @author Walied K. Yassen
 */
@Getter
@Setter
@RequiredArgsConstructor
public final class CompiledScriptUnit {

    /**
     * The AST script node of the compiled unit.
     */
    private ScriptSyntax script;

    /**
     * The binary script of the compiled unit.
     */
    private BinaryScript binaryScript;
}

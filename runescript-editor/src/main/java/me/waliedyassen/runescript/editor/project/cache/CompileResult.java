/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.project.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.syntax.ScriptSyntax;

import java.util.ArrayList;
import java.util.List;

/**
 * An object that holds the result of a compile call within a project.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class CompileResult {

    /**
     * A list of all the parsed script syntax.
     */
    @Getter
    private final List<ScriptSyntax> scriptSyntax = new ArrayList<>();
}

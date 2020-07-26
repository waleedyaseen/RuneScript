/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.compiler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.waliedyassen.runescript.config.ast.AstConfig;
import me.waliedyassen.runescript.config.binding.ConfigBinding;
import me.waliedyassen.runescript.config.codegen.BinaryConfig;

/**
 * Represents a single compiled unit of a configuration source file.
 *
 * @author Walied K. Yassen
 */
@Getter
@Setter
@RequiredArgsConstructor
public final class CompiledConfigUnit {

    /**
     * The configuration binding that was used for the compiled unit.
     */
    private final ConfigBinding binding;

    /**
     * The AST configuration node of the compiled unit.
     */
    private AstConfig config;

    /**
     * The binary configuration of the compiled unit.
     */
    private BinaryConfig binaryConfig;
}

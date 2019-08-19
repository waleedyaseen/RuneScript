/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package me.waliedyassen.runescript.compiler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * A temporary class to pass the compilation task result output errors, this class will be removed when we have proper
 * compilation result handling.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class CompilerErrors extends RuntimeException {

    /**
     * The list of {@link CompilerError} objects that were thrown during the compilation process.
     */
    @Getter
    private final List<CompilerError> errors;
}

/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.semantics;

import lombok.Getter;
import me.waliedyassen.runescript.compiler.ast.AstNode;
import me.waliedyassen.runescript.compiler.semantics.checkers.LocalResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the semantic analysis checker. It checks the source code for any semantic errors.
 *
 * @author Walied K. Yassen
 */
public final class SemanticChecker {

    /**
     * The generated errors during this semantic checker life time.
     */
    @Getter
    private final List<SemanticError> errors = new ArrayList<>();

    /**
     * Executes the semantic checking at the specified {@link AstNode node}.
     *
     * @param tree
     *         the node tree to perform the semantic checking for.
     */
    public void execute(AstNode tree) {
        // declare and resolve all of the local variables.
        var localResolver = new LocalResolver(this);
        tree.accept(localResolver);
    }

    /**
     * Reports an error that has occurred in the semantic checkers.
     *
     * @param error
     *         the error to report.
     */
    public void reportError(SemanticError error) {
        errors.add(error);
    }
}

/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.semantics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.ast.AstNode;
import me.waliedyassen.runescript.compiler.semantics.checkers.VariableScopeBuilder;
import me.waliedyassen.runescript.compiler.semantics.checkers.TypeChecker;
import me.waliedyassen.runescript.compiler.symbol.SymbolTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the semantic analysis checker. It checks the source code for any semantic errors.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class SemanticChecker {

    /**
     * The generated errors during this semantic checker life time.
     */
    @Getter
    private final List<SemanticError> errors = new ArrayList<>();

    /**
     *
     */
    private final SymbolTable symbolTable;

    /**
     * Executes the semantic checking at the specified {@link AstNode node}.
     *
     * @param tree
     *         the node tree to perform the semantic checking for.
     */
    public void execute(AstNode tree) {
        // declare and resolve all of the local variables.
        var scopeBuilder = new VariableScopeBuilder(this);
        tree.accept(scopeBuilder);
        var typeChecker = new TypeChecker(this, symbolTable);
        tree.accept(typeChecker);
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

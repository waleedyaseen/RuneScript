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
import me.waliedyassen.runescript.CompilerError;
import me.waliedyassen.runescript.compiler.ast.AstNode;
import me.waliedyassen.runescript.compiler.ast.AstScript;
import me.waliedyassen.runescript.compiler.env.CompilerEnvironment;
import me.waliedyassen.runescript.compiler.semantics.typecheck.PreTypeChecking;
import me.waliedyassen.runescript.compiler.semantics.typecheck.TypeChecking;
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
    private final List<CompilerError> errors = new ArrayList<>();

    /**
     * The environment of the owner compiler.
     */
    @Getter
    private final CompilerEnvironment environment;

    /**
     * The symbol table to resolve and register symbol information in.
     */
    @Getter
    private final SymbolTable symbolTable;

    /**
     * Executes the pre semantic checking for the specified {@link AstScript scripts}.
     *
     * @param scripts
     *         the scripts to perform the pre semantic checking on.
     */
    public void executePre(Iterable<AstScript> scripts) {
        var pre = new PreTypeChecking( this, symbolTable);
        scripts.forEach(tree -> tree.accept(pre));
    }

    /**
     * Executes the semantic checking for the specified {@link AstNode node}.
     *
     * @param scripts
     *         the scripts to perform the semantic checking on.
     */
    public void execute(Iterable<AstScript> scripts) {
        var checker = new TypeChecking(this, symbolTable);
        scripts.forEach(tree -> tree.accept(checker));
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

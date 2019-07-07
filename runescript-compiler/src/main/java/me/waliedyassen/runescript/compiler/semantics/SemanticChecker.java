/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.semantics;

import jdk.jfr.Threshold;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.ast.AstNode;
import me.waliedyassen.runescript.compiler.ast.AstScript;
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
    private final List<SemanticError> errors = new ArrayList<>();

    /**
     * The symbol table to resolve and register symbol information in.
     */
    @Getter
    private final SymbolTable symbolTable;

    /**
     * Executes the semantic checking at the specified {@link AstNode node}.
     *
     * @param trees
     *         the node trees to perform the semantic checking for.
     */
    public void execute(Iterable<AstScript> trees) {
        var pre = new PreTypeChecking(this, symbolTable);
        trees.forEach(tree -> tree.accept(pre));
        var checker = new TypeChecking(this, symbolTable);
        trees.forEach(tree -> tree.accept(checker));
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

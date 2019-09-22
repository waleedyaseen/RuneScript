/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.semantics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.CompilerError;
import me.waliedyassen.runescript.config.ast.AstConfig;
import me.waliedyassen.runescript.config.binding.ConfigBinding;
import me.waliedyassen.runescript.config.semantics.typecheck.PreTypeChecking;
import me.waliedyassen.runescript.config.semantics.typecheck.TypeChecking;
import me.waliedyassen.runescript.config.symbol.SymbolTable;

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
     * The symbol table to resolve and register symbol information in.
     */
    @Getter
    private final SymbolTable symbolTable;

    /**
     * The configuration binding we are checking for.
     */
    private final ConfigBinding<?> binding;

    /**
     * Executes the pre semantic checking for the specified {@link AstConfig configs}.
     *
     * @param configs
     *         the configs to perform the pre semantic checking on.
     */
    public void executePre(AstConfig... configs) {
        var checker = new PreTypeChecking(this, symbolTable, binding);
        for (var config : configs) {
            config.visit(checker);
        }
    }

    /**
     * Executes the semantic checking for the specified {@link AstConfig configs}.
     *
     * @param configs
     *         the configs to perform the semantic checking on.
     */
    public void execute(AstConfig... configs) {
        var checker = new TypeChecking(this, symbolTable, binding);
        for (var config : configs) {
            config.visit(checker);
        }
    }

    /**
     * Reports an error that has occurred in the semantic checkers.
     *
     * @param error
     *         the semantic error object to report.
     */
    public void reportError(SemanticError error) {
        errors.add(error);
    }
}

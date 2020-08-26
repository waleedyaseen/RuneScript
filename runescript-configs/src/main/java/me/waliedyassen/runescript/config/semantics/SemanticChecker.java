/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.semantics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.compiler.CompilerError;
import me.waliedyassen.runescript.compiler.symbol.SymbolTable;
import me.waliedyassen.runescript.config.syntax.ConfigSyntax;
import me.waliedyassen.runescript.config.binding.ConfigBinding;
import me.waliedyassen.runescript.config.semantics.typecheck.PreTypeChecking;
import me.waliedyassen.runescript.config.semantics.typecheck.TypeChecking;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the semantic analysis checker. It checks the source code for any semantic errors.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class SemanticChecker {

    // TODO: Convert this into ErrorReport and move the methods to static ones.

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
     * Executes the pre semantic checking for the specified {@link ConfigSyntax configs}.
     *
     * @param configs
     *         the configs to perform the pre semantic checking on.
     * @param binding
     *         the binding to use for the type checking.
     */
    public void executePre(Iterable<ConfigSyntax> configs, ConfigBinding binding) {
        var checker = new PreTypeChecking(this, symbolTable, binding);
        for (var config : configs) {
            config.accept(checker);
        }
    }

    /**
     * Executes the semantic checking for the specified {@link ConfigSyntax configs}.
     *
     * @param configs
     *         the configs to perform the semantic checking on.
     * @param binding
     *         the binding to use for the type checking.
     */
    public void execute(Iterable<ConfigSyntax> configs, ConfigBinding binding) {
        var checker = new TypeChecking(this, symbolTable, binding);
        for (var config : configs) {
            config.accept(checker);
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

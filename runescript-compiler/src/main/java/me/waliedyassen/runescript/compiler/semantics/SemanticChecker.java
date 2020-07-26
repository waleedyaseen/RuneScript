/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.semantics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.compiler.CompiledScriptUnit;
import me.waliedyassen.runescript.compiler.env.CompilerEnvironment;
import me.waliedyassen.runescript.compiler.semantics.typecheck.PreTypeChecking;
import me.waliedyassen.runescript.compiler.semantics.typecheck.TypeChecking;
import me.waliedyassen.runescript.compiler.symbol.ScriptSymbolTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the semantic analysis checker. It checks the source code for any semantic errors.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class SemanticChecker implements ErrorReporter {

    /**
     * The generated errors during this semantic checker life time.
     */
    @Getter
    private final List<SemanticError> errors = new ArrayList<>();

    /**
     * The environment of the owner compiler.
     */
    @Getter
    private final CompilerEnvironment environment;

    /**
     * The symbol table to resolve and register symbol information in.
     */
    @Getter
    private final ScriptSymbolTable symbolTable;

    /**
     * Whether or not we allow overriding in symbols.
     */
    @Getter
    private final boolean allowOverriding;

    /**
     * Executes the pre semantic checking for the specified {@link CompiledScriptUnit scripts}.
     *
     * @param scripts
     *         the scripts to perform the pre semantic checking on.
     */
    public void executePre(Iterable<CompiledScriptUnit> scripts) {
        var pre = new PreTypeChecking(this, symbolTable);
        for (var script : scripts) {
            script.getScript().accept(pre);
        }
    }

    /**
     * Executes the semantic checking for the specified {@link CompiledScriptUnit script}s.
     *
     * @param scripts
     *         the scripts to perform the semantic checking on.
     */
    public void execute(Iterable<CompiledScriptUnit> scripts) {
        var checker = new TypeChecking(this, symbolTable, environment.getHookTriggerType());
        for (var script : scripts) {
            script.getScript().accept(checker);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reportError(SemanticError error) {
        errors.add(error);
    }
}

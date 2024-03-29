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
import me.waliedyassen.runescript.compiler.CompiledScriptUnit;
import me.waliedyassen.runescript.compiler.CompilerError;
import me.waliedyassen.runescript.compiler.ScriptCompiler;
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
public final class SemanticChecker {

    /**
     * The generated errors during this semantic checker life time.
     */
    @Getter
    private final List<CompilerError> errors = new ArrayList<>();

    private final ScriptCompiler compiler;
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
            script.getSyntax().accept(pre);
        }
    }

    /**
     * Executes the semantic checking for the specified {@link CompiledScriptUnit script}s.
     *
     * @param scripts
     *         the scripts to perform the semantic checking on.
     */
    public void execute(Iterable<CompiledScriptUnit> scripts) {
        var checker = new TypeChecking(compiler, this, symbolTable, environment.getHookTriggerType());
        for (var script : scripts) {
            script.getSyntax().accept(checker);
        }
    }

    /**
     * Adds the specified {@link CompilerError error} to the list of errors.
     *
     * @param error
     *         the error to add to the list of errors.
     */
    public void reportError(CompilerError error) {
        errors.add(error);
    }
}

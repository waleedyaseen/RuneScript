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
import lombok.var;
import me.waliedyassen.runescript.compiler.ast.AstNode;
import me.waliedyassen.runescript.compiler.ast.AstScript;
import me.waliedyassen.runescript.compiler.env.CompilerEnvironment;
import me.waliedyassen.runescript.compiler.semantics.typecheck.PreTypeChecking;
import me.waliedyassen.runescript.compiler.semantics.typecheck.TypeChecking;
import me.waliedyassen.runescript.compiler.symbol.SymbolTable;
import me.waliedyassen.runescript.compiler.util.Pair;

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
    private final List<Pair<Object, SemanticError>> errors = new ArrayList<>();

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
     * Whether or not we allow overriding in symbols.
     */
    @Getter
    private final boolean allowOverriding;

    /**
     * The current key that we are going to register the errors under.
     */
    // TODO(Walied): A temporary workaround, a proper solution would be finding a way to specify the key when reporting.
    private Object currentKey;

    /**
     * Executes the pre semantic checking for the specified {@link AstScript scripts}.
     *
     * @param scripts the scripts to perform the pre semantic checking on.
     */
    public void executePre(Iterable<Pair<Object, AstScript>> scripts) {
        var pre = new PreTypeChecking(this, symbolTable);
        for (var pair : scripts) {
            currentKey = pair.getKey();
            pair.getValue().accept(pre);
        }
    }

    /**
     * Executes the semantic checking for the specified {@link AstNode node}.
     *
     * @param scripts the scripts to perform the semantic checking on.
     */
    public void execute(Iterable<Pair<Object, AstScript>> scripts) {
        var checker = new TypeChecking(this, symbolTable, environment.getHookTriggerType());
        for (var pair : scripts) {
            currentKey = pair.getKey();
            pair.getValue().accept(checker);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reportError(SemanticError error) {
        errors.add(Pair.of(currentKey, error));
    }
}

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package me.waliedyassen.runescript.config.semantics.typecheck;

import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.compiler.symbol.SymbolTable;
import me.waliedyassen.runescript.config.ast.AstConfig;
import me.waliedyassen.runescript.config.ast.visitor.AstTreeVisitor;
import me.waliedyassen.runescript.config.binding.ConfigBinding;
import me.waliedyassen.runescript.config.semantics.SemanticChecker;
import me.waliedyassen.runescript.config.semantics.SemanticError;

/**
 * Represents the pre type checking semantic analysis.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class PreTypeChecking extends AstTreeVisitor {

    /**
     * The semantic checker which owns this type checker.
     */
    private final SemanticChecker checker;

    /**
     * The symbol table to use for looking up.
     */
    private final SymbolTable table;

    /**
     * The configuration group we are checking for.
     */
    private final ConfigBinding binding;

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visit(AstConfig config) {
        var info = table.lookupConfig(config.getName().getText());
        if (info != null) {
            checker.reportError(new SemanticError(config.getName(), "Duplicate configuration: " + info.getName()));
        } else {
            table.defineConfig(config.getName().getText(), binding.getGroup().getType(), config.resolveContentType(binding));
        }
        return DEFAULT;
    }
}

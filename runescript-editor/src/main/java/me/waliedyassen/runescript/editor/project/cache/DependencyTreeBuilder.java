/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package me.waliedyassen.runescript.editor.project.cache;

import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.compiler.ast.AstScript;
import me.waliedyassen.runescript.compiler.ast.expr.AstCall;
import me.waliedyassen.runescript.compiler.ast.visitor.AstTreeVisitor;
import me.waliedyassen.runescript.editor.project.dependency.DependencyTree;

/**
 * An {@link AstTreeVisitor} which will build a {@link DependencyTree} for all of the visited scripts.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class DependencyTreeBuilder extends AstTreeVisitor {

    /**
     * The dependency tree we are building.
     */
    private final DependencyTree<String> dependencyTree;

    /**
     * The current script we are building the dependency tree for.
     */
    private String currentScript;

    /**
     * {@inheritDoc}
     */
    @Override
    public void enter(AstScript script) {
        currentScript = script.getFullName();
        dependencyTree.findOrCreate(currentScript);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exit(AstScript script) {
        currentScript = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit(AstCall call) {
        var node = dependencyTree.findOrCreate(currentScript);
        node.addDependency(call.getFullName());
        return super.visit(call);
    }
}

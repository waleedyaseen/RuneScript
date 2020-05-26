/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.semantics;

import lombok.Getter;
import me.waliedyassen.runescript.CompilerError;
import me.waliedyassen.runescript.compiler.ast.AstNode;
import me.waliedyassen.runescript.compiler.ast.AstScript;

/**
 * Represents a compiler error that occurred during the semantic analysis time.
 *
 * @author Walied K. Yassen
 */
public final class SemanticError extends CompilerError {

    /**
     * The owner script of the semantic error.
     */
    @Getter
    private final AstScript script;

    /**
     * Constructs a new {@link CompilerError} type object instance.
     *
     * @param node    the node which error occurred in.
     * @param message the message describing why the error has occurred.
     */
    public SemanticError(AstNode node, String message) {
        super(node.getRange(), message);
        script = (AstScript) node.selectParent(owner -> owner instanceof AstScript);
    }
}

/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.commons.document.Element;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;

/**
 * Represents the smallest unit in the Abstract Syntax Tree (AST).
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public abstract class AstNode implements Element {

    /**
     * The node source code range.
     */
    @Getter
    private final Range range;

    /**
     * Accepts the given {@link AstVisitor} in this node and call the corresponding visit method to this node.
     *
     * @param visitor
     *         the visitor to accept.
     */
    public abstract <T> T accept(AstVisitor<T> visitor);
}

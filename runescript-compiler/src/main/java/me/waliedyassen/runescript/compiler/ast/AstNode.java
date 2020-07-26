/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast;

import lombok.EqualsAndHashCode;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;

/**
 * Represents the smallest unit in the Abstract Syntax Tree (AST).
 *
 * @author Walied K. Yassen
 */
@EqualsAndHashCode(callSuper = true)
public abstract class AstNode extends AstNodeBase {

    /**
     * Constructs a new {@link AstNode} type object instance.
     *
     * @param range the source code range of the node.
     */
    public AstNode(Range range) {
        super(range);
    }

    /**
     * Accepts the given {@link AstVisitor} in this node and call the corresponding visit method to this node.
     *
     * @param visitor the visitor to accept.
     */
    public abstract <E, S> Object accept(AstVisitor<E, S> visitor);
}

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax;

import lombok.EqualsAndHashCode;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * Represents the smallest unit in the Abstract Syntax Tree (AST).
 *
 * @author Walied K. Yassen
 */
@EqualsAndHashCode(callSuper = true)
public abstract class Syntax extends SyntaxBase {

    /**
     * Constructs a new {@link Syntax} type object instance.
     *
     * @param range the source code range of the node.
     */
    public Syntax(Range range) {
        super(range);
    }

    /**
     * Accepts the given {@link SyntaxVisitor} in this node and call the corresponding visit method to this node.
     *
     * @param visitor the visitor to accept.
     */
    public abstract <T> T accept(SyntaxVisitor<T> visitor);
}

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.syntax;

import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.syntax.SyntaxBase;
import me.waliedyassen.runescript.config.syntax.visitor.SyntaxVisitor;

/**
 * Represents a simple node in the Abstract Syntax Tree (AST).
 *
 * @author Walied K. Yassen
 */
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
     * Visits this {@link Syntax node} in the specified {@link SyntaxVisitor visitor}.
     *
     * @param visitor the visitor to visit using.
     * @param <R>     the returned result object type.
     * @return the returned result object.
     */
    public abstract <R> R accept(SyntaxVisitor<R> visitor);
}

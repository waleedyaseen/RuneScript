/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax;

import lombok.EqualsAndHashCode;
import me.waliedyassen.runescript.commons.document.Span;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;
import me.waliedyassen.runescript.type.Type;
import me.waliedyassen.runescript.type.primitive.PrimitiveType;

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
     * @param span the source code range of the node.
     */
    public Syntax(Span span) {
        super(span);
    }

    /**
     * Accepts the given {@link SyntaxVisitor} in this node and call the corresponding visit method to this node.
     *
     * @param visitor the visitor to accept.
     */
    public abstract <T> T accept(SyntaxVisitor<T> visitor);

    /**
     * Returns the type that is assigned to the syntax node or {@link PrimitiveType#UNDEFINED} if none is
     * currently assigned.
     *
     * @return the type that is assigned to the syntax node
     */
    public final Type getType() {
        var type = getAttribute("type");
        if (type == null) {
            return PrimitiveType.UNDEFINED.INSTANCE;
        }
        return (Type) type;
    }

    /**
     * Sets the type of this syntax node.
     *
     * @param type the type to assign to this syntax node.
     */
    public final void setType(Type type) {
        if (type == null) {
            removeAttribute("type");
        } else {
            putAttribute("type", type);
        }
    }

    /**
     * Checks whether or not this syntax node has type assigned to it. This does not care if the type is
     * undefined or not, it simply checks if the a type attribute is present in the attributes map.
     *
     * @return <code>true</code> if there is a type assigned otherwise <code>false</code>.
     */
    public final boolean hasType() {
        return getAttribute("type") != null;
    }
}

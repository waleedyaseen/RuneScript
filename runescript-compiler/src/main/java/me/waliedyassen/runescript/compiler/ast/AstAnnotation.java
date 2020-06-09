/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.expr.AstIdentifier;
import me.waliedyassen.runescript.compiler.ast.expr.literal.AstLiteralInteger;
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;

/**
 * Represents the annotation node. Annotations in RuneScript are just hints for the compiler such as script ids, or any
 * other script specific options.
 *
 * @author Walied K. Yassen
 */
public final class AstAnnotation extends AstNodeBase {

    /**
     * The name of the annotation.
     */
    @Getter
    private final AstIdentifier name;

    /**
     * The value of the annotation.
     */
    @Getter
    private final AstLiteralInteger value;

    /**
     * Constructs a new {@link AstAnnotation} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param name
     *         the name of the annotation.
     * @param value
     *         the value of the annotation.
     */
    public AstAnnotation(Range range, AstIdentifier name, AstLiteralInteger value) {
        super(range);
        this.name = addChild(name);
        this.value = addChild(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E, S> Object accept(AstVisitor<E, S> visitor) {
        return visitor.visit(this);
    }
}

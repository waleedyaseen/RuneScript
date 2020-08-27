/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.syntax.expr.IdentifierSyntax;
import me.waliedyassen.runescript.compiler.syntax.expr.literal.LiteralIntegerSyntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * Represents the annotation node. Annotations in RuneScript are just hints for the compiler such as script ids, or any
 * other script specific options.
 *
 * @author Walied K. Yassen
 */
public final class AnnotationSyntax extends Syntax {

    /**
     * The name of the annotation.
     */
    @Getter
    private final IdentifierSyntax name;

    /**
     * The value of the annotation.
     */
    @Getter
    private final LiteralIntegerSyntax value;

    /**
     * Constructs a new {@link AnnotationSyntax} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param name
     *         the name of the annotation.
     * @param value
     *         the value of the annotation.
     */
    public AnnotationSyntax(Range range, IdentifierSyntax name, LiteralIntegerSyntax value) {
        super(range);
        this.name = addChild(name);
        this.value = addChild(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

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
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;
import me.waliedyassen.runescript.type.Type;

/**
 * Represents a parameter AST node.
 *
 * @author Walied K. Yassen
 */
public final class ParameterSyntax extends Syntax {

    /**
     * The type of the parameter.
     */
    @Getter
    private final Type type;

    /**
     * The name of the parameter.
     */
    @Getter
    private final IdentifierSyntax name;

    /**
     * Construct a new {@link ScriptSyntax} type object instance.
     *
     * @param range
     *         the node source range.
     * @param type
     *         the type of the parameter.
     * @param name
     *         the name of the parameter.
     */
    public ParameterSyntax(Range range, Type type, IdentifierSyntax name) {
        super(range);
        this.type = type;
        this.name = addChild(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E, S> S accept(SyntaxVisitor<E, S> visitor) {
        return visitor.visit(this);
    }
}

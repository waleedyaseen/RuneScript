/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.expr;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;

/**
 * The base class for all of the AST variable nodes.
 *
 * @author Walied K. Yassen
 */
public abstract class VariableSyntax extends ExpressionSyntax {

    /**
     * The name of the variable.
     */
    @Getter
    private final IdentifierSyntax name;

    /**
     * Constructs a new {@link VariableSyntax} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param name
     *         the name of the variable.
     */
    public VariableSyntax(Range range, IdentifierSyntax name) {
        super(range);
        this.name = name;
    }
}

/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.stmt;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.expr.AstExpression;
import me.waliedyassen.runescript.compiler.ast.expr.AstIdentifier;
import me.waliedyassen.runescript.compiler.type.Type;
import me.waliedyassen.runescript.compiler.util.VariableScope;

/**
 * Represents a variable define (or declaration) statement.
 *
 * @author Walied K. Yassen
 */
public final class AstVariableDefine extends AstStatement {

    /**
     * The variable type.
     */
    @Getter
    private final Type type;

    /**
     * The variable name.
     */
    @Getter
    private final AstIdentifier name;

    /**
     * The variable initializer expression.
     */
    @Getter
    private final AstExpression expression;

    /**
     * Construct a new {@link AstVariableDefine} type object instance.
     *
     * @param range
     *         the node source code range.
     * @param type
     *         the type of the variable.
     * @param name
     *         the name of the variable.
     * @param expression
     *         the initializer expression of the variable.
     */
    public AstVariableDefine(Range range, Type type, AstIdentifier name, AstExpression expression) {
        super(range);
        this.type = type;
        this.name = name;
        this.expression = expression;
    }
}

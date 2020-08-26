/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.stmt;

import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.syntax.Syntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * Represents a code statement in the Abstract Syntax Tree. A code statement can be anything that represents an action
 * in code, such as perform a script execution, or execute another statement based on a given condition (if-while-for
 * blocks).
 * </p>
 * This class is the base class for all of our statement types. Any statement must extend this class to be recognised by
 * our Abstract Syntax Tree.
 *
 * @author Walied K. Yassen
 */
public abstract class StatementSyntax extends Syntax {

    /**
     * Construct a new {@link StatementSyntax} type object instance.
     *
     * @param range the node source code range.
     */
    public StatementSyntax(Range range) {
        super(range);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract <E, S> S accept(SyntaxVisitor<E, S> visitor);
}

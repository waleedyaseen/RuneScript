/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.stmt.loop;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.lexer.token.Token;
import me.waliedyassen.runescript.compiler.syntax.stmt.StatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * The Syntax Tree element for the continue loop control statement syntax.
 *
 * @author Walied K. Yassen
 */
public final class ContinueStatementSyntax extends StatementSyntax {

    /**
     * The control keyword token.
     */
    @Getter
    private final Token<Kind> controlToken;

    /**
     * Construct a new {@link ContinueStatementSyntax} type object instance.
     *
     * @param range        the node source code range.
     * @param controlToken the control keyword token.
     */
    public ContinueStatementSyntax(Range range, Token<Kind> controlToken) {
        super(range);
        this.controlToken = controlToken;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E, S> S accept(SyntaxVisitor<E, S> visitor) {
        return visitor.visit(this);
    }
}

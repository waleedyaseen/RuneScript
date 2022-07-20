/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.stmt.loop;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Span;
import me.waliedyassen.runescript.compiler.syntax.SyntaxToken;
import me.waliedyassen.runescript.compiler.syntax.stmt.StatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * The Syntax Tree element for the continue loop control statement syntax.
 *
 * @author Walied K. Yassen
 */
public final class ContinueStatementSyntax extends StatementSyntax {

    /**
     * The toke nof the control keyword.
     */
    @Getter
    private final SyntaxToken controlToken;

    /**
     * The token of the semicolon.
     */
    @Getter
    private final SyntaxToken semicolon;

    /**
     * Construct a new {@link ContinueStatementSyntax} type object instance.
     *
     * @param span        the node source code range.
     * @param controlToken the token of the control keyword.
     * @param semicolon    the token of the semicolon.
     */
    public ContinueStatementSyntax(Span span, SyntaxToken controlToken, SyntaxToken semicolon) {
        super(span);
        this.controlToken = controlToken;
        this.semicolon = semicolon;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

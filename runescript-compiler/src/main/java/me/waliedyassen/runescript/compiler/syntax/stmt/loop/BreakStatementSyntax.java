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
import me.waliedyassen.runescript.compiler.syntax.SyntaxToken;
import me.waliedyassen.runescript.compiler.syntax.stmt.StatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

/**
 * The Syntax Tree element for the break loop control statement syntax.
 *
 * @author Walied K. Yassen
 */
public final class BreakStatementSyntax extends StatementSyntax {

    /**
     * The token of the control keyword.
     */
    @Getter
    private final SyntaxToken controlToken;

    /**
     * The token of the semicolon.
     */
    @Getter
    private final SyntaxToken semicolonToken;

    /**
     * Construct a new {@link BreakStatementSyntax} type object instance.
     *
     * @param range          the node source code range.
     * @param controlToken   the token of the control keyword.
     * @param semicolonToken the token of the semicolon.
     */
    public BreakStatementSyntax(Range range, SyntaxToken controlToken, SyntaxToken semicolonToken) {
        super(range);
        this.controlToken = controlToken;
        this.semicolonToken = semicolonToken;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

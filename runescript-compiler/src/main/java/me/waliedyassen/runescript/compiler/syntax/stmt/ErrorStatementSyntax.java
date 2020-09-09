/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.stmt;

import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.lexer.token.Token;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

import java.beans.Statement;

public final class ErrorStatementSyntax extends StatementSyntax {

    private final Token<Kind> token;
    private final Token<Kind> semicolon;

    public ErrorStatementSyntax(Range range, Token<Kind> token, Token<Kind> semicolon) {
        super(range);
        this.token = token;
        this.semicolon = semicolon;
    }

    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return null;
    }
}

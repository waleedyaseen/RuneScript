/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.syntax;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.config.lexer.token.SyntaxToken;
import me.waliedyassen.runescript.config.syntax.value.ValueSyntax;
import me.waliedyassen.runescript.config.syntax.visitor.SyntaxVisitor;

/**
 * The Syntax Tree object of a constant declaration rule.
 *
 * @author Walied K. Yassen
 */
public final class ConstantSyntax extends Syntax {

    /**
     * The token of the caret symbol.
     */
    @Getter
    private final SyntaxToken caretToken;

    /**
     * The name of the constant.
     */
    @Getter
    private final IdentifierSyntax name;

    /**
     * The value of the constant.
     */
    @Getter
    private final ValueSyntax value;

    /**
     * Constructs a new {@link ConstantSyntax} type object instance.
     *
     * @param range      the source code range of the declaration.
     * @param caretToken the token of the caret symbol.
     * @param name       the name of the constant.
     * @param value      the value of the constant.
     */
    public ConstantSyntax(Range range, SyntaxToken caretToken, IdentifierSyntax name, ValueSyntax value) {
        super(range);
        this.caretToken = caretToken;
        this.name = name;
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R accept(SyntaxVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

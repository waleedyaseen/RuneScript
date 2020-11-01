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

/**
 * Represents a single script name.
 *
 * @author Walied K. Yassen
 */
public final class ScriptNameSyntax extends Syntax {

    /**
     * The syntax token of the left bracket.
     */
    @Getter
    private final SyntaxToken leftBracket;

    /**
     * The syntax token of the comma.
     */
    @Getter
    private final SyntaxToken comma;

    /**
     * The syntax token of the right bracket.
     */
    @Getter
    private final SyntaxToken rightBracket;

    /**
     * The trigger identifier expression.
     */
    @Getter
    private final IdentifierSyntax trigger;

    /**
     * The name identifier expression.
     */
    @Getter
    private final IdentifierSyntax name;

    /**
     * Constructs a new {@link ScriptNameSyntax} type object instance.
     *
     * @param range        the node source code range.
     * @param leftBracket  the token of the left bracket.
     * @param trigger      the trigger identifier expression.
     * @param comma        the token of the comma.
     * @param name         the name identifier expression.
     * @param rightBracket the token of the right bracket.
     */
    public ScriptNameSyntax(Range range, SyntaxToken leftBracket, SyntaxToken comma, SyntaxToken rightBracket, IdentifierSyntax trigger, IdentifierSyntax name) {
        super(range);
        this.leftBracket = leftBracket;
        this.comma = comma;
        this.rightBracket = rightBracket;
        this.trigger = addChild(trigger);
        this.name = name != null ? addChild(name) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Returns the string form of the name syntax.
     *
     * @return the string form of the name syntax.
     */
    public String toText() {
        if (name == null) {
            return String.format("[%s]", trigger.getText());
        } else {
            return String.format("[%s,%s]", trigger.getText(), name.getText());
        }
    }
}

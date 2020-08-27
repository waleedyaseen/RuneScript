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
import me.waliedyassen.runescript.compiler.syntax.Syntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;
import me.waliedyassen.runescript.type.Type;

/**
 * Represents an expression node, all the language expressions must be subclasses of this class.
 *
 * @author Walied K. Yassen
 */
public abstract class ExpressionSyntax extends Syntax {

    /**
     * The type of the expression.
     */
    @Getter
    private Type type;

    /**
     * Constructs a new {@link ExpressionSyntax} type object instance.
     *
     * @param range the expression source code range.
     */
    public ExpressionSyntax(Range range) {
        super(range);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract <T> T accept(SyntaxVisitor<T> visitor);

    /**
     * Sets the type of this expression.
     *
     * @param type the new type of the expression.
     * @return the {@link Type} object that was passed to this method.
     */
    public Type setType(Type type) {
        this.type = type;
        return type;
    }

    /**
     * Returns the text of the specified name {@link ExpressionSyntax}.
     *
     * @param name the name expression.
     * @return the name of the script.
     */
    public static String extractNameText(ExpressionSyntax name) {
        // TODO: Allow `:` in identifiers
        if (name instanceof ComponentSyntax) {
            return ((ComponentSyntax) name).getParentInterface().getText() + ":" + ((ComponentSyntax) name).getComponentName();
        }
        return ((IdentifierSyntax) name).getText();
    }
}

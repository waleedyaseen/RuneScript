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
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;
import me.waliedyassen.runescript.compiler.util.trigger.TriggerType;

/**
 * Represents a call script expression.
 *
 * @author Walied K. Yassen
 */
public final class CallSyntax extends ExpressionSyntax {

    /**
     * The trigger type of the script we want to call.
     */
    @Getter
    private final TriggerType triggerType;

    /**
     * The name of the script we want to call
     */
    @Getter
    private final IdentifierSyntax name;

    /**
     * The arguments we are passing in the call.
     */
    @Getter
    private final ExpressionSyntax[] arguments;

    /**
     * Constructs a new {@link CallSyntax} type object instance.
     *
     * @param range
     *         the expression source code range.
     * @param triggerType
     *         the trigger type of the script we want to call.
     * @param name
     *         the name of the script we want to call
     * @param arguments
     *         the arguments that will be passed to that script.
     */
    public CallSyntax(Range range, TriggerType triggerType, IdentifierSyntax name, ExpressionSyntax[] arguments) {
        super(range);
        this.triggerType = triggerType;
        this.name = addChild(name);
        this.arguments = addChild(arguments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Returns the full name of the target script.
     *
     * @return the full name of the target script.
     */
    public String getFullName() {
        return String.format("[%s,%s]", triggerType.getRepresentation(), name.getText());
    }
}

/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.expr;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;
import me.waliedyassen.runescript.compiler.util.trigger.TriggerType;

/**
 * Represents a call script expression.
 *
 * @author Walied K. Yassen
 */
public final class AstCall extends AstExpression {

    /**
     * The trigger type of the script we want to call.
     */
    @Getter
    private final TriggerType triggerType;

    /**
     * The name of the script we want to call
     */
    @Getter
    private final AstIdentifier name;

    /**
     * The arguments we are passing in the call.
     */
    @Getter
    private final AstExpression[] arguments;

    /**
     * Constructs a new {@link AstCall} type object instance.
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
    public AstCall(Range range, TriggerType triggerType, AstIdentifier name, AstExpression[] arguments) {
        super(range);
        this.triggerType = triggerType;
        this.name = addChild(name);
        this.arguments = addChild(arguments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E, S> E accept(AstVisitor<E, S> visitor) {
        return visitor.visit(this);
    }
}

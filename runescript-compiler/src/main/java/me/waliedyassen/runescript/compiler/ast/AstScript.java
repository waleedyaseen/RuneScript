/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.expr.AstExpression;
import me.waliedyassen.runescript.compiler.ast.expr.AstIdentifier;
import me.waliedyassen.runescript.compiler.ast.stmt.AstBlockStatement;
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;
import me.waliedyassen.runescript.type.Type;

import java.util.List;

/**
 * Represents a complete script with header and code statements. Contains unverified AST nodes that represents the
 * signature and the code statements.
 *
 * @author Walied K. Yassen
 */
@EqualsAndHashCode(callSuper = true)
public final class AstScript extends AstNode {

    /**
     * The annotations of the script.
     */
    @Getter
    private final List<AstAnnotation> annotations;

    /**
     * The trigger type name of the script.
     */
    @Getter
    private final AstIdentifier trigger;

    /**
     * The name of the script.
     */
    @Getter
    private final AstExpression name;

    /**
     * The parameters list of the script..
     */
    @Getter
    private final AstParameter[] parameters;

    /**
     * The return type of the script.
     */
    @Getter
    private final Type type;

    /**
     * The code block of the script.
     */
    @Getter
    private final AstBlockStatement code;

    /**
     * Construct a new {@link AstScript} type object instance.
     *
     * @param annotations the annotations of the script.
     * @param range       the script source range.
     * @param trigger     the script trigger type.
     * @param name        the script name.
     * @param parameters  the script parameters.
     * @param type        the script type.
     * @param code        the script code statement.
     */
    public AstScript(Range range, List<AstAnnotation> annotations, AstIdentifier trigger, AstExpression name, AstParameter[] parameters, Type type, AstBlockStatement code) {
        super(range);
        this.annotations = addChild(annotations);
        this.trigger = addChild(trigger);
        this.name = addChild(name);
        this.parameters = addChild(parameters);
        this.type = type;
        this.code = addChild(code);
    }

    /**
     * {@inheritDoc}
     */
    public <E, S> S accept(AstVisitor<E, S> visitor) {
        return visitor.visit(this);
    }

    /**
     * Attempts to find the {@link AstAnnotation} with the specified {@code name}.
     *
     * @param name the name of the annotation we trying to find.
     * @return the  {@link AstAnnotation} object if found othrwise {@code null}.
     */
    public AstAnnotation findAnnotation(String name) {
        for (AstAnnotation annotation : annotations) {
            if (annotation.getName().getText().equals(name)) {
                return annotation;
            }
        }
        return null;
    }

    /**
     * Returns the full name of the script.
     *
     * @return the full name of the script.
     */
    public String getFullName() {
        return String.format("[%s,%s]", trigger.getText(), AstExpression.extractNameText(name));
    }
}

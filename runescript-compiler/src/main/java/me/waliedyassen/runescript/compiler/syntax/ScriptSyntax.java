/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.syntax.expr.ExpressionSyntax;
import me.waliedyassen.runescript.compiler.syntax.expr.IdentifierSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.BlockStatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;
import me.waliedyassen.runescript.type.Type;

import java.util.List;

/**
 * Represents a complete script with header and code statements. Contains unverified AST nodes that represents the
 * signature and the code statements.
 *
 * @author Walied K. Yassen
 */
@EqualsAndHashCode(callSuper = true)
public final class ScriptSyntax extends SyntaxBase {

    /**
     * The extension of the file containing the script.
     */
    @Getter
    private final String extension;

    /**
     * The annotations of the script.
     */
    @Getter
    private final List<AnnotationSyntax> annotations;

    /**
     * The trigger type name of the script.
     */
    @Getter
    private final IdentifierSyntax trigger;

    /**
     * The name of the script.
     */
    @Getter
    private final ExpressionSyntax name;

    /**
     * The parameters list of the script..
     */
    @Getter
    private final ParameterSyntax[] parameters;

    /**
     * The return type of the script.
     */
    @Getter
    private final Type type;

    /**
     * The code block of the script.
     */
    @Getter
    private final BlockStatementSyntax code;

    /**
     * Construct a new {@link ScriptSyntax} type object instance.
     *
     * @param range
     *         the script source range.
     * @param extension
     *         the extension of the file containg the script.
     * @param annotations
     *         the annotations of the script.
     * @param trigger
     *         the script trigger type.
     * @param name
     *         the script name.
     * @param parameters
     *         the script parameters.
     * @param type
     *         the script type.
     * @param code
     *         the script code statement.
     */
    public ScriptSyntax(Range range, String extension, List<AnnotationSyntax> annotations, IdentifierSyntax trigger, ExpressionSyntax name, ParameterSyntax[] parameters, Type type, BlockStatementSyntax code) {
        super(range);
        this.extension = extension;
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
    public <T> T accept(SyntaxVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Attempts to find the {@link AnnotationSyntax} with the specified {@code name}.
     *
     * @param name
     *         the name of the annotation we trying to find.
     *
     * @return the  {@link AnnotationSyntax} object if found othrwise {@code null}.
     */
    public AnnotationSyntax findAnnotation(String name) {
        for (AnnotationSyntax annotation : annotations) {
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
        return String.format("[%s,%s]", trigger.getText(), ExpressionSyntax.extractNameText(name));
    }
}

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
import me.waliedyassen.runescript.commons.document.Span;
import me.waliedyassen.runescript.compiler.syntax.stmt.BlockStatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor;

import java.util.List;

/**
 * Represents a complete script with header and code statements. Contains unverified AST nodes that represents the
 * signature and the code statements.
 *
 * @author Walied K. Yassen
 */
@EqualsAndHashCode(callSuper = true)
public final class ScriptSyntax extends Syntax {

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
     * The name of the script.
     */
    @Getter
    private final ScriptNameSyntax name;

    /**
     * The parameters list of the script..
     */
    @Getter
    private final ParameterSyntax[] parameters;

    /**
     * The code block of the script.
     */
    @Getter
    private final BlockStatementSyntax code;

    /**
     * Construct a new {@link ScriptSyntax} type object instance.
     *
     * @param span
     *         the script source range.
     * @param extension
     *         the extension of the file containg the script.
     * @param annotations
     *         the annotations of the script.
     * @param name
     *         the name of the script.
     * @param parameters
     *         the script parameters.
     * @param code
     *         the script code statement.
     */
    public ScriptSyntax(Span span, String extension, List<AnnotationSyntax> annotations, ScriptNameSyntax name, ParameterSyntax[] parameters, BlockStatementSyntax code) {
        super(span);
        this.extension = extension;
        this.annotations = addChild(annotations);
        this.name = addChild(name);
        this.parameters = addChild(parameters);
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
}

/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.ast;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.commons.document.Element;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.config.ast.visitor.AstVisitor;

/**
 * Represents a simple node in the Abstract Syntax Tree (AST).
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public abstract class AstNode implements Element {

    /**
     * The node source code range.
     */
    @Getter
    private final Range range;

    /**
     * Visits this {@link AstNode node} in the specified {@link AstVisitor visitor}.
     *
     * @param visitor
     *         the visitor to visit using.
     * @param <R>
     *         the returned result object type.
     *
     * @return the returned result object.
     */
    public abstract <R> R visit(AstVisitor<R> visitor);
}

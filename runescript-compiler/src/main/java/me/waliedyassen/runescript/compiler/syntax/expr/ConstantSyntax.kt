/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax.expr

import me.waliedyassen.runescript.commons.document.Span
import me.waliedyassen.runescript.compiler.syntax.SyntaxToken
import me.waliedyassen.runescript.compiler.syntax.visitor.SyntaxVisitor

/**
 * Represents a constant node, a constant is temporal "variable" that will be replaced with it's value in the
 * compile-time.
 *
 * @author Walied K. Yassen
 */
class ConstantSyntax(span: Span, val caretToken: SyntaxToken, val name: IdentifierSyntax) : ExpressionSyntax(span) {

    var value: Any? = null

    init {
        addChild(name)
    }

    override fun <T> accept(visitor: SyntaxVisitor<T>): T = visitor.visit(this)
}
/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.symbol

import me.waliedyassen.runescript.type.primitive.PrimitiveType

/**
 * The base class for all the symbols in the compiler.
 */
abstract class Symbol {
    abstract val name: String
    abstract val id: Int
}

/**
 * The bare minimum implementation of [Symbol] class.
 */
data class BasicSymbol(
    override val name: String,
    override val id: Int
) : Symbol()

/**
 * A [Symbol] implementation that stores an additional [SymbolType].
 */
data class TypedSymbol(
    override val name: String,
    override val id: Int,
    val type: PrimitiveType<*>
) : Symbol()

/**
 * A [Symbol] implementation that stores an additional [SymbolType] and transmit
 * boolean property.
 */
data class ConfigSymbol(
    override val name: String,
    override val id: Int,
    val type: PrimitiveType<*>,
    val transmit: Boolean
) : Symbol()

/**
 * A [Symbol] implementation for constants, which store the value as is, in string form.
 */
data class ConstantSymbol(
    override val name: String,
    override val id: Int,
    val literal: String,
) : Symbol()

enum class DbColumnProp {
    REQUIRED,
    INDEXED,
    CLIENTSIDE,
    LIST
}

/**
 * A [Symbol] implementation for constants, which store the value as is, in string form.
 */
data class DbColumnSymbol(
    override val name: String,
    override val id: Int,
    val types: List<PrimitiveType<*>>,
    val props: Set<DbColumnProp>
) : Symbol()
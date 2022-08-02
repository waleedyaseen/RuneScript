/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.symbol

import me.waliedyassen.runescript.type.Type

/**
 * Represents the base class for any symbol registered in the symbol table.
 *
 * @author Walied K. Yassen
 */
abstract class Symbol {
    abstract val name: String
    abstract val id: Int
}

/**
 * Represents a configuration type value information.
 *
 * @author Walied K. Yassen
 */
data class ConfigSymbol(
    override val name: String,
    override val id: Int,
    val contentType: Type?
) : Symbol()
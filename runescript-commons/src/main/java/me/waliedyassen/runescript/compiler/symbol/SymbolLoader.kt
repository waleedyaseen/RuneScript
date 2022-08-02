/*
 * Copyright (c) 2022 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.symbol

import me.waliedyassen.runescript.type.primitive.PrimitiveType

interface SymbolLoader<T : Symbol> {

    fun load(line: String): T

    fun save(symbol: T): Symbol
}

object ConfigSymbolLoader : SymbolLoader<ConfigSymbol> {
    override fun load(line: String): ConfigSymbol {
        val split = line.split("!", limit = 3)
        val name = split[0]
        val id = split[1].toInt()
        val type = if (split.size > 2) PrimitiveType.forRepresentation(split[2]) else null
        return ConfigSymbol(name, id, type)
    }

    override fun save(symbol: ConfigSymbol): Symbol {
        error("")
    }
}
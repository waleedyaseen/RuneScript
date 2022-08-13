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
}

object BasicSymbolLoader : SymbolLoader<BasicSymbol> {
    override fun load(line: String): BasicSymbol {
        val split = line.split("!", limit = 2)
        val name = split[0]
        val id = split[1].toInt()
        return BasicSymbol(name, id)
    }
}

object TypedSymbolLoader : SymbolLoader<TypedSymbol> {
    override fun load(line: String): TypedSymbol {
        val split = line.split("!", limit = 3)
        val name = split[0]
        val id = split[1].toInt()
        val type = PrimitiveType.forLiteral(split[2])
        return TypedSymbol(name, id, type)
    }
}

object ConfigSymbolLoader : SymbolLoader<ConfigSymbol> {
    override fun load(line: String): ConfigSymbol {
        val parts = line.split("!", limit = 4)
        val name = parts[0]
        val id = parts[1].toInt()
        val type = PrimitiveType.forLiteral(parts[2])
        val transmit = parts[3].toBooleanStrict()
        return ConfigSymbol(name, id, type, transmit)
    }
}
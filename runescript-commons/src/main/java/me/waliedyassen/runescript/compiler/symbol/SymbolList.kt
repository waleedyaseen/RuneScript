/*
 * Copyright (c) 2022 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.symbol

class SymbolList<T : Symbol> {

    private val nameLookup = mutableMapOf<String, T>()

    fun add(symbol: T) {
        check(!nameLookup.containsKey(symbol.name)) { "SymbolList contains another symbol with the same name: ${symbol.name}" }
        nameLookup[symbol.name] = symbol
    }

    fun remove(name: String) {
        nameLookup -= name
    }

    fun lookupByName(name: String) = nameLookup[name]
}
/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.commons.document

/**
 * Represents a position range within a document.
 *
 * @author Walied K. Yassen
 */
class Span {

    /**
     * The start position of the range in a document.
     */
    var begin = 0
        private set

    /**
     * The width of the range in characters.
     */
    var end = 0
        private set

    /**
     * Constructs a new [Span] type object instance.
     */
    @JvmOverloads
    constructor(begin: Int = 0, end: Int = 0) {
        this.begin = begin
        this.end = end
    }

    /**
     * Constructs a new [Span] type object instance.
     *
     * @param spans the ranges which will be combined into one range.
     */
    constructor(vararg spans: Span) {
        add(*spans)
    }

    /**
     * Performs [.add] for each of the given `ranges`.
     *
     * @param spans the ranges to perform for.
     */
    fun add(vararg spans: Span) {
        for (range in spans) {
            add(range)
        }
    }

    /**
     * Updates this position [Span] to include the specified [range][Span].
     *
     * @param span the range which we wil update this [Span] object to include.
     */
    fun add(span: Span) {
        begin = Math.min(begin, span.begin)
        end = Math.max(end, span.end)
    }

    /**
     * Checks whether the specified [position][LineColumn] is within this position [Span] or
     * not.
     *
     * @param position the position to check whether is it within this position range or not.
     * @return `true` if the specified position is within this range otherwise `false`.
     */
    operator fun contains(position: Int): Boolean {
        return position in begin..end
    }

    fun clone() = Span(begin, end)
}
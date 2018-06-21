/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.commons.stream;

import me.waliedyassen.runescript.commons.document.LineColumn;

/**
 * Repesents a source code characters input stream.
 * 
 * @author Walied K. Yassen
 */
public interface CharStream {

	/**
	 * The NULL character value.
	 */
	char NULL = '\0';

	/**
	 * Takes the next character from the stream the increment the current position.
	 * 
	 * @return the next character or {@link #NULL} if there was none left.
	 */
	char take();

	/**
	 * Takes the next character from the stream without incrementing the current position.
	 * 
	 * @return the next character or {@link #NULL} if there was none left.
	 */
	char peek();

	/**
	 * Marks or saves the current position within the document to be restored later.
	 * 
	 * @see #reset()
	 */
	void mark();

	/**
	 * Resets the current position to the marked position.
	 * 
	 * @see #mark()
	 */
	void reset();

	/**
	 * Gets the current position within the document.
	 * 
	 * @return the current position as {@link LineColumn} object.
	 */
	LineColumn position();
}

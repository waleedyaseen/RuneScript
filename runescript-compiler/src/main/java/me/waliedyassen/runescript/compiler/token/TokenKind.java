/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.token;

/**
 * Represents the token kind, which tells us what each token is, whether an identifier, or a number, or a string, or a
 * keywrod etc..
 * 
 * @author Walied K. Yassen
 */
public enum TokenKind {

	/**
	 * The identifier token kind.
	 */
	IDENTIFIER,

	/**
	 * The end of file token kind.
	 */
	EOF,
}

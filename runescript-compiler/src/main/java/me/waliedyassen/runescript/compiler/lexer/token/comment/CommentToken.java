/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.lexer.token.comment;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.lexer.token.Token;

/**
 * Represents a comment token.
 * 
 * @author Walied K. Yassen
 */
public final class CommentToken extends Token {

	/**
	 * The comment content lines.
	 */
	@Getter
	private final List<String> lines;

	/**
	 * Constructs a new {@link CommentToken} type object instance.
	 * 
	 * @param range
	 *              the comment source code range.
	 * @param lines
	 *              the comment content lines list.
	 */
	public CommentToken(Range range, List<String> lines) {
		super(Kind.COMMENT, range);
		this.lines = Collections.unmodifiableList(lines);
	}
}

/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast;

import java.util.Arrays;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.expr.AstIdentifier;
import me.waliedyassen.runescript.compiler.ast.stmt.AstStatement;

/**
 * Represents a complete script with header and code statements. Contains
 * unverified AST nodes that represents the signature and the code statements.
 *
 * @author Walied K. Yassen
 */
@EqualsAndHashCode
public final class AstScript extends AstNode {

	/**
	 * The script trigger type. The trigger type controls when the script will be
	 * executed, after the occurrence of a specific event or just after a direct
	 * call from another script.
	 */
	@Getter
	private final AstIdentifier trigger;

	/**
	 * The script name. The name must be unique all over the current compiling
	 * scripts, it is used for referring to this script from other scripts.
	 */
	@Getter
	private final AstIdentifier name;

	/**
	 * The script code statements.
	 */
	@Getter
	private final AstStatement[] code;

	/**
	 * Construct a new {@link AstScript} type object instance.
	 *
	 * @param range
	 *                the script source range.
	 * @param trigger
	 *                the script trigger type.
	 * @param name
	 *                the script name.
	 * @param code
	 *                the script code statements.
	 */
	public AstScript(Range range, AstIdentifier trigger, AstIdentifier name, AstStatement[] code) {
		super(range);
		this.trigger = trigger;
		this.name = name;
		this.code = code;
	}
}

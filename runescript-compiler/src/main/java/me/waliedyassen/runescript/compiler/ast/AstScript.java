/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast;

import java.util.Arrays;

import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.expr.AstIdentifier;
import me.waliedyassen.runescript.compiler.ast.stmt.AstStatement;

/**
 * Represents a complete script with header and code statements. Contains
 * unverified AST nodes that represents the signature and the code statements.
 *
 * @author Walied K. Yassen
 */
public final class AstScript extends AstNode {

	/**
	 * The script trigger type. The trigger type controls when the script will be
	 * executed, after the occurrence of a specific event or just after a direct
	 * call from another script.
	 */
	private final AstIdentifier trigger;

	/**
	 * The script name. The name must be unique all over the current compiling
	 * scripts, it is used for referring to this script from other scripts.
	 */
	private final AstIdentifier name;

	/**
	 * The script code statements.
	 */
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

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(code);
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + (trigger == null ? 0 : trigger.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AstScript other = (AstScript) obj;
		if (!Arrays.equals(code, other.code)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (trigger == null) {
			if (other.trigger != null) {
				return false;
			}
		} else if (!trigger.equals(other.trigger)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns an {@link AstIdentifier} object which contains the name of our script
	 * trigger.
	 *
	 * @return an {@link AstIdentifier} object which represents the script trigger
	 *         type.
	 */
	public AstIdentifier getTrigger() {
		return trigger;
	}

	/**
	 * Returns an {@link AstIdentifier} object which contains a string of the script
	 * name.
	 *
	 * @return an {@link AstIdentifier} object which represents the script name.
	 */
	public AstIdentifier getName() {
		return name;
	}

	/**
	 * Returns an array of {@link AstStatement} each statement represents a code
	 * block or an execution line.
	 *
	 * @return an array {@link AstStatement} which represents the script code.
	 */
	public AstStatement[] getCode() {
		return code;
	}
}

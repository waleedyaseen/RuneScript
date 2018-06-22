/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.commons.document;

/**
 * Represents a ("line and column") structure with additonal utilitiy methods.
 * 
 * @author Walied K. Yassen
 */
public final class LineColumn {

	/**
	 * The line number within the document.
	 */
	private final int line;

	/**
	 * The column number within the line.
	 */
	private final int column;

	/**
	 * Constructs a new {@link LineColumn} type object instance.
	 * 
	 * @param line
	 *               the line number within the document.
	 * @param column
	 *               the column number within the line.
	 */
	public LineColumn(int line, int column) {
		this.line = line;
		this.column = column;
	}

	/**
	 * Checks whether or not the given {@linkplain LineColumn position} is lesser than this position.
	 * 
	 * @param other
	 *              the other position to check whether it is lesser or not.
	 * @return <code>true</code> if the given position is lesser than this position otherwise <code>false</code>.
	 */
	public boolean isLesserThan(LineColumn other) {
		if (line == other.line) {
			return column < other.column;
		} else if (line < other.line) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether or not the given {@linkplain LineColumn position} is greater than this position.
	 * 
	 * @param other
	 *              the other position to check whether it is greater or not.
	 * @return <code>true</code> if the given position is greater than this position otherwise <code>false</code>.
	 */
	public boolean isGreaterThan(LineColumn other) {
		if (line == other.line) {
			return column > other.column;
		} else if (line > other.line) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "line: " + line + ", column: " + column;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + column;
		result = prime * result + line;
		return result;
	}

	/*
	 * (non-Javadoc)
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
		LineColumn other = (LineColumn) obj;
		if (column != other.column) {
			return false;
		}
		if (line != other.line) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the line number within the document.
	 * 
	 * @return the line number.
	 */
	public int getLine() {
		return line;
	}

	/**
	 * Gets the column number within the line.
	 * 
	 * @return the column number.
	 */
	public int getColumn() {
		return column;
	}
}

/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.commons.document;

/**
 * Represents a position range within a document.
 * 
 * @author Walied K. Yassen
 */
public final class Range {

	/**
	 * The range start position.
	 */
	private LineColumn start;

	/**
	 * The range end position.
	 */
	private LineColumn end;

	/**
	 * Constructs a new {@link Range} type object instance.
	 * 
	 * @param start
	 *              the start position.
	 * @param end
	 *              the end position.
	 */
	public Range(LineColumn start, LineColumn end) {
		this.start = start;
		this.end = end;
	}

	/**
	 * Updates this position {@link Range} to include the specified {@linkplain LineColumn position}.
	 * 
	 * @param position
	 *                 the position which we will update this {@link Range} object to include.
	 */
	public void add(LineColumn position) {
		if (position.isLesserThan(start)) {
			start = position;
		} else if (position.isGreaterThan(end)) {
			end = position;
		}
	}

	/**
	 * Checks whether or not the specified {@linkplain LineColumn position} is within this position {@link Range} or not.
	 * 
	 * @param position
	 *                 the position to check whether is it within this position range or not.
	 * @return <code>true</code> if the specified position is within this range otherwise <code>false</code>.
	 */
	public boolean contains(LineColumn position) {
		return position.isGreaterThan(start) && position.isLesserThan(end);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (end == null ? 0 : end.hashCode());
		result = prime * result + (start == null ? 0 : start.hashCode());
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
		Range other = (Range) obj;
		if (end == null) {
			if (other.end != null) {
				return false;
			}
		} else if (!end.equals(other.end)) {
			return false;
		}
		if (start == null) {
			if (other.start != null) {
				return false;
			}
		} else if (!start.equals(other.start)) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the start position.
	 * 
	 * @return the start position.
	 */
	public LineColumn getStart() {
		return start;
	}

	/**
	 * Gets the end position.
	 * 
	 * @return the end position.
	 */
	public LineColumn getEnd() {
		return end;
	}
}

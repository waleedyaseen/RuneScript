/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.commons.stream;

import java.io.IOException;
import java.io.InputStream;

import me.waliedyassen.runescript.commons.document.LineColumn;

/**
 * Represents a buffered character stream, it reads all the data from {@link InputStream} and then caches the data into
 * a {@code char[]} object.
 * 
 * @author Walied K. Yassen
 */
public final class BufferedCharStream implements CharStream {

	/**
	 * The default tab size, how many spaces the special tab character advaces the column pointer.
	 */
	private static final int DEFAULT_TABSIZE = 4;

	/**
	 * The characters buffer data.
	 */
	private final char[] buffer;

	/**
	 * The tab size for position calculations.
	 */
	private final int tabSize;

	/**
	 * The characters buffer position.
	 */
	private int pos;

	/**
	 * The current line witOOFhin the document.
	 */
	private int line = 1;

	/**
	 * The current column within the document.
	 */
	private int column = 1;

	/**
	 * The marked position.
	 */
	private int m_pos = -1, m_line = -1, m_column = -1;

	/**
	 * Constructs a new {@link BufferedCharStream} type object instance.
	 * 
	 * @param stream
	 *                   the source code input stream.
	 * @throws IOException
	 *                         if anything occurs while reading the data from the specified {@link InputStream}.
	 */
	public BufferedCharStream(InputStream stream) throws IOException {
		this(stream, DEFAULT_TABSIZE);
	}

	/**
	 * Constructs a new {@link BufferedCharStream} type object instance.
	 * 
	 * @param stream
	 *                    the source code input stream.
	 * @param tabSize
	 *                    the tab size, reprsents how many spaces should we increase the column pointer by after the tab
	 *                    special character.
	 * @throws IOException
	 *                         if anything occurs while reading the data from the specified {@link InputStream}.
	 */
	public BufferedCharStream(InputStream stream, int tabSize) throws IOException {
		this.tabSize = tabSize;
		buffer = new char[stream.available()];
		for (int index = 0; index < buffer.length; index++) {
			buffer[index] = (char) stream.read();
		}
		// Skips the CR and LF characters in-case they are present as the first characters in the file.
		skipCrLf();
	}

	/*
	 * (non-Javadoc)
	 * @see me.waliedyassen.runescript.commons.stream.CharStream#take()
	 */
	@Override
	public char take() {
		if (pos >= buffer.length) {
			return NULL;
		}
		char ch = buffer[pos++];
		if (ch == '\t') {
			column += tabSize - (column - 1) % tabSize;
		} else {
			column++;
		}
		skipCrLf();
		return ch;
	}

	/**
	 * Skips the carriage return and line feed special characters.
	 */
	private void skipCrLf() {
		do {
			if (peek() == '\r') {
				pos++;
			}
			if (peek() == '\n') {
				pos++;
				line++;
				column = 1;
				continue;
			}
			break;
		} while (true);
	}

	/*
	 * (non-Javadoc)
	 * @see me.waliedyassen.runescript.commons.stream.CharStream#peek()
	 */
	@Override
	public char peek() {
		if (pos >= buffer.length) {
			return NULL;
		}
		return buffer[pos];
	}

	/*
	 * (non-Javadoc)
	 * @see me.waliedyassen.runescript.commons.stream.CharStream#mark()
	 */
	@Override
	public void mark() {
		// TODO: we can just save the line and colum positions to save time
		m_pos = pos;
		m_line = line;
		m_column = column;
	}

	/*
	 * (non-Javadoc)
	 * @see me.waliedyassen.runescript.commons.stream.CharStream#reset()
	 */
	@Override
	public void reset() {
		if (pos == -1) {
			throw new IllegalStateException("The stream has no marker currently!");
		}
		pos = m_pos;
		line = m_line;
		column = m_column;
		m_pos = m_line = m_column = -1;
	}

	/*
	 * (non-Javadoc)
	 * @see me.waliedyassen.runescript.commons.stream.CharStream#position()
	 */
	@Override
	public LineColumn position() {
		return new LineColumn(line, column);
	}

}
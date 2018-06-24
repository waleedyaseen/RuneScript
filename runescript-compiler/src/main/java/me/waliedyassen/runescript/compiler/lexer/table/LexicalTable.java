/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.lexer.table;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import me.waliedyassen.runescript.compiler.lexer.token.Kind;

/**
 * Represents the symbol table for the lexical phase of the compilation process, it holds all the symbols that we need
 * during the tokenizing process, whether it is being a separator, a keyword or an operator etc..
 * 
 * @author Walied K. Yassen
 */
public final class LexicalTable {

	/**
	 * An empty lexical phase symbol table.
	 */
	public static final LexicalTable DEFAULT_TABLE = new LexicalTable(true);

	/**
	 * The registered keywords.
	 */
	private final Map<String, Kind> keywords = new HashMap<String, Kind>();

	/**
	 * Constructs a new {@link LexicalTable} type object instance.
	 * 
	 * @param defaultTable
	 *                     whether should we register the default table content to this table or not.
	 */
	public LexicalTable(boolean defaultTable) {
		if (defaultTable) {
			initialiseDefault();
		}
	}

	/**
	 * Initialises the default lexical table content.
	 */
	private void initialiseDefault() {
		registerKeyword("true", Kind.BOOL);
		registerKeyword("false", Kind.BOOL);
	}

	/**
	 * Registers a new keyword into the table.
	 * 
	 * @param word
	 *             the keyword text, will be converted to lower case.
	 * @param kind
	 *             the keyword token kind.
	 * @throws IllegalArgumentException
	 *                                  if the keyword was already registered.
	 */
	public void registerKeyword(String word, Kind kind) {
		Objects.requireNonNull(word, "word");
		Objects.requireNonNull(kind, "kind");
		word = word.toLowerCase();
		if (keywords.containsKey(word)) {
			throw new IllegalArgumentException("The specified keyword was already registered.");
		}
		keywords.put(word, kind);
	}

	/**
	 * Looks-up the {@link Kind} for the specified keyword.
	 * 
	 * @param word
	 *             the keyword text.
	 * @return the {@link Kind}.
	 */
	public Kind lookupKeyword(String word) {
		return keywords.get(word);
	}

	/**
	 * Checks whether or not the specified {@code word} is registered as a keyword.
	 * 
	 * @param word
	 *             the word to check if it is whether a keyword or not
	 * @return <code>true</code> if the specified <code>word</code> is a keyword otherwise {@code null}.
	 */
	public boolean isKeyword(String word) {
		return keywords.containsKey(word);
	}
}

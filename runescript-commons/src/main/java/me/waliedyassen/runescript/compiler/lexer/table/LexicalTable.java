/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.lexer.table;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the symbol table for the lexical phase of the compilation process, it holds all the symbols that we need
 * during the tokenizing process, whether it is being a separator, a keyword or an operator etc..
 *
 * @param <K>
 *         the lexical token kind type.
 *
 * @author Walied K. Yassen
 */
public final class LexicalTable<K> {

    /**
     * The registered keywords.
     */
    @Getter
    private final Map<String, K> keywords = new HashMap<>();

    /**
     * The registered separators.
     */
    @Getter
    private final Map<Character, K> separators = new HashMap<>();

    /**
     * The registered operators.
     */
    @Getter
    private final Map<String, K> operators = new HashMap<>();

    /**
     * The operator max size.
     */
    private int operatorSize;

    /**
     * The operator starts.
     */
    private String operatorStarts;

    /**
     * Registers a new keyword into the table.
     *
     * @param word
     *         the keyword text, will be converted to lower case.
     * @param kind
     *         the keyword token kind.
     *
     * @throws IllegalArgumentException
     *         if the keyword was already registered.
     */
    public void registerKeyword(String word, K kind) {
        Objects.requireNonNull(word, "word");
        Objects.requireNonNull(kind, "kind");
        word = word.toLowerCase();
        if (keywords.containsKey(word)) {
            throw new IllegalArgumentException("The specified keyword was already registered.");
        }
        keywords.put(word, kind);
    }

    /**
     * Looks-up the {@link K} for the specified keyword.
     *
     * @param word
     *         the keyword text.
     *
     * @return the {@link K} of the keyword if it was present otherwise {@code null}.
     */
    public K lookupKeyword(String word) {
        return keywords.get(word);
    }

    /**
     * Checks whether or not the specified {@code word} is registered as a keyword.
     *
     * @param word
     *         the word to check if it is whether a keyword or not
     *
     * @return <code>true</code> if the specified <code>word</code> is a keyword otherwise {@code null}.
     */
    public boolean isKeyword(String word) {
        return keywords.containsKey(word);
    }

    /**
     * Registers a new separator into the table.
     *
     * @param character
     *         the separator character.
     * @param kind
     *         the separator token kind.
     *
     * @throws IllegalArgumentException
     *         if the separator was already registered.
     */
    public void registerSeparator(char character, K kind) {
        Objects.requireNonNull(kind, "kind");
        if (separators.containsKey(character)) {
            throw new IllegalArgumentException("The specified separator was already registered.");
        }
        separators.put(character, kind);
    }

    /**
     * Looks-up the separator token {@link K} for the specified separator character.
     *
     * @param character
     *         the separator character.
     *
     * @return the {@link K} of the separator if it was present otherwise {@code null}.
     */
    public K lookupSeparator(char character) {
        return separators.get(character);
    }

    /**
     * Checks whether or not the specified {@code character} is registered as a separator.
     *
     * @param character
     *         the word to check if it is whether a separator or not
     *
     * @return <code>true</code> if the specified <code>character</code> is a separator otherwise {@code null}.
     */
    public boolean isSeparator(char character) {
        return separators.containsKey(character);
    }

    /**
     * Registers a new operator into the table.
     *
     * @param sequence
     *         the operator sequence.
     * @param kind
     *         the operator token kind.
     *
     * @throws IllegalArgumentException
     *         if the operator was already registered.
     */
    public void registerOperator(String sequence, K kind) {
        Objects.requireNonNull(sequence, "sequence");
        Objects.requireNonNull(kind, "kind");
        if (sequence.length() < 1) {
            throw new IllegalArgumentException("The operator size must be greater than zero!");
        }
        if (operators.containsKey(sequence)) {
            throw new IllegalArgumentException("The specifie operator was already registered.");
        }
        operators.put(sequence, kind);
        operatorStarts += sequence.charAt(0);
        if (sequence.length() > operatorSize) {
            operatorSize = sequence.length();
        }
    }

    /**
     * Looks-up the operator token {@link K} for the specified operator sequence.
     *
     * @param sequence
     *         the operator sequence.
     *
     * @return the {@link K} of the operator if it was present otherwise {@code null}.
     */
    public K lookupOperator(String sequence) {
        return operators.get(sequence);
    }

    /**
     * Checks whether or not the specified {@code sequence} is registered as a operator.
     *
     * @param sequence
     *         the word to check if it is whether a operator or not
     *
     * @return <code>true</code> if the specified <code>sequence</code> is a operator otherwise {@code null}.
     */
    public boolean isOperator(String sequence) {
        return operators.containsKey(sequence);
    }

    /**
     * Checks whether the specified character is a start of an operator character or not.
     *
     * @param character
     *         the character value to check.
     *
     * @return <code>true</code> if it was otherwise <code>false</code>.
     */
    public boolean isOperatorStart(char character) {
        if (operatorStarts == null) {
            // no operators were registered.
            return false;
        }
        return operatorStarts.indexOf(character) != -1;
    }

    /**
     * Gets the maximum operator size.
     *
     * @return the maximum operator size.
     */
    public int getOperatorSize() {
        return operatorSize;
    }

}

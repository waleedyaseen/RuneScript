/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.parser;

import lombok.var;
import me.waliedyassen.runescript.compiler.parser.ParserBase;
import me.waliedyassen.runescript.config.ast.AstConfig;
import me.waliedyassen.runescript.config.ast.AstIdentifier;
import me.waliedyassen.runescript.config.ast.AstProperty;
import me.waliedyassen.runescript.config.ast.value.*;
import me.waliedyassen.runescript.config.lexer.Lexer;
import me.waliedyassen.runescript.config.lexer.token.Kind;
import me.waliedyassen.runescript.type.PrimitiveType;

import java.util.ArrayList;

import static me.waliedyassen.runescript.config.lexer.token.Kind.*;

/**
 * Represents the configuration grammar Abstract-Syntax-Tree parser.
 *
 * @author Walied K. Yassen
 */
public final class ConfigParser extends ParserBase<Kind> {

    /**
     * Constructs a new {@link ConfigParser} type object instance.
     *
     * @param lexer
     *         the lexical parser to use for tokens.
     */
    public ConfigParser(Lexer lexer) {
        super(lexer, EOF);
    }

    /**
     * Attempts to parse an array of {@link AstConfig} objects from the next sequence of tokens.
     *
     * @return the parsed array of {@link AstConfig} objects.
     */
    public AstConfig[] configs() {
        var configs = new ArrayList<AstConfig>();
        while (peekKind() == LBRACKET) {
            configs.add(config());
        }
        return configs.toArray(new AstConfig[0]);
    }

    /**
     * Attempts to parse an {@link AstConfig} object from the next sequence of tokens.
     *
     * @return the parsed {@link AstConfig} object.
     */
    public AstConfig config() {
        pushRange();
        consume(LBRACKET);
        var name = identifier();
        consume(RBRACKET);
        var properties = propertyList();
        return new AstConfig(popRange(), name, properties);
    }

    /**
     * Attempts to parse an array of {@link AstProperty} objects from the next sequence of tokens.
     *
     * @return the parsed array of {@link AstProperty} objects.
     */
    public AstProperty[] propertyList() {
        pushRange();
        var properties = new ArrayList<AstProperty>();
        while (isKey()) {
            properties.add(property());
        }
        return properties.toArray(new AstProperty[0]);
    }

    /**
     * Attempts to parse an {@link AstProperty} object from the next sequence of tokens.
     *
     * @return the parsed {@link AstProperty} object.
     */
    public AstProperty property() {
        pushRange();
        var name = advancedIdentifier();
        consume(EQUAL);
        var values = values();
        return new AstProperty(popRange(), name, values);
    }

    /**
     * Checks whether or not the next token can be fit as a property key.
     *
     * @return <code>true</code> if it can otherwise <code>false</code>.
     */
    private boolean isKey() {
        return isAdvancedIdentifier();
    }

    /**
     * Attempts to parse an array of {@link AstValue} objects from the next sequence of tokens.
     *
     * @return the parsed array of {@link AstValue} objects.
     */
    private AstValue[] values() {
        var values = new ArrayList<AstValue>();
        do {
            values.add(value());
        } while (consumeIf(COMMA));
        return values.toArray(new AstValue[0]);
    }

    /**
     * Attempts to parse an {@link AstValue} object from the next sequence of tokens.
     *
     * @return the parsed {@link AstValue} object.
     */
    private AstValue value() {
        switch (peekKind()) {
            case STRING:
                return valueString();
            case INTEGER:
                return valueInteger();
            case LONG:
                return valueLong();
            case BOOLEAN:
                return valueBoolean();
            case TYPE:
                return valueType();
            case CARET:
                return valueConstant();
            case IDENTIFIER:
                return valueConfig();
            default:
                throwError(consume(), "Expected a property value");
                return null;
        }
    }

    /**
     * Attempts to parse an {@link AstValueConfig} object from the next sequence of tokens.
     *
     * @return the parsed {@link AstValueConfig} object.
     */
    private AstValueConfig valueConfig() {
        pushRange();
        var name = identifier();
        return new AstValueConfig(popRange(), name);
    }

    /**
     * Attempts to parse an {@link AstValueString} object from the next sequence of tokens.
     *
     * @return the parsed {@link AstValueString} object.
     */
    public AstValueString valueString() {
        pushRange();
        var text = consume(STRING).getLexeme();
        return new AstValueString(popRange(), text);
    }

    /**
     * Attempts to parse an {@link AstValueInteger} object from the next sequence of tokens.
     *
     * @return the parsed {@link AstValueInteger} object.
     */
    public AstValueInteger valueInteger() {
        pushRange();
        var token = consume(INTEGER);
        try {
            return new AstValueInteger(popRange(), Integer.parseInt(token.getLexeme()));
        } catch (NumberFormatException e) {
            throw createError(token, "The literal " + token.getLexeme() + " of type int is out of range");
        }
    }

    /**
     * Attempts to parse an {@link AstValueLong} object from the next sequence of tokens.
     *
     * @return the parsed {@link AstValueLong} object.
     */
    public AstValueLong valueLong() {
        pushRange();
        var token = consume(LONG);
        try {
            return new AstValueLong(popRange(), Long.parseLong(token.getLexeme()));
        } catch (NumberFormatException e) {
            throw createError(token, "The literal " + token.getLexeme() + " of type long is out of range");
        }
    }

    /**
     * Attempts to parse an {@link AstValueInteger} object from the next sequence of tokens.
     *
     * @return the parsed {@link AstValueInteger} object.
     */
    public AstValueBoolean valueBoolean() {
        pushRange();
        var token = consume(BOOLEAN);
        try {
            return new AstValueBoolean(popRange(), "yes".contentEquals(token.getLexeme()) || "true".contentEquals(token.getLexeme()));
        } catch (NumberFormatException e) {
            throw createError(token, "The literal " + token.getLexeme() + " of type int is out of range");
        }
    }

    /**
     * Attempts to parse an {@link AstValueType} object from the next sequence of tokens.
     *
     * @return the parsed {@link AstValueType} object.
     */
    private AstValueType valueType() {
        pushRange();
        var identifier = consume(TYPE);
        return new AstValueType(popRange(), PrimitiveType.forRepresentation(identifier.getLexeme()));
    }

    /**
     * Attempts to parse an {@link AstValueConstant} object from the next sequence of tokens.
     *
     * @return the parsed {@link AstValueConstant} object.
     */
    private AstValueConstant valueConstant() {
        pushRange();
        consume(CARET);
        var name = identifier();
        return new AstValueConstant(popRange(), name);
    }


    /**
     * Attempts to parse an {@link AstIdentifier} object from the next sequence of tokens.
     *
     * @return the parsed {@link AstIdentifier} object.
     */
    private AstIdentifier advancedIdentifier() {
        pushRange();
        var token = consume();
        switch (token.getKind()) {
            case IDENTIFIER:
            case TYPE:
                return new AstIdentifier(popRange(), token.getLexeme());
            default:
                throw createError(token, "Expected an identifier");
        }
    }

    /**
     * Checks whether or not the next token can be fit as an advanced identifier.
     *
     * @return <code>true</code> if it can otherwise <code>false</code>.
     */
    private boolean isAdvancedIdentifier() {
        switch (peekKind()) {
            case IDENTIFIER:
            case TYPE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Attempts to parse an {@link AstIdentifier} object from the next sequence of tokens.
     *
     * @return the parsed {@link AstIdentifier} object.
     */
    public AstIdentifier identifier() {
        pushRange();
        var text = consume(IDENTIFIER).getLexeme();
        return new AstIdentifier(popRange(), text);
    }

}

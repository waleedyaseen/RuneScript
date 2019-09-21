/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.parser;

import me.waliedyassen.runescript.config.ast.AstConfig;
import me.waliedyassen.runescript.config.ast.AstIdentifier;
import me.waliedyassen.runescript.config.ast.AstProperty;
import me.waliedyassen.runescript.config.ast.value.AstValue;
import me.waliedyassen.runescript.config.ast.value.AstValueInteger;
import me.waliedyassen.runescript.config.ast.value.AstValueLong;
import me.waliedyassen.runescript.config.ast.value.AstValueString;
import me.waliedyassen.runescript.config.lexer.Lexer;
import me.waliedyassen.runescript.config.lexer.token.Kind;
import me.waliedyassen.runescript.parser.ParserBase;

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
     * Attempts to parse an {@link AstProperty} object from the next sequence of tokens.
     *
     * @return the parsed {@link AstProperty} object.
     */
    public AstProperty property() {
        pushRange();
        var name = identifier();
        consume(EQUAL);
        var values = values();
        return new AstProperty(popRange(), name, values);
    }

    /**
     * Attempts to parse an array of {@link AstProperty} objects from the next sequence of tokens.
     *
     * @return the parsed array of {@link AstProperty} objects.
     */
    public AstProperty[] propertyList() {
        pushRange();
        var properties = new ArrayList<AstProperty>();
        while (peekKind() == IDENTIFIER) {
            properties.add(property());
        }
        return properties.toArray(AstProperty[]::new);
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
        return values.toArray(AstValue[]::new);
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
            default:
                throwError(consume(), "Expected a property value");
                return null;
        }
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

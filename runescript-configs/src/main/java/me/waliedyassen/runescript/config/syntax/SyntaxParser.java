/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.config.syntax;

import lombok.var;
import me.waliedyassen.runescript.compiler.error.ErrorReporter;
import me.waliedyassen.runescript.compiler.parser.ParserBase;
import me.waliedyassen.runescript.config.syntax.value.*;
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
public final class SyntaxParser extends ParserBase<Kind> {

    /**
     * Constructs a new {@link SyntaxParser} type object instance.
     *
     * @param errorReporter the error reporter of the syntax parser.
     * @param lexer         the lexical parser to use for tokens.
     */
    public SyntaxParser(ErrorReporter errorReporter, Lexer lexer) {
        super(errorReporter, lexer, EOF);
    }

    /**
     * Attempts to parse an array of {@link ConfigSyntax} objects from the next sequence of tokens.
     *
     * @return the parsed array of {@link ConfigSyntax} objects.
     */
    public ConfigSyntax[] configs() {
        var configs = new ArrayList<ConfigSyntax>();
        while (peekKind() == LBRACKET) {
            configs.add(config());
        }
        return configs.toArray(new ConfigSyntax[0]);
    }

    /**
     * Attempts to parse an {@link ConfigSyntax} object from the next sequence of tokens.
     *
     * @return the parsed {@link ConfigSyntax} object.
     */
    public ConfigSyntax config() {
        pushRange();
        consume(LBRACKET);
        var name = identifier();
        consume(RBRACKET);
        var properties = propertyList();
        return new ConfigSyntax(popRange(), name, properties);
    }

    /**
     * Attempts to parse an array of {@link PropertySyntax} objects from the next sequence of tokens.
     *
     * @return the parsed array of {@link PropertySyntax} objects.
     */
    public PropertySyntax[] propertyList() {
        pushRange();
        var properties = new ArrayList<PropertySyntax>();
        while (isKey()) {
            properties.add(property());
        }
        return properties.toArray(new PropertySyntax[0]);
    }

    /**
     * Attempts to parse an {@link PropertySyntax} object from the next sequence of tokens.
     *
     * @return the parsed {@link PropertySyntax} object.
     */
    public PropertySyntax property() {
        pushRange();
        var name = advancedIdentifier();
        consume(EQUAL);
        var values = values();
        return new PropertySyntax(popRange(), name, values);
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
     * Attempts to parse an array of {@link ValueSyntax} objects from the next sequence of tokens.
     *
     * @return the parsed array of {@link ValueSyntax} objects.
     */
    private ValueSyntax[] values() {
        var values = new ArrayList<ValueSyntax>();
        do {
            values.add(value());
        } while (consumeIf(COMMA));
        return values.toArray(new ValueSyntax[0]);
    }

    /**
     * Attempts to parse an {@link ValueSyntax} object from the next sequence of tokens.
     *
     * @return the parsed {@link ValueSyntax} object.
     */
    private ValueSyntax value() {
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
                addError(consume(), "Expected a property value");
                return errorValueSyntax();
        }
    }

    /**
     * Returns a newly created {@link ErrorValueSyntax} object.
     *
     * @return the created {@link ErrorValueSyntax} object.
     */
    private ErrorValueSyntax errorValueSyntax() {
        return new ErrorValueSyntax(emptyRange());
    }

    /**
     * Attempts to parse an {@link ValueConfigSyntax} object from the next sequence of tokens.
     *
     * @return the parsed {@link ValueConfigSyntax} object.
     */
    private ValueConfigSyntax valueConfig() {
        pushRange();
        var name = identifier();
        return new ValueConfigSyntax(popRange(), name);
    }

    /**
     * Attempts to parse an {@link ValueStringSyntax} object from the next sequence of tokens.
     *
     * @return the parsed {@link ValueStringSyntax} object.
     */
    public ValueStringSyntax valueString() {
        pushRange();
        var text = consume(STRING).getLexeme();
        return new ValueStringSyntax(popRange(), text);
    }

    /**
     * Attempts to parse an {@link ValueIntegerSyntax} object from the next sequence of tokens.
     *
     * @return the parsed {@link ValueIntegerSyntax} object.
     */
    public ValueIntegerSyntax valueInteger() {
        pushRange();
        var token = consume(INTEGER);
        try {
            var radix = 10;
            var text = token.getLexeme();
            if (text.startsWith("0x")) {
                text = text.substring(2);
                radix = 16;
            }
            return new ValueIntegerSyntax(popRange(), Integer.parseInt(text, radix));
        } catch (NumberFormatException e) {
            throw createError(token, "The value " + token.getLexeme() + " of type int is out of range");
        }
    }

    /**
     * Attempts to parse an {@link ValueLongSyntax} object from the next sequence of tokens.
     *
     * @return the parsed {@link ValueLongSyntax} object.
     */
    public ValueLongSyntax valueLong() {
        pushRange();
        var token = consume(LONG);
        try {
            return new ValueLongSyntax(popRange(), Long.parseLong(token.getLexeme()));
        } catch (NumberFormatException e) {
            throw createError(token, "The literal " + token.getLexeme() + " of type long is out of range");
        }
    }

    /**
     * Attempts to parse an {@link ValueIntegerSyntax} object from the next sequence of tokens.
     *
     * @return the parsed {@link ValueIntegerSyntax} object.
     */
    public ValueBooleanSyntax valueBoolean() {
        pushRange();
        var token = consume(BOOLEAN);
        try {
            return new ValueBooleanSyntax(popRange(), "yes".contentEquals(token.getLexeme()) || "true".contentEquals(token.getLexeme()));
        } catch (NumberFormatException e) {
            throw createError(token, "The literal " + token.getLexeme() + " of type int is out of range");
        }
    }

    /**
     * Attempts to parse an {@link ValueTypeSyntax} object from the next sequence of tokens.
     *
     * @return the parsed {@link ValueTypeSyntax} object.
     */
    private ValueTypeSyntax valueType() {
        pushRange();
        var identifier = consume(TYPE);
        return new ValueTypeSyntax(popRange(), PrimitiveType.forRepresentation(identifier.getLexeme()));
    }

    /**
     * Attempts to parse an {@link ValueConstantSyntax} object from the next sequence of tokens.
     *
     * @return the parsed {@link ValueConstantSyntax} object.
     */
    private ValueConstantSyntax valueConstant() {
        pushRange();
        consume(CARET);
        var name = identifier();
        return new ValueConstantSyntax(popRange(), name);
    }


    /**
     * Attempts to parse an {@link IdentifierSyntax} object from the next sequence of tokens.
     *
     * @return the parsed {@link IdentifierSyntax} object.
     */
    private IdentifierSyntax advancedIdentifier() {
        pushRange();
        var token = consume();
        switch (token.getKind()) {
            case IDENTIFIER:
            case TYPE:
                return new IdentifierSyntax(popRange(), token.getLexeme());
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
     * Attempts to parse an {@link IdentifierSyntax} object from the next sequence of tokens.
     *
     * @return the parsed {@link IdentifierSyntax} object.
     */
    public IdentifierSyntax identifier() {
        pushRange();
        var text = consume(IDENTIFIER).getLexeme();
        return new IdentifierSyntax(popRange(), text);
    }

}

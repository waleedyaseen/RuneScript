/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.parser;

import me.waliedyassen.runescript.commons.document.Element;
import me.waliedyassen.runescript.commons.document.Range;
import me.waliedyassen.runescript.compiler.ast.AstScript;
import me.waliedyassen.runescript.compiler.ast.expr.AstExpression;
import me.waliedyassen.runescript.compiler.ast.expr.AstIdentifier;
import me.waliedyassen.runescript.compiler.ast.expr.var.AstConstant;
import me.waliedyassen.runescript.compiler.ast.expr.var.AstGlobalVariable;
import me.waliedyassen.runescript.compiler.ast.expr.var.AstLocalVariable;
import me.waliedyassen.runescript.compiler.ast.literal.*;
import me.waliedyassen.runescript.compiler.ast.stmt.AstBlockStatement;
import me.waliedyassen.runescript.compiler.ast.stmt.AstReturnStatement;
import me.waliedyassen.runescript.compiler.ast.stmt.AstStatement;
import me.waliedyassen.runescript.compiler.ast.stmt.conditional.AstIfStatement;
import me.waliedyassen.runescript.compiler.ast.stmt.conditional.AstWhileStatement;
import me.waliedyassen.runescript.compiler.lexer.Lexer;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.lexer.token.Token;

import java.util.ArrayList;
import java.util.Stack;

import static me.waliedyassen.runescript.compiler.lexer.token.Kind.*;

/**
 * Represents the grammar parser, it takes a {@link Lexer} fed with
 * {@link Token} objects, and then it attempts to apply our RuneScript grammar
 * rules to these tokens.
 *
 * @author Walied K. Yassen
 */
public final class Parser {

    // TODO: Detailed documentation

    /**
     * The {@link Range} object stack. It is used to calculate the nested
     * {@link Range}s.
     */
    private final Stack<Range> ranges = new Stack<>();

    /**
     * The lexical phase result object.
     */
    private final Lexer lexer;

    /**
     * Constructs a new {@link Parser} type object instance.
     *
     * @param lexer the lexical phase result object.
     */
    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    /**
     * Attempts to match all of the next tokens to a {@link AstScript} object.
     *
     * @return the parsed {@link AstScript} object.
     */
    public AstScript script() {
        pushRange();
        // ------------------ the signature parsing ------------------//
        consume(LBRACKET);
        var trigger = identifier();
        consume(COMMA);
        var name = identifier();
        consume(RBRACKET);
        // ------------------ the code parsing ------------------//
        var statements = new ArrayList<AstStatement>();
        // keep parsing as long as we have a statement next, this allows
        // the file to contain multiple scripts, and not just only one.
        while (isStatement()) {
            statements.add(statement());
        }
        // return the parsed script.
        return new AstScript(popRange(), trigger, name, statements.toArray(new AstStatement[0]));
    }

    /**
     * Attempts to parse a simple expression rule.
     *
     * @return the parsed rule as a {@link AstExpression} object.
     */
    public AstExpression simpleExpression() {
        switch (peekKind()) {
            case INTEGER:
                return integerNumber();
            case LONG:
                return longNumber();
            case STRING:
                return string();
            case CONCATB:
                return concatString();
            case BOOL:
                return bool();
            case IDENTIFIER:
                return identifier();
            case DOLLAR:
                return localVariable();
            case MODULO:
                return globalVariable();
            case CARET:
                return constant();
            case LPAREN:
                return parExpression();
            default:
                throw createError(consume(), "Expecting an expression");
        }
    }

    /**
     * Checks whether or not the next token is a valid expression start.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public boolean isExpression() {
        var kind = peekKind();
        return kind == INTEGER || kind == LONG || kind == STRING || kind == CONCATB || kind == BOOL || kind == IDENTIFIER || kind == DOLLAR || kind == MODULO || kind == CARET;
    }

    /**
     * Attempts to parse an {@link AstExpression} that is surrounded with
     * parenthesis. The return value is equal to calling {@link #simpleExpression()}
     * method, the only difference in this method that it checks for parenthesis
     * before and after the expression and consume them.
     *
     * @return the parsed {@link AstExpression} object.
     */
    public AstExpression parExpression() {
        consume(LPAREN);
        var expression = simpleExpression();
        consume(RPAREN);
        return expression;
    }

    /**
     * Attempts to match the next token set to any valid {@link AstStatement} types.
     *
     * @return the matched {@link AstStatement} type object instance.
     */
    public AstStatement statement() {
        var kind = peekKind();
        switch (kind) {
            case IF:
                return ifStatement();
            case WHILE:
                return whileStatement();
            case LBRACE:
                return blockStatement();
            case RETURN:
                return returnStatement();
            default:
                throw createError(consume(), "Expecting a statement");
        }
    }

    /**
     * Checks whether or not the next token is a valid statement start.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    private boolean isStatement() {
        var kind = peekKind();
        return kind == IF || kind == WHILE || kind == LBRACE || kind == RETURN;
    }

    /**
     * Attempts to match the next token set to an if-statement rule.
     *
     * @return the matched {@link AstIfStatement} type object instance.
     */
    public AstIfStatement ifStatement() {
        pushRange();
        consume(IF);
        var expression = parExpression();
        var trueStatement = statement();
        var falseStatement = consumeIf(ELSE) ? statement() : null;
        return new AstIfStatement(popRange(), expression, trueStatement, falseStatement);
    }

    /**
     * Attempts to match the next token set to a while-statement rule.
     *
     * @return the matched {@link AstWhileStatement} type object instance.
     */
    public AstWhileStatement whileStatement() {
        pushRange();
        consume(WHILE);
        var expression = parExpression();
        var statement = statement();
        return new AstWhileStatement(popRange(), expression, statement);
    }

    /**
     * Attempts to match the next token set to an block-statement rule.
     *
     * @return the matched {@link AstBlockStatement} type object instance.
     */
    public AstBlockStatement blockStatement() {
        pushRange();
        consume(LBRACE);
        var statements = statementsList();
        consume(RBRACE);
        return new AstBlockStatement(popRange(), statements);
    }

    /**
     * Attempts to parse all of the next sequential code-statements.
     *
     * @return the parsed code-statements as {@link AstBlockStatement} object.
     */
    public AstBlockStatement unbracedBlockStatement() {
        pushRange();
        var statements = statementsList();
        return new AstBlockStatement(popRange(), statements);
    }

    /**
     * Parses a list of sequential code statements.
     *
     * @return the parsed code-statements as {@link AstStatement} array object.
     */
    private AstStatement[] statementsList() {
        var list = new ArrayList<AstStatement>();
        while (isStatement()) {
            list.add(statement());
        }
        return list.toArray(new AstStatement[0]);
    }

    /**
     * Attempts to match the next token set to a return-statement rule.
     *
     * @return the matched {@link AstReturnStatement} type object instance.
     */
    private AstReturnStatement returnStatement() {
        pushRange();
        var expr = simpleExpression();
        return new AstReturnStatement(popRange(), expr);
    }

    /**
     * Attempts to match the next token to an {@link AstInteger} object instance.
     *
     * @return the parsed {@link AstInteger} object.
     */
    public AstInteger integerNumber() {
        pushRange();
        var token = consume(INTEGER);
        try {
            return new AstInteger(popRange(), Integer.parseInt(token.getLexeme()));
        } catch (NumberFormatException e) {
            throw createError(token, "The literal " + token.getLexeme() + " of type int is out of range");
        }
    }

    /**
     * Attempts to match the next token to an {@link AstLong} object instance.
     *
     * @return the parsed {@link AstLong} object.
     */
    public AstLong longNumber() {
        pushRange();
        var token = consume(LONG);
        try {
            return new AstLong(popRange(), Long.parseLong(token.getLexeme()));
        } catch (NumberFormatException e) {
            throw createError(token, "The literal " + token.getLexeme() + " of type long is out of range");
        }
    }

    /**
     * Attempts to match the next token to an {@link AstString} object.
     *
     * @return the parsed {@link AstString} object.
     */
    public AstString string() {
        pushRange();
        var token = consume(STRING);
        return new AstString(popRange(), token.getLexeme());
    }

    /**
     * Attempts to match the next token set to an {@link AstStringConcat} node.
     *
     * @return the parsed {@link AstStringConcat} object.
     */
    public AstStringConcat concatString() {
        pushRange();
        consume(CONCATB);
        var expressions = new ArrayList<AstExpression>();
        while (isExpression()) {
            expressions.add(simpleExpression());
        }
        consume(CONCATE);
        return new AstStringConcat(popRange(), expressions.toArray(new AstExpression[0]));
    }

    /**
     * Attempts to the match the next token to an {@link AstBool} object.
     *
     * @return the parsed {@link AstBool} object.
     */
    public AstBool bool() {
        pushRange();
        var token = consume(BOOL);
        return new AstBool(popRange(), Boolean.parseBoolean(token.getLexeme()));
    }

    /**
     * Attempts to match the next set of tokens to an {@link AstIdentifier} object.
     *
     * @return the parsed {@link AstIdentifier} object.
     */
    public AstIdentifier identifier() {
        pushRange();
        var text = consume(IDENTIFIER);
        return new AstIdentifier(popRange(), text.getLexeme());
    }

    /**
     * Attempts to match the next set of tokens to an {@link AstLocalVariable} object.
     *
     * @return the parsed {@link AstLocalVariable} object.
     */
    public AstLocalVariable localVariable() {
        pushRange();
        consume(DOLLAR);
        var name = identifier();
        return new AstLocalVariable(popRange(), name);
    }

    /**
     * Attempts to match the next set of tokens to an {@link AstGlobalVariable} object.
     *
     * @return the parsed {@link AstGlobalVariable} object.
     */
    public AstGlobalVariable globalVariable() {
        pushRange();
        consume(MODULO);
        var name = identifier();
        return new AstGlobalVariable(popRange(), name);
    }

    /**
     * Attempts to match the next set of tokens to an {@link AstConstant} object.
     *
     * @return the parsed {@link AstConstant} object.
     */
    public AstConstant constant() {
        pushRange();
        consume(CARET);
        var name = identifier();
        return new AstConstant(popRange(), name);
    }

    /**
     * Takes the next {@link Token} object and checks whether or not it's
     * {@linkplain Kind kind} matches the specified {@linkplain Kind kind}.
     *
     * @param expected the expected token kind.
     * @return the expected {@link Token} object.
     * @throws SyntaxError if the next token does not match the expected token.
     */
    public Token consume(Kind expected) {
        var token = consume();
        var kind = token == null ? Kind.EOF : token.getKind();
        if (kind != expected) {
            throwError(token, "Unexpected rule: " + kind + ", expected: " + expected);
        }
        return token;
    }

    /**
     * Takes the next {@link Token} object and checks whether or not it's
     * {@linkplain Kind kind} matches the specified {@linkplain Kind kind}.
     *
     * @param expected the expected token kind.
     * @return <code>true</code> if the token
     * @throws SyntaxError if the next token does not match the expected token.
     */
    public boolean consumeIf(Kind expected) {
        var token = peek();
        var kind = token == null ? Kind.EOF : token.getKind();
        if (kind == expected) {
            consume();
            return true;
        }
        return false;
    }

    /**
     * Takes the next {@link Token} object from the lexer.
     *
     * @return the next {@link Token} object or {@code null}.
     * @see Lexer#take()
     */
    public Token consume() {
        var token = lexer.take();
        appendRange(token);
        return token;
    }

    /**
     * Takes the next {@link Token} object from the lexer and return it's kind if it
     * was present or {@link Kind#EOF}.
     *
     * @return the token {@link Kind} or {@link Kind#EOF} if it was not present.
     */
    public Kind kind() {
        var token = consume();
        if (token == null) {
            return Kind.EOF;
        }
        return token.getKind();
    }

    /**
     * Takes the next {@link Token} object without advancing the lexer cursor.
     *
     * @return the next {@link Token} object or {@code null}.
     * @see Lexer#peek()
     */
    public Token peek() {
        return lexer.peek();
    }

    /**
     * Gets the next token {@link Kind} from the lexer without advancing the lexer
     * cursor.
     *
     * @return the next {@link Kind} or {@link Kind#EOF} if there is no more tokens.
     */
    public Kind peekKind() {
        var token = peek();
        if (token == null) {
            return Kind.EOF;
        }
        return token.getKind();
    }

    /**
     * Pushes a new {@link Range} into the {@link #ranges} stack. Calls to this
     * method should be followed by {@link #popRange()} to remove the pushed
     * {@link Range} object from the stack.
     */
    private void pushRange() {
        ranges.push(new Range());
    }

    /**
     * Appends the specified {@link Element} range into the last {@link Range} in
     * the {@link #ranges} stack. If the element is null or there is no
     * {@link Range} object available into the stack, the method will have no
     * effect.
     *
     * @param element the element to append it's range.
     */
    private void appendRange(Element element) {
        if (ranges.isEmpty() || element == null) {
            return;
        }
        ranges.lastElement().add(element.getRange());
    }

    /**
     * Pops the last pushed {@link Range} object from the stack. If the stack is
     * empty.
     *
     * @return the popped {@link Range} object.
     */
    private Range popRange() {
        var range = ranges.pop();
        if (!ranges.isEmpty()) {
            ranges.lastElement().add(range);
        }
        return range;
    }

    /**
     * Throws a syntax error indicating a mismatched grammar rule.
     *
     * @param token   the token which the error has occurred at.
     * @param message the error message describing why the error has occurred.
     */
    private void throwError(Token token, String message) {
        throw createError(token, message);
    }

    /**
     * Creates a syntax error indicating a mismatched grammar rule.
     *
     * @param token   the token which the error has occurred at.
     * @param message the error message describing why the error has occurred
     * @return the created {@link SyntaxError} object.
     */
    private SyntaxError createError(Token token, String message) {
        return new SyntaxError(token, message);
    }
}

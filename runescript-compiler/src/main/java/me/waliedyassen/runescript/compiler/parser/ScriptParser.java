/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.parser;

import me.waliedyassen.runescript.compiler.ast.AstAnnotation;
import me.waliedyassen.runescript.compiler.ast.AstParameter;
import me.waliedyassen.runescript.compiler.ast.AstScript;
import me.waliedyassen.runescript.compiler.ast.expr.*;
import me.waliedyassen.runescript.compiler.ast.expr.literal.AstLiteralBool;
import me.waliedyassen.runescript.compiler.ast.expr.literal.AstLiteralInteger;
import me.waliedyassen.runescript.compiler.ast.expr.literal.AstLiteralLong;
import me.waliedyassen.runescript.compiler.ast.expr.literal.AstLiteralString;
import me.waliedyassen.runescript.compiler.ast.stmt.*;
import me.waliedyassen.runescript.compiler.ast.stmt.conditional.AstIfStatement;
import me.waliedyassen.runescript.compiler.ast.stmt.conditional.AstWhileStatement;
import me.waliedyassen.runescript.compiler.env.CompilerEnvironment;
import me.waliedyassen.runescript.compiler.lexer.Lexer;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.type.ArrayReference;
import me.waliedyassen.runescript.compiler.util.Operator;
import me.waliedyassen.runescript.compiler.util.VariableScope;
import me.waliedyassen.runescript.lexer.token.Token;
import me.waliedyassen.runescript.parser.ParserBase;
import me.waliedyassen.runescript.type.PrimitiveType;
import me.waliedyassen.runescript.type.TupleType;
import me.waliedyassen.runescript.type.Type;

import java.util.ArrayList;
import java.util.List;

import static me.waliedyassen.runescript.compiler.lexer.token.Kind.*;

/**
 * Represents the scripts grammar Abstract-Syntax-Tree parser.
 *
 * @author Walied K. Yassen
 */
public final class ScriptParser extends ParserBase<Kind> {

    // TODO: Detailed documentation

    /**
     * The environment of the owner compiler.
     */
    private final CompilerEnvironment environment;

    /**
     * Constructs a new {@link ScriptParser} type object instance.
     *
     * @param environment
     *         the environment of the compiler.
     * @param lexer
     *         the lexical parser to use for tokens.
     */
    public ScriptParser(CompilerEnvironment environment, Lexer lexer) {
        super(lexer, Kind.EOF);
        this.environment = environment;
    }

    /**
     * Attempts to match all of the next tokens to a {@link AstScript} object.
     *
     * @return the parsed {@link AstScript} object.
     */
    public AstScript script() {
        pushRange();
        // parse annotations if we have any.
        var annotations = annotationList();
        // parse the script trigger and name.
        consume(LBRACKET);
        var trigger = identifier();
        consume(COMMA);
        var name = identifier();
        consume(RBRACKET);
        // parse the script return ype nad parameters list.
        Type type = PrimitiveType.VOID;
        var parameters = new ArrayList<AstParameter>();
        var has_returntype = false;
        if (consumeIf(LPAREN)) {
            if (consumeIf(RPAREN)) {
                // we have a ()
            } else {
                if (isParameter()) {
                    parameters.addAll(parametersList());
                } else {
                    type = type();
                    has_returntype = true;
                }
                consume(RPAREN);
            }
        }
        if (consumeIf(LPAREN)) {
            if (consumeIf(RPAREN)) {
                // we have a ()
            } else {
                if (has_returntype) {
                    parameters.addAll(parametersList());
                } else {
                    type = type();
                }
                consume(RPAREN);
            }
        }
        // we will allow empty scripts for now.
        var code = unbracedBlockStatement();
        // return the parsed script.
        return new AstScript(popRange(), annotations, trigger, name, parameters.toArray(AstParameter[]::new), type, code);
    }

    /**
     * Attempts to parse an {@link AstAnnotation} object node.
     *
     * @return the parsed {@link AstAnnotation} object.
     */
    private AstAnnotation annotation() {
        pushRange();
        consume(HASH);
        consume(LBRACKET);
        var name = identifier();
        consume(COLON);
        var value = integerNumber();
        consume(RBRACKET);
        return new AstAnnotation(popRange(), name, value);
    }

    /**
     * Attempts to parse a list of {@link AstParameter} objects.
     *
     * @return the parsed list of {@link AstParameter} objects.
     */
    public ArrayList<AstAnnotation> annotationList() {
        var annotations = new ArrayList<AstAnnotation>();
        while (isAnnotation()) {
            annotations.add(annotation());
        }
        return annotations;
    }

    /**
     * Checks whether or not the next tokens can be parsed as an {@link AstAnnotation} object.
     *
     * @return <code>true</code> if it can otherwise <code>false</code>.
     */
    private boolean isAnnotation() {
        return peekKind(0) == HASH;
    }

    /**
     * Attempts to parse an {@link AstParameter} object node.
     *
     * @return the parsed {@link AstParameter} object.
     * @see #parameter(int)
     */
    public AstParameter parameter() {
        return parameter(0);
    }

    /**
     * Attempts to parse an {@link AstParameter} object node.
     *
     * @param index
     *         the current index of the parameter used for array references.
     *
     * @return the parsed {@link AstParameter} object.
     */
    public AstParameter parameter(int index) {
        pushRange();
        var array = peekKind() == ARRAY_TYPE;
        var type = array ? arrayType() : primitiveType();
        if (!type.isDeclarable()) {
            throwError(lexer.previous(), "Illegal type: " + type.getRepresentation());
        }
        consume(DOLLAR);
        var name = identifier();
        return new AstParameter(popRange(), array ? new ArrayReference(type, index) : type, name);
    }

    /**
     * Attempts to parse a list of {@link AstParameter} separated with a {@link Kind#COMMA} token {@link Kind kind}.
     *
     * @return the parsed list of {@link AstParameter} objects.
     */
    private List<AstParameter> parametersList() {
        var parameters = new ArrayList<AstParameter>();
        do {
            parameters.add(parameter(parameters.size()));
        } while (consumeIf(COMMA));
        return parameters;
    }

    /**
     * Checks whether or not the next tokens can be parsed as a {@link AstParameter} object.
     *
     * @return <code>true</code> if it can otherwise <code>false</code>.
     */
    private boolean isParameter() {
        return (peekKind(0) == ARRAY_TYPE || peekKind(0) == TYPE) && peekKind(1) == DOLLAR;
    }

    /**
     * Attempts to a parse an {@link AstExpression} object node.
     *
     * @return the parsed {@link AstExpression} object.
     */
    public AstExpression expression() {
        return expression(0);
    }

    /**
     * Attemps to parse a {@link AstExpression} tree with given lowest precedence allowed.
     *
     * @param precedence
     *         the lowest precedence that to be allowed in this tree.
     *
     * @return the parsed tree as a {@link AstExpression} object.
     */
    private AstExpression expression(int precedence) {
        var tree = simpleExpression();
        while (true) {
            var op = Operator.lookup(peekKind());
            if (op == null) {
                return tree;
            }
            if (precedence < op.getPrecedence()) {
                consume();
                var right = expression(op.getPrecedence());
                tree = new AstBinaryOperation(tree, op, right);
            } else if (precedence == op.getPrecedence()) {
                switch (op.getAssociativity()) {
                    case RIGHT:
                        consume();
                        var right = expression(precedence);
                        return new AstBinaryOperation(tree, op, right);
                    case LEFT:
                        return tree;
                }
            } else {
                return tree;
            }
        }
    }


    /**
     * Attempts to parse a simple expression rule.
     *
     * @return the parsed rule as a {@link AstExpression} object.
     */
    public AstExpression simpleExpression() {
        switch (peekKind()) {
            case LPAREN:
                return parExpression();
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
            case DOLLAR:
                if (peekKind(2) == LPAREN) {
                    return arrayVariable();
                }
                return localVariable();
            case MOD:
                return globalVariable();
            case CARET:
                return constant();
            case IDENTIFIER:
                if (peekKind(1) == LPAREN) {
                    return command();
                }
                return dynamic();
            case DOT:
                return command();
            case CALC:
                return calc();
            default:
                if (isCall()) {
                    return call();
                } else {
                    throw createError(consume(), "Expecting an expression");
                }
        }
    }

    /**
     * Checks whether or not the next token is a valid expression start.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public boolean isExpression() {
        var kind = peekKind();
        return kind == INTEGER || kind == LONG || kind == STRING || kind == CONCATB || kind == BOOL || kind == IDENTIFIER || kind == DOLLAR || kind == MOD || kind == CARET || kind == LPAREN || kind == DOT || kind == CALC || isCall();
    }

    /**
     * Checks whether or not the next token is a valid trigger call start.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    private boolean isCall() {
        return environment.lookupTrigger(peekKind()) != null;
    }

    /**
     * Attempts to parse an {@link AstExpression} that is surrounded with parenthesis. The return value is equal to
     * calling {@link #expression()} method, the only difference in this method that it checks for parenthesis before
     * and after the expression and consumes them.
     *
     * @return the parsed {@link AstExpression} object.
     */
    public AstExpression parExpression() {
        consume(LPAREN);
        var expression = expression();
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
            case DEFINE:
                if (peekKind(3) == LPAREN) {
                    return arrayDeclaration();
                }
                return variableDeclaration();
            case DOLLAR:
                if (peekKind(2) == LPAREN) {
                    return arrayInitializer();
                }
                return variableInitializer();
            case MOD:
                return variableInitializer();
            case SWITCH:
                return switchStatement();
            default:
                if (isExpression()) {
                    return expressionStatement();
                } else {
                    throw createError(consume(), "Expecting a statement");
                }
        }
    }

    /**
     * Checks whether or not the next token is a valid statement start.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    private boolean isStatement() {
        var kind = peekKind();
        // TODO: We can check for an EQUAL sign after the DOLLAR (kind == DOLLAR && peekKind(1) == EQUAL) to avoid errors.
        return kind == IF || kind == WHILE || kind == LBRACE || kind == RETURN || kind == DEFINE || kind == DOLLAR || kind == MOD || kind == SWITCH || isExpression();
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
     * Attempts to match the next token set to an {@link AstBlockStatement} but without requiring it to be surrounded to
     * braces.
     *
     * @return the parsed {@link AstBlockStatement} object.
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
    public AstReturnStatement returnStatement() {
        pushRange();
        consume(RETURN);
        var exprs = new ArrayList<AstExpression>();
        if (isExpression()) {
            do {
                exprs.add(expression());
            } while (consumeIf(COMMA));
        }
        consume(SEMICOLON);
        return new AstReturnStatement(popRange(), exprs.toArray(AstExpression[]::new));
    }

    /**
     * Attempts to parse an {@link AstVariableDeclaration} from the next set of {@link Token token}s.
     *
     * @return the parsed {@link AstVariableDeclaration} object.
     */
    public AstVariableDeclaration variableDeclaration() {
        pushRange();
        var token = consume(DEFINE);
        var type = PrimitiveType.forRepresentation(token.getLexeme().substring(4));
        if (!consumeIf(DOLLAR)) {
            throw createError(consume(), "Expecting a local variable name");
        }
        var name = identifier();
        AstExpression expression;
        if (consumeIf(EQUALS)) {
            expression = expression();
        } else {
            expression = null;
        }
        consume(SEMICOLON);
        return new AstVariableDeclaration(popRange(), type, name, expression);
    }

    /**
     * Attempts to parse an {@link AstVariableDeclaration} from the next set of {@link Token token}s.
     *
     * @return the parsed {@link AstVariableDeclaration} object.
     */
    public AstArrayDeclaration arrayDeclaration() {
        pushRange();
        var token = consume(DEFINE);
        var type = PrimitiveType.forRepresentation(token.getLexeme().substring(4));
        if (!consumeIf(DOLLAR)) {
            throw createError(consume(), "Expecting an array name");
        }
        var name = identifier();
        var size = parExpression();
        consume(SEMICOLON);
        return new AstArrayDeclaration(popRange(), type, name, size);
    }

    /**
     * Attempts to parse an {@link AstVariableInitializer} from the next set of {@link Token token}s.
     *
     * @return the parsed {@link AstVariableInitializer} object.
     */
    public AstVariableInitializer variableInitializer() {
        pushRange();
        var token = consume();
        var scope = VariableScope.forKind(token.getKind());
        if (scope == null) {
            throw createError(token, "Expecting a variable");
        }
        var variable = identifier();
        consume(EQUALS);
        var expression = expression();
        consume(SEMICOLON);
        return new AstVariableInitializer(popRange(), scope, variable, expression);
    }

    /**
     * Attempts to parse an {@link AstArrayInitializer} from the next set of {@link Token token}s.
     *
     * @return the parsed {@link AstArrayInitializer} object.
     */
    public AstArrayInitializer arrayInitializer() {
        pushRange();
        consume(DOLLAR);
        var name = identifier();
        var index = parExpression();
        consume(EQUALS);
        var value = expression();
        consume(SEMICOLON);
        return new AstArrayInitializer(popRange(), name, index, value);
    }

    /**
     * Attempts to parse an {@link AstSwitchStatement} from the next set of {@link Token token}s.
     *
     * @return the parsed {@link AstSwitchStatement} object.
     */
    public AstSwitchStatement switchStatement() {
        pushRange();
        var token = consume(SWITCH);
        var type = PrimitiveType.forRepresentation(token.getLexeme().substring(7));
        var condition = parExpression();
        var cases = new ArrayList<AstSwitchCase>();
        var defaultCase = (AstSwitchCase) null;
        consume(LBRACE);
        while (!consumeIf(RBRACE)) {
            var _case = switchCase();
            if (_case.isDefault()) {
                if (defaultCase != null) {
                    throw createError(_case.getRange(), "Switch statements can only have one default case defined");
                }
                defaultCase = _case;
            } else {
                cases.add(_case);
            }
        }
        return new AstSwitchStatement(popRange(), type, condition, cases.toArray(AstSwitchCase[]::new), defaultCase);
    }

    /**
     * Attempts to parse an {@link AstSwitchCase} from the next set of {@link Token token}s.
     *
     * @return the parsed {@link AstSwitchCase} object.
     */
    public AstSwitchCase switchCase() {
        pushRange();
        consume(CASE);
        var keys = new ArrayList<AstExpression>();
        if (!consumeIf(DEFAULT)) {
            do {
                keys.add(expression());
            } while (consumeIf(COMMA));
        }
        consume(COLON);
        var block = unbracedBlockStatement();
        return new AstSwitchCase(popRange(), keys.toArray(AstExpression[]::new), block);
    }


    /**
     * Attempts to match the next set of token(s) to an {@link AstExpressionStatement}.
     *
     * @return the parsed {@link AstExpressionStatement} object.
     */
    private AstExpressionStatement expressionStatement() {
        pushRange();
        var expr = expression();
        consume(SEMICOLON);
        return new AstExpressionStatement(popRange(), expr);
    }

    /**
     * Attempts to match the next token to an {@link AstLiteralInteger} object instance.
     *
     * @return the parsed {@link AstLiteralInteger} object.
     */
    public AstLiteralInteger integerNumber() {
        pushRange();
        var token = consume(INTEGER);
        try {
            return new AstLiteralInteger(popRange(), Integer.parseInt(token.getLexeme()));
        } catch (NumberFormatException e) {
            throw createError(token, "The literal " + token.getLexeme() + " of type int is out of range");
        }
    }

    /**
     * Attempts to match the next token to an {@link AstLiteralLong} object instance.
     *
     * @return the parsed {@link AstLiteralLong} object.
     */
    public AstLiteralLong longNumber() {
        pushRange();
        var token = consume(LONG);
        try {
            return new AstLiteralLong(popRange(), Long.parseLong(token.getLexeme()));
        } catch (NumberFormatException e) {
            throw createError(token, "The literal " + token.getLexeme() + " of type long is out of range");
        }
    }

    /**
     * Attempts to match the next token to an {@link AstLiteralString} object.
     *
     * @return the parsed {@link AstLiteralString} object.
     */
    public AstLiteralString string() {
        pushRange();
        var token = consume(STRING);
        return new AstLiteralString(popRange(), token.getLexeme());
    }

    /**
     * Attempts to match the next token set to an {@link AstConcatenation} node.
     *
     * @return the parsed {@link AstConcatenation} object.
     */
    public AstConcatenation concatString() {
        pushRange();
        consume(CONCATB);
        var expressions = new ArrayList<AstExpression>();
        while (isExpression()) {
            expressions.add(expression());
        }
        consume(CONCATE);
        return new AstConcatenation(popRange(), expressions.toArray(new AstExpression[0]));
    }

    /**
     * Attempts to the match the next token to an {@link AstLiteralBool} object.
     *
     * @return the parsed {@link AstLiteralBool} object.
     */
    public AstLiteralBool bool() {
        pushRange();
        var token = consume(BOOL);
        return new AstLiteralBool(popRange(), Boolean.parseBoolean(token.getLexeme()));
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
     * Attempts to match the next set of tokens to an {@link AstVariableExpression} object with a variable scope of
     * {@link VariableScope#LOCAL}.
     *
     * @return the parsed {@link AstVariableExpression} object.
     */
    public AstVariableExpression localVariable() {
        pushRange();
        consume(DOLLAR);
        var name = identifier();
        return new AstVariableExpression(popRange(), VariableScope.LOCAL, name);
    }

    /**
     * Attempts to match the next set of tokens to an {@link AstArrayExpression} object.
     *
     * @return the parsed {@link AstArrayExpression} object.
     */
    public AstArrayExpression arrayVariable() {
        pushRange();
        consume(DOLLAR);
        var name = identifier();
        consume(LPAREN);
        var index = expression();
        consume(RPAREN);
        return new AstArrayExpression(popRange(), name, index);
    }

    /**
     * Attempts to match the next set of tokens to an {@link AstVariableExpression} object with a variable scope of
     * {@link VariableScope#GLOBAL}.
     *
     * @return the parsed {@link AstVariableExpression} object.
     */
    public AstVariableExpression globalVariable() {
        pushRange();
        consume(MOD);
        var name = identifier();
        return new AstVariableExpression(popRange(), VariableScope.GLOBAL, name);
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
     * Attempts to parse an {@link AstCall} from the next set of tokens.
     *
     * @return the parsed {@link AstCall} object.
     */
    public AstCall call() {
        pushRange();
        var operator = consume();
        var triggerType = environment.lookupTrigger(operator.getKind());
        if (triggerType == null) {
            throw createError(consume(), "Expecting an script call operator");
        }
        var name = identifier();
        var arguments = new ArrayList<AstExpression>();
        if (consumeIf(LPAREN)) {
            do {
                arguments.add(expression());
            } while (consumeIf(COMMA));
            consume(RPAREN);
        }
        return new AstCall(popRange(), triggerType, name, arguments.toArray(AstExpression[]::new));
    }

    /**
     * Attempts to parse an {@link AstDynamic} from the next set of tokens.
     *
     * @return the parsed {@link AstDynamic} object.
     */
    public AstDynamic dynamic() {
        pushRange();
        var name = identifier();
        return new AstDynamic(popRange(), name);
    }

    /**
     * Attempts to parse an {@link AstCommand} from the next set of tokens.
     *
     * @return the parsed {@link AstCommand} object.
     */
    public AstCommand command() {
        pushRange();
        var alternative = consumeIf(DOT);
        var name = identifier();
        var arguments = new ArrayList<AstExpression>();
        if (consumeIf(LPAREN)) {
            if (isExpression()) {
                do {
                    arguments.add(expression());
                } while (consumeIf(COMMA));
            }
            consume(RPAREN);
        }
        return new AstCommand(popRange(), name, arguments.toArray(AstExpression[]::new), alternative);
    }

    /**
     * Attempts to parse an {@link AstCalc} from the next set of tokens.
     *
     * @return the parsed {@link AstCalc} object.
     */
    public AstCalc calc() {
        pushRange();
        consume(CALC);
        var expr = expression();
        return new AstCalc(popRange(), expr);
    }

    /**
     * Attempts to match the next set of token(s) to a single {@link PrimitiveType} or to a set of {@link
     * PrimitiveType}s represented as a {@link TupleType}.
     *
     * @return the parsed {@link Type} object.
     */
    public Type type() {
        var types = new ArrayList<PrimitiveType>(1);
        do {
            types.add(primitiveType());
        } while (consumeIf(COMMA));
        if (types.size() == 1) {
            return types.get(0);
        } else {
            return new TupleType(types.toArray(PrimitiveType[]::new));
        }
    }

    /**
     * Attempts to parse a {@link PrimitiveType} from the next array type token.
     *
     * @return the parsed {@link PrimitiveType} enum constant.
     */
    public PrimitiveType arrayType() {
        var token = consume(ARRAY_TYPE);
        return PrimitiveType.forRepresentation(token.getLexeme().substring(0, token.getLexeme().length() - 5));
    }

    /**
     * Attempts to parse a {@link PrimitiveType} from the next type token.
     *
     * @return the parsed {@link PrimitiveType}.
     */
    public PrimitiveType primitiveType() {
        var token = consume(TYPE);
        return PrimitiveType.forRepresentation(token.getLexeme());
    }
}

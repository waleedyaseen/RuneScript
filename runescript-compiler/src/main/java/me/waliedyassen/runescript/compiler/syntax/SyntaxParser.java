/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax;

import lombok.NonNull;
import lombok.var;
import me.waliedyassen.runescript.commons.stream.BufferedCharStream;
import me.waliedyassen.runescript.compiler.env.CompilerEnvironment;
import me.waliedyassen.runescript.compiler.lexer.Lexer;
import me.waliedyassen.runescript.compiler.lexer.LexerBase;
import me.waliedyassen.runescript.compiler.lexer.LexicalError;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.lexer.token.Token;
import me.waliedyassen.runescript.compiler.lexer.tokenizer.Tokenizer;
import me.waliedyassen.runescript.compiler.parser.ParserBase;
import me.waliedyassen.runescript.compiler.parser.SyntaxError;
import me.waliedyassen.runescript.compiler.symbol.ScriptSymbolTable;
import me.waliedyassen.runescript.compiler.syntax.expr.*;
import me.waliedyassen.runescript.compiler.syntax.expr.literal.*;
import me.waliedyassen.runescript.compiler.syntax.expr.op.BinaryOperationSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.*;
import me.waliedyassen.runescript.compiler.syntax.stmt.conditional.IfStatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.loop.BreakStatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.loop.ContinueStatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.loop.WhileStatementSyntax;
import me.waliedyassen.runescript.compiler.type.ArrayReference;
import me.waliedyassen.runescript.compiler.util.Operator;
import me.waliedyassen.runescript.compiler.util.VariableScope;
import me.waliedyassen.runescript.type.PrimitiveType;
import me.waliedyassen.runescript.type.TupleType;
import me.waliedyassen.runescript.type.Type;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static me.waliedyassen.runescript.compiler.lexer.token.Kind.*;

/**
 * Represents the scripts grammar Abstract-Syntax-Tree parser.
 *
 * @author Walied K. Yassen
 */
public final class SyntaxParser extends ParserBase<Kind> {

    // TODO: Detailed documentation

    /**
     * The symbol table which we will use for checking hooks.
     */
    private final ScriptSymbolTable symbolTable;

    /**
     * The environment of the owner compiler.
     */
    private final CompilerEnvironment environment;

    /**
     * The scripts type that we are parsing.
     */
    private final String type;

    /**
     * Constructs a new {@link SyntaxParser} type object instance.
     *
     * @param environment the environment of the compiler.
     * @param symbolTable the symbol table to use for checking hooks.
     * @param lexer       the lexical parser to use for tokens.
     * @param type        the scripts type that we are parsing.
     */
    public SyntaxParser(@NonNull CompilerEnvironment environment, @NonNull ScriptSymbolTable symbolTable, @NonNull Lexer lexer, @NonNull String type) {
        super(lexer, Kind.EOF);
        this.environment = environment;
        this.symbolTable = symbolTable;
        this.type = type;
    }

    /**
     * Attempts to match all of the next tokens to a {@link ScriptSyntax} object.
     *
     * @return the parsed {@link ScriptSyntax} object.
     */
    public ScriptSyntax script() {
        pushRange();
        // parse annotations if we have any.
        var annotations = annotationList();
        // parse the script trigger and name.
        consume(LBRACKET);
        var trigger = advancedIdentifier();
        consume(COMMA);
        var name = scriptName();
        consume(RBRACKET);
        // parse the script return ype nad parameters list.
        Type type = PrimitiveType.VOID;
        var parameters = new ArrayList<ParameterSyntax>();
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
        return new ScriptSyntax(popRange(), this.type, annotations, trigger, name, parameters.toArray(new ParameterSyntax[0]), type, code);
    }

    /**
     * Attempts to parse a script name {@link ExpressionSyntax} object node.
     *
     * @return the parsed {@link ExpressionSyntax} object node.
     */
    private ExpressionSyntax scriptName() {
        if (isComponent()) {
            return component();
        }
        return advancedIdentifier();
    }


    /**
     * Attempts to parse an {@link AnnotationSyntax} object node.
     *
     * @return the parsed {@link AnnotationSyntax} object.
     */
    private AnnotationSyntax annotation() {
        pushRange();
        consume(HASH);
        var name = identifier();
        consume(COLON);
        var value = integerNumber();
        return new AnnotationSyntax(popRange(), name, value);
    }

    /**
     * Attempts to parse a list of {@link ParameterSyntax} objects.
     *
     * @return the parsed list of {@link ParameterSyntax} objects.
     */
    public ArrayList<AnnotationSyntax> annotationList() {
        var annotations = new ArrayList<AnnotationSyntax>();
        while (isAnnotation()) {
            annotations.add(annotation());
        }
        return annotations;
    }

    /**
     * Checks whether or not the next tokens can be parsed as an {@link AnnotationSyntax} object.
     *
     * @return <code>true</code> if it can otherwise <code>false</code>.
     */
    private boolean isAnnotation() {
        return peekKind(0) == HASH;
    }

    /**
     * Attempts to parse an {@link ParameterSyntax} object node.
     *
     * @return the parsed {@link ParameterSyntax} object.
     * @see #parameter(int)
     */
    public ParameterSyntax parameter() {
        return parameter(0);
    }

    /**
     * Attempts to parse an {@link ParameterSyntax} object node.
     *
     * @param index the current index of the parameter used for array references.
     * @return the parsed {@link ParameterSyntax} object.
     */
    public ParameterSyntax parameter(int index) {
        pushRange();
        var array = peekKind() == ARRAY_TYPE;
        var type = array ? arrayType() : primitiveType();
        if (!type.isDeclarable()) {
            throwError(lexer().previous(), "Illegal type: " + type.getRepresentation());
        }
        consume(DOLLAR);
        var name = identifier();
        return new ParameterSyntax(popRange(), array ? new ArrayReference(type, index) : type, name);
    }

    /**
     * Attempts to parse a list of {@link ParameterSyntax} separated with a {@link Kind#COMMA} token {@link Kind kind}.
     *
     * @return the parsed list of {@link ParameterSyntax} objects.
     */
    private List<ParameterSyntax> parametersList() {
        var parameters = new ArrayList<ParameterSyntax>();
        do {
            parameters.add(parameter(parameters.size()));
        } while (consumeIf(COMMA));
        return parameters;
    }

    /**
     * Checks whether or not the next tokens can be parsed as a {@link ParameterSyntax} object.
     *
     * @return <code>true</code> if it can otherwise <code>false</code>.
     */
    private boolean isParameter() {
        return (peekKind(0) == ARRAY_TYPE || peekKind(0) == TYPE) && peekKind(1) == DOLLAR;
    }

    /**
     * Attempts to a parse an {@link ExpressionSyntax} object node.
     *
     * @return the parsed {@link ExpressionSyntax} object.
     */
    public ExpressionSyntax expression() {
        return expression(0);
    }

    /**
     * Attemps to parse a {@link ExpressionSyntax} tree with given lowest precedence allowed.
     *
     * @param precedence the lowest precedence that to be allowed in this tree.
     * @return the parsed tree as a {@link ExpressionSyntax} object.
     */
    private ExpressionSyntax expression(int precedence) {
        var tree = simpleExpression();
        while (true) {
            var op = Operator.lookup(peekKind());
            if (op == null) {
                return tree;
            }
            if (precedence < op.getPrecedence()) {
                consume();
                var right = expression(op.getPrecedence());
                tree = new BinaryOperationSyntax(tree, op, right);
            } else if (precedence == op.getPrecedence()) {
                switch (op.getAssociativity()) {
                    case RIGHT:
                        consume();
                        var right = expression(precedence);
                        return new BinaryOperationSyntax(tree, op, right);
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
     * @return the parsed rule as a {@link ExpressionSyntax} object.
     */
    public ExpressionSyntax simpleExpression() {
        switch (peekKind()) {
            case LPAREN:
                return parExpression();
            case INTEGER:
                return integerNumber();
            case COORDGRID:
                return coordgrid();
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
            case DOT:
                return command();
            case CALC:
                return calc();
            default:
                if (isAdvancedIdentifier()) {
                    if (peekKind(1) == LPAREN) {
                        return command();
                    } else if (isComponent()) {
                        return component();
                    }
                    return dynamic();
                } else if (isCall()) {
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
        return kind == INTEGER || kind == LONG || kind == STRING || kind == CONCATB || kind == BOOL || kind == IDENTIFIER || kind == DOLLAR || kind == MOD || kind == CARET || kind == LPAREN || kind == DOT || kind == CALC || kind == NULL || isCall() || isComponent();
    }

    /**
     * Checks whether or not the next token set matches a component expression.
     *
     * @return <code>true</code> if it does otherwise <code>false</code.>
     */
    public boolean isComponent() {
        return isAdvancedIdentifier() && peekKind(1) == COLON && (peekKind(2) == IDENTIFIER || peekKind(2) == INTEGER);
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
     * Attempts to parse an {@link ExpressionSyntax} that is surrounded with parenthesis. The return value is equal to
     * calling {@link #expression()} method, the only difference in this method that it checks for parenthesis before
     * and after the expression and consumes them.
     *
     * @return the parsed {@link ExpressionSyntax} object.
     */
    public ExpressionSyntax parExpression() {
        consume(LPAREN);
        var expression = expression();
        consume(RPAREN);
        return expression;
    }

    /**
     * Attempts to match the next token set to any valid {@link StatementSyntax} types.
     *
     * @return the matched {@link StatementSyntax} type object instance.
     */
    public StatementSyntax statement() {
        var kind = peekKind();
        switch (kind) {
            case IF:
                return ifStatement();
            case WHILE:
                return whileStatement();
            case CONTINUE:
                return continueStatement();
            case BREAK:
                return breakStatement();
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
        return kind == IF || kind == WHILE || kind == LBRACE || kind == RETURN || kind == DEFINE || kind == DOLLAR
                || kind == MOD || kind == SWITCH || kind == CONTINUE || kind == BREAK || isExpression();
    }

    /**
     * Attempts to match the next token set to an if-statement rule.
     *
     * @return the matched {@link IfStatementSyntax} type object instance.
     */
    public IfStatementSyntax ifStatement() {
        pushRange();
        consume(IF);
        var expression = parExpression();
        var trueStatement = statement();
        var falseStatement = consumeIf(ELSE) ? statement() : null;
        return new IfStatementSyntax(popRange(), expression, trueStatement, falseStatement);
    }

    /**
     * Attempts to match the next token set to a while-statement rule.
     *
     * @return the matched {@link WhileStatementSyntax} type object instance.
     */
    public WhileStatementSyntax whileStatement() {
        pushRange();
        consume(WHILE);
        var expression = parExpression();
        var statement = statement();
        return new WhileStatementSyntax(popRange(), expression, statement);
    }

    /**
     * Attempts to match the next token set to a while-statement rule.
     *
     * @return the matched {@link ContinueStatementSyntax} type object instance.
     */
    public ContinueStatementSyntax continueStatement() {
        pushRange();
        var token = consume(CONTINUE);
        consume(SEMICOLON);
        return new ContinueStatementSyntax(popRange(), token);
    }

    /**
     * Attempts to match the next token set to an block-statement rule.
     *
     * @return the matched {@link BreakStatementSyntax} type object instance.
     */
    public BreakStatementSyntax breakStatement() {
        pushRange();
        var token = consume(BREAK);
        consume(SEMICOLON);
        return new BreakStatementSyntax(popRange(), token);
    }

    /**
     * Attempts to match the next token set to an block-statement rule.
     *
     * @return the matched {@link BlockStatementSyntax} type object instance.
     */
    public BlockStatementSyntax blockStatement() {
        pushRange();
        consume(LBRACE);
        var statements = statementsList();
        consume(RBRACE);
        return new BlockStatementSyntax(popRange(), statements);
    }

    /**
     * Attempts to match the next token set to an {@link BlockStatementSyntax} but without requiring it to be surrounded to
     * braces.
     *
     * @return the parsed {@link BlockStatementSyntax} object.
     */
    public BlockStatementSyntax unbracedBlockStatement() {
        pushRange();
        var statements = statementsList();
        return new BlockStatementSyntax(popRange(), statements);
    }

    /**
     * Parses a list of sequential code statements.
     *
     * @return the parsed code-statements as {@link StatementSyntax} array object.
     */
    private StatementSyntax[] statementsList() {
        var list = new ArrayList<StatementSyntax>();
        while (isStatement()) {
            list.add(statement());
        }
        return list.toArray(new StatementSyntax[0]);
    }

    /**
     * Attempts to match the next token set to a return-statement rule.
     *
     * @return the matched {@link ReturnStatementSyntax} type object instance.
     */
    public ReturnStatementSyntax returnStatement() {
        pushRange();
        consume(RETURN);
        var expressions = new ArrayList<ExpressionSyntax>();
        if (consumeIf(LPAREN)) {
            if (isExpression()) {
                do {
                    expressions.add(expression());
                } while (consumeIf(COMMA));
            }
            consume(RPAREN);
        }
        consume(SEMICOLON);
        return new ReturnStatementSyntax(popRange(), expressions.toArray(new ExpressionSyntax[0]));
    }

    /**
     * Attempts to parse an {@link VariableDeclarationSyntax} from the next set of {@link Token token}s.
     *
     * @return the parsed {@link VariableDeclarationSyntax} object.
     */
    public VariableDeclarationSyntax variableDeclaration() {
        pushRange();
        var token = consume(DEFINE);
        var type = PrimitiveType.forRepresentation(token.getLexeme().substring(4));
        if (!consumeIf(DOLLAR)) {
            throw createError(consume(), "Expecting a local variable name");
        }
        var name = identifier();
        ExpressionSyntax expression;
        if (consumeIf(EQUALS)) {
            expression = consumeIf(NULL) ? null : expression();
        } else {
            expression = null;
        }
        consume(SEMICOLON);
        return new VariableDeclarationSyntax(popRange(), type, name, expression);
    }

    /**
     * Attempts to parse an {@link VariableDeclarationSyntax} from the next set of {@link Token token}s.
     *
     * @return the parsed {@link VariableDeclarationSyntax} object.
     */
    public ArrayDeclarationSyntax arrayDeclaration() {
        pushRange();
        var token = consume(DEFINE);
        var type = PrimitiveType.forRepresentation(token.getLexeme().substring(4));
        if (!consumeIf(DOLLAR)) {
            throw createError(consume(), "Expecting an array name");
        }
        var name = identifier();
        var size = parExpression();
        consume(SEMICOLON);
        return new ArrayDeclarationSyntax(popRange(), type, name, size);
    }

    /**
     * Attempts to parse an {@link VariableInitializerSyntax} from the next set of {@link Token token}s.
     *
     * @return the parsed {@link VariableInitializerSyntax} object.
     */
    public VariableInitializerSyntax variableInitializer() {
        pushRange();
        var variables = new ArrayList<VariableSyntax>();
        do {
            variables.add(variable());
        } while (consumeIf(COMMA));
        consume(EQUALS);
        var expressions = new ArrayList<ExpressionSyntax>();
        do {
            expressions.add(expression());
        } while (consumeIf(COMMA));
        consume(SEMICOLON);
        return new VariableInitializerSyntax(popRange(), variables.toArray(new VariableSyntax[0]), expressions.toArray(new ExpressionSyntax[0]));
    }

    /**
     * Attempts to parse an {@link VariableSyntax} from the next set of {@link Token token}s.
     *
     * @return the parsed {@link VariableSyntax} object.
     */
    private VariableSyntax variable() {
        pushRange();
        var token = consume();
        var scope = VariableScope.forKind(token.getKind());
        if (scope == null) {
            throw createError(token, "Expecting a variable");
        }
        var name = identifier();
        if (consumeIf(LPAREN)) {
            if (scope != VariableScope.LOCAL) {
                throw createError(token, "Unrecognised scope for array variable expression");
            }
            var expression = expression();
            consume(RPAREN);
            return new ArrayVariableSyntax(popRange(), name, expression);
        } else {
            return new ScopedVariableSyntax(popRange(), scope, name);
        }
    }

    /**
     * Attempts to parse an {@link SwitchStatementSyntax} from the next set of {@link Token token}s.
     *
     * @return the parsed {@link SwitchStatementSyntax} object.
     */
    public SwitchStatementSyntax switchStatement() {
        pushRange();
        var token = consume(SWITCH);
        var type = PrimitiveType.forRepresentation(token.getLexeme().substring(7));
        var condition = parExpression();
        var cases = new ArrayList<SwitchCaseSyntax>();
        var defaultCase = (SwitchCaseSyntax) null;
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
        return new SwitchStatementSyntax(popRange(), type, condition, cases.toArray(new SwitchCaseSyntax[0]), defaultCase);
    }

    /**
     * Attempts to parse an {@link SwitchCaseSyntax} from the next set of {@link Token token}s.
     *
     * @return the parsed {@link SwitchCaseSyntax} object.
     */
    public SwitchCaseSyntax switchCase() {
        pushRange();
        consume(CASE);
        var keys = new ArrayList<ExpressionSyntax>();
        if (!consumeIf(DEFAULT)) {
            do {
                keys.add(expression());
            } while (consumeIf(COMMA));
        }
        consume(COLON);
        var block = unbracedBlockStatement();
        return new SwitchCaseSyntax(popRange(), keys.toArray(new ExpressionSyntax[0]), block);
    }


    /**
     * Attempts to match the next set of token(s) to an {@link ExpressionStatementSyntax}.
     *
     * @return the parsed {@link ExpressionStatementSyntax} object.
     */
    private ExpressionStatementSyntax expressionStatement() {
        pushRange();
        var expr = expression();
        consume(SEMICOLON);
        return new ExpressionStatementSyntax(popRange(), expr);
    }

    /**
     * Attempts to match the next token to an {@link LiteralIntegerSyntax} object instance.
     *
     * @return the parsed {@link LiteralIntegerSyntax} object.
     */
    public LiteralIntegerSyntax integerNumber() {
        pushRange();
        var token = consume(INTEGER);
        try {
            var radix = 10;
            var text = token.getLexeme();
            if (text.startsWith("0x")) {
                text = text.substring(2);
                radix = 16;
            }
            return new LiteralIntegerSyntax(popRange(), Integer.parseInt(text, radix));
        } catch (NumberFormatException e) {
            throw createError(token, "The literal " + token.getLexeme() + " of type int is out of range");
        }
    }

    /**
     * Attempts to match the next token to an {@link LiteralCoordgridSyntax} object instance.
     *
     * @return the parsed {@link LiteralCoordgridSyntax} object.
     */
    public LiteralCoordgridSyntax coordgrid() {
        pushRange();
        var token = consume(COORDGRID);
        var parts = token.getLexeme().split("_");
        if (parts.length != 5) {
            throw createError(popRange(), "Expected 5 components for literal of type coordgrid");
        }
        var parsed = new int[parts.length];
        for (var index = 0; index < parts.length; index++) {
            try {
                parsed[index] = Integer.parseInt(parts[index]);
            } catch (NumberFormatException e) {
                throw createError(token, "The literal " + token.getLexeme() + " of type coordgrid is out of range");
            }
        }
        if (parsed[0] < 0 || parsed[0] > 3) {
            throw createError(token, "Expected the level component value to be between [0-3] inclusively");
        }
        if (parsed[1] < 0 || parsed[1] > 127) {
            throw createError(token, "Expected the square-x component value to be between [0-127] inclusively");
        }
        if (parsed[2] < 0 || parsed[2] > 255) {
            throw createError(token, "Expected the square-y component value to be between [0-255] inclusively");
        }
        if (parsed[3] < 0 || parsed[3] > 63) {
            throw createError(token, "Expected the tile-x component value to be between [0-63] inclusively");
        }
        if (parsed[4] < 0 || parsed[4] > 63) {
            throw createError(token, "Expected the tile-y component value to be between [0-63] inclusively");
        }
        var packed = parsed[0] << 28 | parsed[1] << 20 | parsed[2] << 14 | parsed[3] << 6 | parsed[4];
        return new LiteralCoordgridSyntax(popRange(), packed);
    }

    /**
     * Attempts to match the next token to an {@link LiteralLongSyntax} object instance.
     *
     * @return the parsed {@link LiteralLongSyntax} object.
     */
    public LiteralLongSyntax longNumber() {
        pushRange();
        var token = consume(LONG);
        try {
            var radix = 10;
            var text = token.getLexeme();
            if (text.startsWith("0x")) {
                text = text.substring(2);
                radix = 16;
            }
            return new LiteralLongSyntax(popRange(), Long.parseLong(text, radix));
        } catch (NumberFormatException e) {
            throw createError(token, "The literal " + token.getLexeme() + " of type long is out of range");
        }
    }

    /**
     * Attempts to match the next token to an {@link LiteralStringSyntax} object.
     *
     * @return the parsed {@link LiteralStringSyntax} object.
     */
    public LiteralStringSyntax string() {
        pushRange();
        var token = consume(STRING);
        return new LiteralStringSyntax(popRange(), token.getLexeme());
    }

    /**
     * Attempts to match the next token set to an {@link ConcatenationSyntax} node.
     *
     * @return the parsed {@link ConcatenationSyntax} object.
     */
    public ConcatenationSyntax concatString() {
        pushRange();
        consume(CONCATB);
        var expressions = new ArrayList<ExpressionSyntax>();
        while (isExpression()) {
            expressions.add(expression());
        }
        consume(CONCATE);
        return new ConcatenationSyntax(popRange(), expressions.toArray(new ExpressionSyntax[0]));
    }

    /**
     * Attempts to the match the next token to an {@link LiteralBooleanSyntax} object.
     *
     * @return the parsed {@link LiteralBooleanSyntax} object.
     */
    public LiteralBooleanSyntax bool() {
        pushRange();
        var token = consume(BOOL);
        return new LiteralBooleanSyntax(popRange(), Boolean.parseBoolean(token.getLexeme()));
    }

    /**
     * Attempts to match the next set of tokens to an {@link IdentifierSyntax} object.
     *
     * @return the parsed {@link IdentifierSyntax} object.
     */
    public IdentifierSyntax identifier() {
        pushRange();
        var text = consume(IDENTIFIER);
        return new IdentifierSyntax(popRange(), text.getLexeme());
    }

    /**
     * Attempts to match the next set of tokens to an {@link IdentifierSyntax} object. This method
     * will also attempt to match any keyword to number to an identifier.
     *
     * @return the parsed {@link IdentifierSyntax} object.
     */
    private IdentifierSyntax advancedIdentifier() {
        // TODO: Maybe checked advanced identifier based on symbol table names.
        pushRange();
        String text;
        var kind = peekKind();
        switch (kind) {
            case IF:
            case ELSE:
            case WHILE:
            case RETURN:
            case SWITCH:
            case CASE:
            case DEFAULT:
            case CALC:
            case IDENTIFIER:
            case BOOL:
            case INTEGER:
            case LONG:
            case TYPE:
                text = consume().getLexeme();
                break;
            default:
                throw createError(popRange(), "Expected an identifier but got: " + kind());
        }
        return new IdentifierSyntax(popRange(), text);
    }

    /**
     * Checks whether or not the current token can be parsed as an advanced identifier expression.
     *
     * @return <copde>true</copde> if it does otherwise <code>false</code>.
     */
    private boolean isAdvancedIdentifier() {
        var kind = peekKind();
        return kind == IF || kind == ELSE || kind == WHILE || kind == RETURN || kind == SWITCH || kind == CASE || kind == DEFAULT || kind == CALC || kind == IDENTIFIER || kind == BOOL || kind == INTEGER || kind == LONG || kind == TYPE;
    }

    /**
     * Attempts to match the next set of tokens to an {@link VariableExpressionSyntax} object with a variable scope of
     * {@link VariableScope#LOCAL}.
     *
     * @return the parsed {@link VariableExpressionSyntax} object.
     */
    public VariableExpressionSyntax localVariable() {
        pushRange();
        consume(DOLLAR);
        var name = identifier();
        return new VariableExpressionSyntax(popRange(), VariableScope.LOCAL, name);
    }

    /**
     * Attempts to match the next set of tokens to an {@link ArrayElementSyntax} object.
     *
     * @return the parsed {@link ArrayElementSyntax} object.
     */
    public ArrayElementSyntax arrayVariable() {
        pushRange();
        consume(DOLLAR);
        var name = identifier();
        consume(LPAREN);
        var index = expression();
        consume(RPAREN);
        return new ArrayElementSyntax(popRange(), name, index);
    }

    /**
     * Attempts to match the next set of tokens to an {@link VariableExpressionSyntax} object with a variable scope of
     * {@link VariableScope#GLOBAL}.
     *
     * @return the parsed {@link VariableExpressionSyntax} object.
     */
    public VariableExpressionSyntax globalVariable() {
        pushRange();
        consume(MOD);
        var name = identifier();
        return new VariableExpressionSyntax(popRange(), VariableScope.GLOBAL, name);
    }

    /**
     * Attempts to match the next set of tokens to an {@link ConstantSyntax} object.
     *
     * @return the parsed {@link ConstantSyntax} object.
     */
    public ConstantSyntax constant() {
        pushRange();
        consume(CARET);
        var name = identifier();
        return new ConstantSyntax(popRange(), name);
    }

    /**
     * Attempts to parse an {@link CallSyntax} from the next set of tokens.
     *
     * @return the parsed {@link CallSyntax} object.
     */
    public CallSyntax call() {
        pushRange();
        var operator = consume();
        var triggerType = environment.lookupTrigger(operator.getKind());
        if (triggerType == null) {
            throw createError(consume(), "Expecting an script call operator");
        }
        var name = advancedIdentifier();
        var arguments = new ArrayList<ExpressionSyntax>();
        if (consumeIf(LPAREN)) {
            do {
                arguments.add(expression());
            } while (consumeIf(COMMA));
            consume(RPAREN);
        }
        return new CallSyntax(popRange(), triggerType, name, arguments.toArray(new ExpressionSyntax[0]));
    }

    /**
     * Attempts to parse an {@link DynamicSyntax} from the next set of tokens.
     *
     * @return the parsed {@link DynamicSyntax} object.
     */
    public DynamicSyntax dynamic() {
        pushRange();
        var name = advancedIdentifier();
        return new DynamicSyntax(popRange(), name);
    }

    /**
     * Attempts to parse an {@link CommandSyntax} from the next set of tokens.
     *
     * @return the parsed {@link CommandSyntax} object.
     */
    public CommandSyntax command() {
        pushRange();
        var alternative = consumeIf(DOT);
        var name = advancedIdentifier();
        var arguments = new ArrayList<ExpressionSyntax>();
        if (consumeIf(LPAREN)) {
            if (isExpression()) {
                int index = 0;
                do {
                    if (isHookParameter(name.getText(), index)) {
                        arguments.add(hook());
                    } else {
                        arguments.add(expression());
                    }
                    index++;
                } while (consumeIf(COMMA));
            }
            consume(RPAREN);
        }
        return new CommandSyntax(popRange(), name, arguments.toArray(new ExpressionSyntax[0]), alternative);
    }

    /**
     * Checks whether or not a hook should be parsed as an argument of the command with the specified
     * {@code name} and at the specified  {@code index}.
     *
     * @param name  the name of the command which the argument is in.
     * @param index the index which the argument is located at in thecommand.
     * @return <code>true</code> if a hook should be parsed otherwise <code>false</code>.
     */
    private boolean isHookParameter(String name, int index) {
        var type = symbolTable.lookupCommand(name);
        if (type == null) {
            return false;
        }
        if (!type.isHook()) {
            return false;
        }
        return type.getArguments()[index] == PrimitiveType.HOOK && (peekKind() == STRING || peekKind() == NULL);
    }

    /**
     * Attempts to match the next set of token(s) to an {@link ComponentSyntax}.
     *
     * @return the parsed {@link ComponentSyntax} object.
     */
    public ComponentSyntax component() {
        // TODO: Switch to advancedIdentifier
        pushRange();
        var parent = identifier();
        consume(COLON);
        ExpressionSyntax component;
        switch (peekKind()) {
            case IDENTIFIER:
                component = identifier();
                break;
            case INTEGER:
                component = integerNumber();
                break;
            default:
                throw createError(popRange(), "Expected a component name or id");
        }
        return new ComponentSyntax(popRange(), parent, component);
    }

    /**
     * Attempts to match the next set of token(s) to an {@link HookSyntax}.
     *
     * @return the parsed {@link HookSyntax} object.
     */
    private HookSyntax hook() {
        pushRange();
        if (consumeIf(NULL)) {
            return new HookSyntax(popRange(), null, null, null);
        } else {
            var rawString = consume(STRING);
            try {
                pushLexer(createLexerFromString(rawString));
            } catch (SyntaxError | LexicalError e) {
                throwError(rawString, e.getMessage());
            }
            var name = identifier();
            var arguments = new ArrayList<ExpressionSyntax>();
            var transmits = new ArrayList<ExpressionSyntax>();
            if (consumeIf(LPAREN)) {
                if (isExpression()) {
                    do {
                        arguments.add(expression());
                    } while (consumeIf(COMMA));
                }
                consume(RPAREN);
                if (consumeIf(LBRACE)) {
                    do {
                        transmits.add(expression());
                    } while (consumeIf(COMMA));
                    consume(RBRACE);
                }
            }
            popLexer();
            return new HookSyntax(popRange(), name, arguments.toArray(new ExpressionSyntax[0]), transmits.toArray(new ExpressionSyntax[0]));
        }
    }

    /**
     * Attempts to create a sub-lexer from the content of the specified {@link Token}.
     *
     * @param token the token which the content will be taken from.
     * @return the created {@link LexerBase} object.
     */
    private LexerBase<Kind> createLexerFromString(Token<Kind> token) {
        try {
            var lexeme = token.getLexeme().getBytes();
            var line = token.getRange().getStart().getLine();
            var column = token.getRange().getStart().getColumn();
            var stream = new BufferedCharStream(new ByteArrayInputStream(lexeme), line, column);
            var tokenizer = new Tokenizer(((Lexer) lexer()).getLexicalTable(), stream);
            return new Lexer(tokenizer);
        } catch (IOException e) {
            throw new IllegalStateException("Should not happen");
        }
    }

    /**
     * Attempts to parse an {@link CalcSyntax} from the next set of tokens.
     *
     * @return the parsed {@link CalcSyntax} object.
     */
    public CalcSyntax calc() {
        pushRange();
        consume(CALC);
        var expr = expression();
        return new CalcSyntax(popRange(), expr);
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
            return new TupleType(types.toArray(new PrimitiveType[0]));
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

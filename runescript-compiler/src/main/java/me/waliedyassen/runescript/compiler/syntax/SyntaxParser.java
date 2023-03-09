/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.syntax;

import lombok.NonNull;
import me.waliedyassen.runescript.commons.stream.BufferedCharStream;
import me.waliedyassen.runescript.compiler.env.CompilerEnvironment;
import me.waliedyassen.runescript.compiler.error.ErrorReporter;
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
import me.waliedyassen.runescript.compiler.syntax.stmt.loop.DoWhileStatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.loop.WhileStatementSyntax;
import me.waliedyassen.runescript.compiler.util.Operator;
import me.waliedyassen.runescript.compiler.util.VariableScope;
import me.waliedyassen.runescript.type.Type;
import me.waliedyassen.runescript.type.primitive.PrimitiveType;
import me.waliedyassen.runescript.type.tuple.TupleType;

import java.util.ArrayList;
import java.util.List;

import static me.waliedyassen.runescript.compiler.lexer.token.Kind.*;

/**
 * Represents the scripts grammar Abstract-Syntax-Tree parser.
 *
 * @author Walied K. Yassen
 */
public final class SyntaxParser extends ParserBase<Kind, SyntaxToken> {

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
    private boolean inCalc;

    /**
     * Constructs a new {@link SyntaxParser} type object instance.
     *
     * @param environment   the environment of the compiler.
     * @param symbolTable   the symbol table to use for checking hooks.
     * @param errorReporter the error reporter we will use for reporting back errors.
     * @param lexer         the lexical parser to use for tokens.
     * @param type          the scripts type that we are parsing.
     */
    public SyntaxParser(@NonNull CompilerEnvironment environment,
                        @NonNull ScriptSymbolTable symbolTable,
                        @NonNull ErrorReporter errorReporter,
                        @NonNull Lexer lexer,
                        @NonNull String type) {
        super(errorReporter, lexer, Kind.EOF);
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
        var name = scriptName();
        // parse the script return ype nad parameters list.
        Type type = TupleType.EMPTY;
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
        var node = new ScriptSyntax(popRange(), this.type, annotations, name, parameters.toArray(new ParameterSyntax[0]), code);
        node.setType(type); // TODO: Store raw types
        return node;
    }

    /**
     * Attempts to parse a script name {@link ScriptNameSyntax} object node.
     *
     * @return the parsed {@link ScriptNameSyntax} object node.
     */
    private ScriptNameSyntax scriptName() {
        pushRange();
        var leftBracket = consume(LBRACKET);
        var trigger = advancedIdentifier();
        var name = (IdentifierSyntax) null;
        var comma = (SyntaxToken) null;
        if (peekKind() == COMMA) {
            comma = consume(COMMA);
            name = advancedIdentifier();
        }
        var rightBracket = consume(RBRACKET);
        return new ScriptNameSyntax(popRange(), leftBracket, comma, rightBracket, trigger, name);
    }

    /**
     * Attempts to parse an {@link AnnotationSyntax} object node.
     *
     * @return the parsed {@link AnnotationSyntax} object.
     */
    private AnnotationSyntax annotation() {
        pushRange();
        var hashToken = consume(HASH);
        var name = identifier();
        var colonToken = consume(COLON);
        var value = literalInteger();
        return new AnnotationSyntax(popRange(), hashToken, colonToken, name, value);
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
        var typeToken = consume(peekKind() == ARRAY_TYPE ? ARRAY_TYPE : TYPE);
        var dollarToken = consume(DOLLAR);
        var name = advancedIdentifier();
        return new ParameterSyntax(popRange(), dollarToken, typeToken, name, index);
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
            if (inCalc) {
                if (op == Operator.LOGICAL_AND) op = Operator.BITWISE_AND;
                if (op == Operator.LOGICAL_OR) op = Operator.BITWISE_OR;
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
                return literalInteger();
            case STRING:
                return literalString();
            case LONG:
                return literalLong();
            case BOOL:
                return literalBool();
            case COORDGRID:
                return literalCoord();
            case NULL:
                return literalNull();
            case TYPE:
                if (peekKind(1) == LPAREN) {
                    return command();
                }
                return literalType();
            case CONCATB:
                return concatString();
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
                    }
                    return dynamic();
                } else if (isCall()) {
                    return call();
                } else {
                    throw createError(consume(), "Expecting an expression");
                }
        }
    }

    private boolean isSimpleExpression() {
        switch (peekKind()) {
            case LPAREN:
            case INTEGER:
            case STRING:
            case LONG:
            case BOOL:
            case COORDGRID:
            case NULL:
            case TYPE:
            case CONCATB:
            case DOLLAR:
            case MOD:
            case CARET:
            case DOT:
            case CALC:
                return true;
            default:
                return isAdvancedIdentifier() || isCall();
        }
    }

    /**
     * Attempts to match the next token set to any valid {@link StatementSyntax} types.
     *
     * @return the matched {@link StatementSyntax} type object instance.
     */
    public ParExpressionSyntax parExpression() {
        pushRange();
        var leftParenToken = consume(LPAREN);
        var expression = expression();
        var rightParenToken = consume(RPAREN);
        return new ParExpressionSyntax(popRange(), leftParenToken, rightParenToken, expression);
    }

    /**
     * Checks whether or not the next token is a valid expression start.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public boolean isExpression() {
        var kind = peekKind();
        return kind == INTEGER
                || kind == LONG
                || kind == STRING
                || kind == CONCATB
                || kind == BOOL
                || kind == COORDGRID
                || kind == IDENTIFIER
                || kind == DOLLAR
                || kind == MOD
                || kind == CARET
                || kind == LPAREN
                || kind == DOT
                || kind == CALC
                || kind == NULL
                || kind == TYPE
                || isCall();
    }

    /**
     * Checks whether or not the next token is a valid expression statement start.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public boolean isExpressionStatement() {
        var kind = peekKind();
        return kind == IDENTIFIER
                || kind == DOT
                || isCall();
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
            case DO:
                return doWhileStatement();
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
                if (isExpressionStatement()) {
                    return expressionStatement();
                } else {
                    errorReporter.addError(createError(peek(), "Expecting a statement"));
                    return errorStatement();
                }
        }
    }

    /**
     * Creates a new {@link ErrorStatementSyntax} object using the current parsing token.
     *
     * @return the created {@link ErrorStatementSyntax} object.
     */
    private ErrorStatementSyntax errorStatement() {
        pushRange();
        var token = consume();
        var semicolon = peekKind() == SEMICOLON ? consume(SEMICOLON) : null;
        return new ErrorStatementSyntax(popRange(), token, semicolon);
    }

    /**
     * Checks whether or not the next token is a valid statement start.
     *
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    private boolean isStatement() {
        var kind = peekKind();
        // TODO: We can check for an EQUAL sign after the DOLLAR (kind == DOLLAR && peekKind(1) == EQUAL) to avoid errors.
        return kind == IF || kind == WHILE || kind == DO || kind == LBRACE || kind == RETURN || kind == DEFINE || kind == DOLLAR
                || kind == MOD || kind == SWITCH || kind == CONTINUE || kind == BREAK || isExpression();
    }

    /**
     * Attempts to match the next token set to an if-statement rule.
     *
     * @return the matched {@link IfStatementSyntax} type object instance.
     */
    public IfStatementSyntax ifStatement() {
        pushRange();
        var ifKeyword = consume(IF);
        var leftParenToken = consume(LPAREN);
        var expression = expression();
        var rightParenToken = consume(RPAREN);
        var trueStatement = statement();
        if (peekKind() == ELSE) {
            var elseKeyword = consume(ELSE);
            var falseStatement = statement();
            return new IfStatementSyntax(popRange(), ifKeyword, leftParenToken, rightParenToken, elseKeyword, expression, trueStatement, falseStatement);
        } else {
            return new IfStatementSyntax(popRange(), ifKeyword, leftParenToken, rightParenToken, null, expression, trueStatement, null);
        }
    }

    /**
     * Attempts to match the next token set to a while-statement rule.
     *
     * @return the matched {@link WhileStatementSyntax} type object instance.
     */
    public WhileStatementSyntax whileStatement() {
        pushRange();
        var whileToken = consume(WHILE);
        var leftParenToken = consume(LPAREN);
        var expression = expression();
        var rightParenToken = consume(RPAREN);
        var statement = statement();
        return new WhileStatementSyntax(popRange(), whileToken, leftParenToken, rightParenToken, expression, statement);
    }

    /**
     * Attempts to match the next token set to a while-statement rule.
     *
     * @return the matched {@link DoWhileStatementSyntax} type object instance.
     */
    public DoWhileStatementSyntax doWhileStatement() {
        pushRange();
        var doToken = consume(DO);
        var code = blockStatement();
        var whileToken = consume(WHILE);
        var expression = parExpression();
        var semicolonToken = consume(SEMICOLON);
        return new DoWhileStatementSyntax(popRange(), doToken, whileToken, semicolonToken, code, expression);
    }

    /**
     * Attempts to match the next token set to a while-statement rule.
     *
     * @return the matched {@link ContinueStatementSyntax} type object instance.
     */
    public ContinueStatementSyntax continueStatement() {
        pushRange();
        var controlToken = consume(CONTINUE);
        var semicolonToken = consume(SEMICOLON);
        return new ContinueStatementSyntax(popRange(), controlToken, semicolonToken);
    }

    /**
     * Attempts to match the next token set to an block-statement rule.
     *
     * @return the matched {@link BreakStatementSyntax} type object instance.
     */
    public BreakStatementSyntax breakStatement() {
        pushRange();
        var controlToken = consume(BREAK);
        var semicolonToken = consume(SEMICOLON);
        return new BreakStatementSyntax(popRange(), controlToken, semicolonToken);
    }

    /**
     * Attempts to match the next token set to an block-statement rule.
     *
     * @return the matched {@link BlockStatementSyntax} type object instance.
     */
    public BlockStatementSyntax blockStatement() {
        pushRange();
        var leftBraceToken = consume(LBRACE);
        var statements = statementsList();
        var rightBraceToken = consume(RBRACE);
        return new BlockStatementSyntax(popRange(), leftBraceToken, rightBraceToken, statements);
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
        return new BlockStatementSyntax(popRange(), null, null, statements);
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
        var returnToken = consume(RETURN);
        var expressions = new ArrayList<ExpressionSyntax>();
        if (consumeIf(LPAREN)) {
            if (isExpression()) {
                do {
                    expressions.add(expression());
                } while (consumeIf(COMMA));
            }
            consume(RPAREN);
        }
        var semicolonToken = consume(SEMICOLON);
        return new ReturnStatementSyntax(popRange(), returnToken, semicolonToken, expressions.toArray(new ExpressionSyntax[0]));
    }

    /**
     * Attempts to parse an {@link VariableDeclarationSyntax} from the next set of {@link Token token}s.
     *
     * @return the parsed {@link VariableDeclarationSyntax} object.
     */
    public VariableDeclarationSyntax variableDeclaration() {
        pushRange();
        var defineToken = consume(DEFINE);
        var dollarSign = consume(DOLLAR);
        var name = advancedIdentifier();
        ExpressionSyntax expression;
        if (consumeIf(EQUALS)) {
            expression = consumeIf(NULL) ? null : expression();
        } else {
            expression = null;
        }
        consume(SEMICOLON);
        return new VariableDeclarationSyntax(popRange(), defineToken, dollarSign, name, expression);
    }

    /**
     * Attempts to parse an {@link VariableDeclarationSyntax} from the next set of {@link Token token}s.
     *
     * @return the parsed {@link VariableDeclarationSyntax} object.
     */
    public ArrayDeclarationSyntax arrayDeclaration() {
        pushRange();
        var defineToken = consume(DEFINE);
        var dollarToken = consume(DOLLAR);
        var name = identifier();
        var size = parExpression();
        var semicolonToken = consume(SEMICOLON);
        return new ArrayDeclarationSyntax(popRange(), defineToken, dollarToken, semicolonToken, name, size);
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
        var name = advancedIdentifier();
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
        var switchToken = consume(SWITCH);
        var condition = parExpression();
        var cases = new ArrayList<SwitchCaseSyntax>();
        var defaultCase = (SwitchCaseSyntax) null;
        consume(LBRACE);
        while (!consumeIf(RBRACE)) {
            var _case = switchCase();
            if (_case.isDefault()) {
                if (defaultCase != null) {
                    throw createError(_case.getSpan(), "Switch statements can only have one default case defined");
                }
                defaultCase = _case;
            } else {
                cases.add(_case);
            }
        }
        return new SwitchStatementSyntax(popRange(), switchToken, condition, cases.toArray(new SwitchCaseSyntax[0]), defaultCase);
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
        var semicolonToken = consume(SEMICOLON);
        return new ExpressionStatementSyntax(popRange(), semicolonToken, expr);
    }

    /**
     * Attempts to match the next token to an {@link LiteralIntegerSyntax} object instance.
     *
     * @return the parsed {@link LiteralIntegerSyntax} object.
     */
    public LiteralIntegerSyntax literalInteger() {
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
    public LiteralCoordgridSyntax literalCoord() {
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
    public LiteralLongSyntax literalLong() {
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
    public LiteralStringSyntax literalString() {
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
        while (isSimpleExpression()) {
            expressions.add(simpleExpression());
        }
        consume(CONCATE);
        return new ConcatenationSyntax(popRange(), expressions.toArray(new ExpressionSyntax[0]));
    }

    /**
     * Attempts to the match the next token to an {@link LiteralBooleanSyntax} object.
     *
     * @return the parsed {@link LiteralBooleanSyntax} object.
     */
    public LiteralBooleanSyntax literalBool() {
        pushRange();
        var token = consume(BOOL);
        return new LiteralBooleanSyntax(popRange(), Boolean.parseBoolean(token.getLexeme()));
    }

    /**
     * Attempts to match the next token to an {@link LiteralNullSyntax} object instance.
     *
     * @return the parsed {@link LiteralNullSyntax} object.
     */
    public LiteralNullSyntax literalNull() {
        pushRange();
        var wordToken = consume(NULL);
        return new LiteralNullSyntax(popRange(), wordToken);
    }

    /**
     * Attempts to match the next token to an {@link LiteralTypeSyntax} object instance.
     *
     * @return the parsed {@link LiteralTypeSyntax} object.
     */
    public LiteralTypeSyntax literalType() {
        pushRange();
        var type = consume(TYPE);
        return new LiteralTypeSyntax(popRange(), PrimitiveType.forRepresentation(type.getLexeme()));
    }

    /**
     * Attempts to match the next set of tokens to an {@link IdentifierSyntax} object.
     *
     * @return the parsed {@link IdentifierSyntax} object.
     */
    public IdentifierSyntax identifier() {
        pushRange();
        var text = consume(IDENTIFIER);
        return new IdentifierSyntax(popRange(), text);
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
        SyntaxToken token;
        var kind = peekKind();
        switch (kind) {
            case IF:
            case ELSE:
            case DO:
            case WHILE:
            case RETURN:
            case SWITCH:
            case CASE:
            case DEFAULT:
            case CALC:
            case IDENTIFIER:
            case BOOL:
            case TYPE:
                token = consume();
                break;
            default:
                throw createError(popRange(), "Expected an identifier but got: " + kind());
        }
        return new IdentifierSyntax(popRange(), token);
    }

    /**
     * Checks whether or not the current token can be parsed as an advanced identifier expression.
     *
     * @return <copde>true</copde> if it does otherwise <code>false</code>.
     */
    private boolean isAdvancedIdentifier() {
        var kind = peekKind();
        return kind == IF || kind == ELSE || kind == DO || kind == WHILE || kind == RETURN || kind == SWITCH || kind == CASE || kind == DEFAULT || kind == CALC || kind == IDENTIFIER || kind == BOOL || kind == INTEGER || kind == LONG || kind == TYPE;
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
        var name = advancedIdentifier();
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
        var caretToken = consume(CARET);
        var name = advancedIdentifier();
        return new ConstantSyntax(popRange(), caretToken, name);
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
        if (index >= type.getArguments().length) {
            return false;
        }
        return type.getArguments()[index] == PrimitiveType.HOOK.INSTANCE && (peekKind() == STRING || peekKind() == NULL);
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
                addError(rawString, e.getMessage());
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
            }
            if (consumeIf(LBRACE)) {
                if (isExpression()) {
                    do {
                        transmits.add(expression());
                    } while (consumeIf(COMMA));
                }
                consume(RBRACE);
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
    private LexerBase<Kind, SyntaxToken> createLexerFromString(SyntaxToken token) {
        var stream = new BufferedCharStream(token.getLexeme().toCharArray());
        var tokenizer = new Tokenizer(errorReporter, ((Lexer) lexer()).getLexicalTable(), stream, token.getSpan().getBegin() + 1);
        return new Lexer(tokenizer);
    }

    /**
     * Attempts to parse an {@link CalcSyntax} from the next set of tokens.
     *
     * @return the parsed {@link CalcSyntax} object.
     */
    public CalcSyntax calc() {
        var wasInCalc = inCalc;
        pushRange();
        consume(CALC);
        inCalc = true;
        var expr = parExpression();
        inCalc = wasInCalc;
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

    public boolean hasMore() {
        return lexer().remaining() > 0;
    }
}

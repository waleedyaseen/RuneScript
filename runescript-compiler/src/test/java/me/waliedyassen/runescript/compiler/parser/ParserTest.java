/*
 * Copyright (c) 2018 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.parser;

import me.waliedyassen.runescript.commons.stream.BufferedCharStream;
import me.waliedyassen.runescript.compiler.ast.AstParameter;
import me.waliedyassen.runescript.compiler.ast.expr.AstBinaryExpression;
import me.waliedyassen.runescript.compiler.ast.expr.AstIdentifier;
import me.waliedyassen.runescript.compiler.ast.expr.AstConstant;
import me.waliedyassen.runescript.compiler.ast.expr.AstVariable;
import me.waliedyassen.runescript.compiler.ast.literal.*;
import me.waliedyassen.runescript.compiler.ast.stmt.AstBlockStatement;
import me.waliedyassen.runescript.compiler.ast.stmt.AstVariableInitialize;
import me.waliedyassen.runescript.compiler.ast.stmt.conditional.AstIfStatement;
import me.waliedyassen.runescript.compiler.ast.stmt.conditional.AstWhileStatement;
import me.waliedyassen.runescript.compiler.lexer.Lexer;
import me.waliedyassen.runescript.compiler.lexer.table.LexicalTable;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.lexer.tokenizer.Tokenizer;
import me.waliedyassen.runescript.compiler.type.PrimitiveType;
import me.waliedyassen.runescript.compiler.util.Operator;
import me.waliedyassen.runescript.compiler.util.VariableScope;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringBufferInputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Walied K. Yassen
 */
@SuppressWarnings("deprecation")
final class ParserTest {

    @Test
    void testScript() {
        assertAll("script", () -> {
            var script = fromResource("parse-script.rs2").script();
            assertEquals(script.getCode().length, 2);
            assertTrue(script.getCode()[0] instanceof AstIfStatement);
            assertTrue(script.getCode()[1] instanceof AstIfStatement);
            // TODO: Bad scripts testing.
        }, () -> {
            // script with three parameters
            assertEquals(fromString("[trigger,name](int $one, int $two, string $three)").script().getParameters().length, 3);
        }, () -> {
            // script with missing parameter
            assertThrows(SyntaxError.class, () -> fromString("[trigger,name](int $one,").script());
        }, () -> {
            // script with unclosed right parenthesis for parameters
            assertThrows(SyntaxError.class, () -> fromString("[trigger,name](int $one").script());
        });
    }

    @Test
    void testParameter() {
        assertAll("parameter", () -> {
            // valid parameter
            var parameter = fromString("int $int0").parameter();
            assertTrue(parameter instanceof AstParameter);
            assertEquals(parameter.getType(), PrimitiveType.INT);
            assertEquals(parameter.getName().getText(), "int0");
        }, () -> {
            // invalid parameter name
            assertThrows(SyntaxError.class, () -> fromString("int 0123").parameter());
            assertThrows(SyntaxError.class, () -> fromString("int $0123").parameter());
        }, () -> {
            // invalid parameter type
            assertThrows(SyntaxError.class, () -> fromString("strin $param").parameter());
            assertThrows(SyntaxError.class, () -> fromString("int0 $param").parameter());
        }, () -> {
            // illegal parameter type.
            assertThrows(SyntaxError.class, () -> fromString("void $param").parameter());
        });
    }

    @Test
    void testSimpleExpression() {
        assertAll("simple expression", () -> {
            // string
            assertTrue(fromString("\"myString\"").simpleExpression() instanceof AstString);
        }, () -> {
            // interpolated string.
            assertTrue(fromString("\"my interpolated string <br>\"").simpleExpression() instanceof AstStringConcat);
        }, () -> {
            // integer
            assertTrue(fromString("123456").simpleExpression() instanceof AstInteger);
        }, () -> {
            // long
            assertTrue(fromString("123456L").simpleExpression() instanceof AstLong);
        }, () -> {
            // bool.
            assertTrue(fromString("true").simpleExpression() instanceof AstBool);
        }, () -> {
            // identifier
            assertTrue(fromString("test").simpleExpression() instanceof AstIdentifier);
        }, () -> {
            // local variable.
            assertTrue(fromString("$local_var").simpleExpression() instanceof AstVariable);
        }, () -> {
            // local variable.
            assertTrue(fromString("%global_var").simpleExpression() instanceof AstVariable);
        }, () -> {
            // constant.
            assertTrue(fromString("^constant").simpleExpression() instanceof AstConstant);
        }, () -> {
            // empty
            assertThrows(SyntaxError.class, () -> fromString("").simpleExpression());
        }, () -> {
            // expression in parenthesis.
            assertTrue(fromString("(^constant)").simpleExpression() instanceof AstConstant);
        }, () -> {
            // not expression
            assertThrows(SyntaxError.class, () -> fromString("if(123);").simpleExpression());
        });
    }

    @Test
    void testExpression() {
        assertAll("expression", () -> {
            // a basic binary operator.
            var expr = fromString("5 > 3").expression();
            assertTrue(expr instanceof AstBinaryExpression);
            var bin = (AstBinaryExpression) expr;
            assertEquals(bin.getOperator(), Operator.GREATER_THAN);
        }, () -> {
            // a bit more complex binary operator.
            var expr = fromString("5 > 3 ! true").expression();
            assertTrue(expr instanceof AstBinaryExpression);
            var bin = (AstBinaryExpression) expr;
            assertTrue(bin.getLeft() instanceof AstBinaryExpression);
            assertEquals(bin.getOperator(), Operator.NOT_EQUAL);
        });
    }

    @Test
    void testParExpression() {
        assertAll("par expression", () -> {
            // valid expression
            assertTrue(fromString("(1234)").parExpression() instanceof AstInteger);
        }, () -> {
            // invalid expression 1
            assertThrows(SyntaxError.class, () -> fromString("(1234").parExpression());
        }, () -> {
            // invalid expression 2
            assertThrows(SyntaxError.class, () -> fromString("1234)").parExpression());
        });
    }

    @Test
    void testStatement() {
        assertAll("statement", () -> {
            // valid if statement
            assertTrue(fromString("if(1234){}").statement() instanceof AstIfStatement);
        }, () -> {
            // valid block statement
            assertTrue(fromString("{}").statement() instanceof AstBlockStatement);
        }, () -> {
            // empty statement
            assertThrows(SyntaxError.class, () -> fromString("").statement());
        }, () -> {
            // invalid statement
            assertThrows(SyntaxError.class, () -> fromString("123456").statement());
        });
    }

    @Test
    void testIfStatement() {
        assertAll("if statement", () -> {
            // valid if statement
            assertTrue(fromString("if(1){}").ifStatement() instanceof AstIfStatement);
        }, () -> {
            // no code statement
            assertThrows(SyntaxError.class, () -> fromString("if(2)").ifStatement());
        }, () -> {
            // no condition expression
            assertThrows(SyntaxError.class, () -> fromString("if(){}").ifStatement());
        });
    }

    @Test
    void testIfElseStatement() {
        assertAll("if else statement", () -> {
            // valid if else statement.
            assertTrue(fromString("if(1) {} else if(2) {}").ifStatement().getFalseStatement() instanceof AstIfStatement);
        }, () -> {
            // missing false code statement.
            assertThrows(SyntaxError.class, () -> fromString("if (2) else").ifStatement());
        });
    }

    @Test
    void testWhileStatement() {
        assertAll("while statement", () -> {
            // valid while loop statement.
            assertTrue(fromString("while (true) {}").whileStatement() instanceof AstWhileStatement);
        }, () -> {
            // missing condition expression.
            assertThrows(SyntaxError.class, () -> fromString("while () {}").whileStatement());
        }, () -> {
            // missing code statement.
            assertThrows(SyntaxError.class, () -> fromString("while (true)").whileStatement());
        });
    }

    @Test
    void testBlockStatement() {
        assertAll("braced block", () -> {
            // valid block
            var blockStatement = fromString("{if(1234){}}").blockStatement();
            assertNotNull(blockStatement);
            assertEquals(blockStatement.getStatements().length, 1);
            assertTrue(blockStatement.getStatements()[0] instanceof AstIfStatement);
        }, () -> {
            // empty block
            assertNotNull(fromString("{}").blockStatement());
        }, () -> {

            // unclosed block
            assertThrows(SyntaxError.class, () -> fromString("{").blockStatement());
        });
    }

    @Test
    void testUnbracedBlock() {
        assertAll("unbraced block", () -> {
            // meaningful block
            AstBlockStatement statement = fromString("if(1){}if(2){}").unbracedBlockStatement();
            assertNotNull(statement);
            assertTrue(statement.getStatements().length == 2);
            for (var ifStatement : statement.getStatements()) {
                assertTrue(ifStatement instanceof AstIfStatement);
                assertTrue(((AstIfStatement) ifStatement).getCondition() instanceof AstInteger);
            }
        }, () -> {
            // empty block
            assertTrue(fromString("").unbracedBlockStatement() instanceof AstBlockStatement);
        });
    }

    @Test
    void testVariableInitialise() {
        assertAll("variable initialise", () -> {
            // valid local variable initialise.
            var variableInitialise = fromString("$test = true;").variableInitialize();
            assertNotNull(variableInitialise);
            assertEquals(variableInitialise.getScope(), VariableScope.LOCAL);
            assertEquals(variableInitialise.getName().getText(), "test");
            assertTrue(variableInitialise.getExpression() instanceof AstBool);
        }, () -> {
            // valid global variable initialise.
            var variableInitialise = fromString("%hello = 1234;").variableInitialize();
            assertNotNull(variableInitialise);
            assertEquals(variableInitialise.getScope(), VariableScope.GLOBAL);
            assertEquals(variableInitialise.getName().getText(), "hello");
            assertTrue(variableInitialise.getExpression() instanceof AstInteger);
        }, () -> {
            // missing variable scope.
            assertThrows(SyntaxError.class, () -> fromString("noscope = 5;").variableInitialize());
        }, () -> {
            // missing variable name.
            assertThrows(SyntaxError.class, () -> fromString("% = \"no name\";").variableInitialize());
        }, () -> {
            // missing variable expression.
            assertThrows(SyntaxError.class, () -> fromString("%noexpr = ;").variableInitialize());
        });
    }

    @Test
    void testIntParsing() {
        assertAll("int parsing", () -> {
            // non-signed integer.
            assertEquals(fromString("881251628").integerNumber().getValue(), 881251628);
        }, () -> {
            // negative signed integer.
            assertEquals(fromString("-1040462968").integerNumber().getValue(), -1040462968);
        }, () -> {
            // positive signed integer.
            assertEquals(fromString("1035471165").integerNumber().getValue(), 1035471165);
        });
    }

    @Test
    void testIntRange() {
        assertAll("int range", () -> {
            // integer underflow
            SyntaxError error = assertThrows(SyntaxError.class, () -> fromString("-2147483649").integerNumber());
            assertNotNull(error);
            assertEquals(error.getToken().getKind(), Kind.INTEGER);
        }, () -> {
            // integer overflow
            SyntaxError error = assertThrows(SyntaxError.class, () -> fromString("2147483648").integerNumber());
            assertNotNull(error);
            assertEquals(error.getToken().getKind(), Kind.INTEGER);
        }, () -> {
            // within range
            assertEquals(fromString("1785498889").integerNumber().getValue(), 1785498889);
        });
    }

    @Test
    void testLongParsing() {
        assertAll("long parsing", () -> {
            // lower case long identifier
            assertEquals(fromString("4327430278518173700l").longNumber().getValue(), 4327430278518173700l);
        }, () -> {
            // upper case long identifier
            assertEquals(fromString("5837188049693458000L").longNumber().getValue(), 5837188049693458000L);
        }, () -> {
            // non-signed long.
            assertEquals(fromString("6883184492006257000L").longNumber().getValue(), 6883184492006257000L);
        }, () -> {
            // negative signed long.
            assertEquals(fromString("-7226522914666815000L").longNumber().getValue(), -7226522914666815000L);
        }, () -> {
            // positive signed long.
            assertEquals(fromString("+4809541778570648000L").longNumber().getValue(), +4809541778570648000L);
        });
    }

    @Test
    void testLongRange() {
        assertAll("long range", () -> {
            // long underflow
            SyntaxError error = assertThrows(SyntaxError.class, () -> fromString("-9223372036854775809L").longNumber());
            assertNotNull(error);
            assertEquals(error.getToken().getKind(), Kind.LONG);
        }, () -> {
            // long overflow
            SyntaxError error = assertThrows(SyntaxError.class, () -> fromString("9223372036854775808L").longNumber());
            assertNotNull(error);
            assertEquals(error.getToken().getKind(), Kind.LONG);
        }, () -> {
            // within range
            assertEquals(fromString("8490600559331033000L").longNumber().getValue(), 8490600559331033000L);
        });
    }

    @Test
    void testString() {
        assertEquals(fromString("\"my test string\"").string().getValue(), "my test string");
    }

    @Test
    void testStringConcat() {
        assertEquals(fromString("\"my interpolated <br> text\"").concatString().getExpressions().length, 3);
        var nested = fromString("\"my nested interpolated string <\"<test>\">\"").concatString().getExpressions();
        assertEquals(nested.length, 2);
        assertTrue(nested[0] instanceof AstString);
        assertTrue(nested[1] instanceof AstStringConcat);
    }

    @Test
    void testBool() {
        assertAll("bool", () -> {
            // valid boolean
            assertTrue(fromString("true").bool().getValue());
            assertFalse(fromString("false").bool().getValue());
        }, () -> {
            // invalid boolean
            assertThrows(SyntaxError.class, () -> fromString("tru").bool());
        });
    }

    @Test
    void testIdentifier() {
        assertEquals(fromString("testKeyword").identifier().getText(), "testKeyword");
    }

    @Test
    void testPrimitiveType() {
        assertAll("primitive type", () -> {
            // all valid primitive types.
            for (var type : PrimitiveType.values()) {
                assertEquals(fromString(type.getRepresentation()).primitiveType(), type);
            }
        }, () -> {
            // invalid primitive type.
            assertThrows(SyntaxError.class, () -> fromString("int0").primitiveType());
        }, () -> {
            assertThrows(SyntaxError.class, () -> fromString("voi").primitiveType());
        });
    }

    private static Parser fromString(String text) {
        try (var stream = new StringBufferInputStream(text)) {
            var tokenizer = new Tokenizer(LexicalTable.DEFAULT_TABLE, new BufferedCharStream(stream));
            var lexer = new Lexer(tokenizer);
            return new Parser(lexer);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Parser fromResource(String name) {
        try (var stream = ClassLoader.getSystemResourceAsStream(name)) {
            Tokenizer tokenizer = new Tokenizer(LexicalTable.DEFAULT_TABLE, new BufferedCharStream(stream));
            Lexer lexer = new Lexer(tokenizer);
            Parser parser = new Parser(lexer);
            return parser;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}

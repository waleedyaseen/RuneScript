/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.waliedyassen.runescript.commons.stream.BufferedCharStream;
import me.waliedyassen.runescript.compiler.Compiler;
import me.waliedyassen.runescript.compiler.ast.AstParameter;
import me.waliedyassen.runescript.compiler.ast.expr.*;
import me.waliedyassen.runescript.compiler.ast.expr.literal.AstLiteralBool;
import me.waliedyassen.runescript.compiler.ast.expr.literal.AstLiteralInteger;
import me.waliedyassen.runescript.compiler.ast.expr.literal.AstLiteralLong;
import me.waliedyassen.runescript.compiler.ast.expr.literal.AstLiteralString;
import me.waliedyassen.runescript.compiler.ast.stmt.*;
import me.waliedyassen.runescript.compiler.ast.stmt.conditional.AstIfStatement;
import me.waliedyassen.runescript.compiler.ast.stmt.conditional.AstWhileStatement;
import me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode;
import me.waliedyassen.runescript.compiler.env.CompilerEnvironment;
import me.waliedyassen.runescript.compiler.lexer.Lexer;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.lexer.tokenizer.Tokenizer;
import me.waliedyassen.runescript.compiler.type.ArrayReference;
import me.waliedyassen.runescript.compiler.util.Operator;
import me.waliedyassen.runescript.compiler.util.VariableScope;
import me.waliedyassen.runescript.compiler.util.trigger.TriggerType;
import me.waliedyassen.runescript.parser.SyntaxError;
import me.waliedyassen.runescript.type.PrimitiveType;
import me.waliedyassen.runescript.type.TupleType;
import me.waliedyassen.runescript.type.Type;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringBufferInputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Walied K. Yassen
 */
@SuppressWarnings("deprecation")
public final class ScriptParserTest {

    static CompilerEnvironment environment;

    @BeforeAll
    static void setupEnvironment() {
        environment = new CompilerEnvironment();
        for (TestTriggerType triggerType : TestTriggerType.values()) {
            environment.registerTrigger(triggerType);
        }
    }

    @Test
    void testScript() {
        assertAll("script", () -> {
            var script = fromResource("parse-script.rs2").script();
            assertEquals(script.getCode().getStatements().length, 2);
            assertTrue(script.getCode().getStatements()[0] instanceof AstIfStatement);
            assertTrue(script.getCode().getStatements()[1] instanceof AstIfStatement);
            // TODO: Bad scripts testing.
        }, () -> {
            // script with three parameters
            assertEquals(fromString("[trigger,name](int $one, int $two, string $three) return;").script().getParameters().length, 3);
        }, () -> {
            // script with missing parameter
            assertThrows(SyntaxError.class, () -> fromString("[trigger,name](int $one,").script());
        }, () -> {
            // script with unclosed right parenthesis for parameters
            assertThrows(SyntaxError.class, () -> fromString("[trigger,name](int $one").script());
        }, () -> {
            // script with return type
            assertEquals(fromString("[trigger,name](bool) return;").script().getType(), PrimitiveType.BOOL);
        }, () -> {
            // script with parameters and return type
            var script = fromString("[trigger,name](int $myint, long $mylong)(int) return;").script();
            assertEquals(script.getType(), PrimitiveType.INT);
            assertEquals(script.getParameters().length, 2);
            // same test but with order swapped
            script = fromString("[trigger,name](int)(int $myint, long $mylong) return;").script();
            assertEquals(script.getType(), PrimitiveType.INT);
            assertEquals(script.getParameters().length, 2);
        }, () -> {
            // unclosed return type parenthesis
            assertThrows(SyntaxError.class, () -> fromString("[trigger,name](int").script());
        }, () -> {
            // multiple return types
            var type = fromString("[trigger,name](int,int,long) return;").script().getType();
            assertTrue(type instanceof TupleType);
            assertEquals(3, ((TupleType) type).getChilds().length);
        }, () -> {
            // uncontinued return types
            assertThrows(SyntaxError.class, () -> fromString("[trigger,name](int,int").script());
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
            // illegal parameter type
            assertThrows(SyntaxError.class, () -> fromString("void $param").parameter());
        }, () -> {
            // valid array reference parameter
            var parameter = fromString("intarray $array0").parameter();
            assertTrue(parameter.getType() instanceof ArrayReference);
            var reference = (ArrayReference) parameter.getType();
            assertEquals(PrimitiveType.INT, reference.getType());
            assertEquals(0, reference.getIndex());
        }, () -> {
            // invalid array reference type
            assertThrows(SyntaxError.class, () -> fromString("voidarray $array0").parameter());
        });
    }

    @Test
    void testSimpleExpression() {
        assertAll("simple expression", () -> {
            // string
            assertTrue(fromString("\"myString\"").simpleExpression() instanceof AstLiteralString);
        }, () -> {
            // interpolated string.
            assertTrue(fromString("\"my interpolated string <br>\"").simpleExpression() instanceof AstConcatenation);
        }, () -> {
            // integer
            assertTrue(fromString("123456").simpleExpression() instanceof AstLiteralInteger);
        }, () -> {
            // long
            assertTrue(fromString("123456L").simpleExpression() instanceof AstLiteralLong);
        }, () -> {
            // bool.
            assertTrue(fromString("true").simpleExpression() instanceof AstLiteralBool);
        }, () -> {
            // local variable.
            assertTrue(fromString("$local_var").simpleExpression() instanceof AstVariableExpression);
        }, () -> {
            // local variable.
            assertTrue(fromString("%global_var").simpleExpression() instanceof AstVariableExpression);
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
            assertTrue(expr instanceof AstBinaryOperation);
            var bin = (AstBinaryOperation) expr;
            assertEquals(bin.getOperator(), Operator.GREATER_THAN);
        }, () -> {
            // a bit more complex binary operator.
            var expr = fromString("5 > 3 ! true").expression();
            assertTrue(expr instanceof AstBinaryOperation);
            var bin = (AstBinaryOperation) expr;
            assertTrue(bin.getLeft() instanceof AstBinaryOperation);
            assertEquals(bin.getOperator(), Operator.NOT_EQUAL);
        });
    }

    @Test
    void testParExpression() {
        assertAll("par expression", () -> {
            // valid expression
            assertTrue(fromString("(1234)").parExpression() instanceof AstLiteralInteger);
        }, () -> {
            // invalid expression 1
            assertThrows(SyntaxError.class, () -> fromString("(1234").parExpression());
        }, () -> {
            // invalid expression 2
            assertThrows(SyntaxError.class, () -> fromString("1234)").parExpression());
        });
    }

    @Test
    void testGosubExpression() {
        assertAll("gosub expression", () -> {
            // valid no arguments gosub
            assertEquals("gosub", fromString("~gosub;").call().getName().getText());
        }, () -> {
            // valid with arguments
            assertEquals(3, fromString("~gosub(1234, \"test\", 5 > 4 > 3 > 2 > 1);").call().getArguments().length);
        }, () -> {
            // invalid gosub name
            assertThrows(SyntaxError.class, () -> fromString("~1234(1234);").call());
        }, () -> {
            // invalid gosub arguments
            assertThrows(SyntaxError.class, () -> fromString("~gosub(if);").call());
        });
    }

    @Test
    void testDynamicExpression() {
        assertAll("dynamic expression", () -> {
            // valid dynamic expression
            var expr = fromString("mydynamic").dynamic();
            assertNotNull(expr);
            assertEquals("mydynamic", expr.getName().getText());
        }, () -> {
            // invalid dynamic name
            assertThrows(SyntaxError.class, () -> fromString("0000").dynamic());
        });

    }

    @Test
    void testCommandExpression() {
        assertAll("command expression", () -> {
            // valid alternative command with no arguments
            var expr = fromString(".mycommand").command();
            assertNotNull(expr);
            assertEquals("mycommand", expr.getName().getText());
            assertEquals(0, expr.getArguments().length);
            assertTrue(expr.isAlternative());
        }, () -> {
            // valid non alternative comamnd with arguments
            var expr = fromString("mycommand(5 > 3, ~gosub, \"test\")").command();
            assertNotNull(expr);
            assertEquals("mycommand", expr.getName().getText());
            assertEquals(3, expr.getArguments().length);
            assertFalse(expr.isAlternative());
            assertTrue(expr.getArguments()[0] instanceof AstBinaryOperation);
            assertTrue(expr.getArguments()[1] instanceof AstCall);
            assertTrue(expr.getArguments()[2] instanceof AstLiteralString);
        });
    }

    @Test
    void testCalcExpression() {
        assertAll("calc expression", () -> {
            // valid calc par expression
            var expr = fromString("calc(1)").calc();
            assertNotNull(expr);
            assertTrue(expr.getExpression() instanceof AstLiteralInteger);
        }, () -> {
            // valid double calc no par expression
            var parser = fromString("calc 1 = 5 calc 2 = 5");
            var expr1 = parser.calc();
            assertNotNull(expr1);
            assertTrue(expr1.getExpression() instanceof AstBinaryOperation);
            var expr2 = parser.calc();
            assertNotNull(expr2);
            assertTrue(expr2.getExpression() instanceof AstBinaryOperation);
        }, () -> {
            // expressionless no par calc
            var parser = fromString("calc ");
            assertThrows(SyntaxError.class, () -> parser.calc());
        }, () -> {
            // expressionless par calc
            var parser = fromString("calc()");
            assertThrows(SyntaxError.class, () -> parser.calc());
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
            // valid return statement
            assertTrue(fromString("return test;").statement() instanceof AstReturnStatement);
        }, () -> {
            // valid variable define statement
            assertTrue(fromString("def_bool $mybool = true;").statement() instanceof AstVariableDeclaration);
        }, () -> {
            // valid variable initialise statement
            assertTrue(fromString("$varinit = 5;").statement() instanceof AstVariableInitializer);
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
            var statement = fromString("if(1){}if(2){}").unbracedBlockStatement();
            assertNotNull(statement);
            assertTrue(statement.getStatements().length == 2);
            for (var ifStatement : statement.getStatements()) {
                assertTrue(ifStatement instanceof AstIfStatement);
                assertTrue(((AstIfStatement) ifStatement).getCondition() instanceof AstLiteralInteger);
            }
        }, () -> {
            // empty block
            assertTrue(fromString("").unbracedBlockStatement() instanceof AstBlockStatement);
        });
    }

    @Test
    void testReturnStatement() {
        assertAll("return statement", () -> {
            // valid return one expression
            var returnStatement = fromString("return \"am valid\";").returnStatement();
            assertNotNull(returnStatement);
            assertEquals(1, returnStatement.getExpressions().length);
            assertTrue(returnStatement.getExpressions()[0] instanceof AstLiteralString);
        }, () -> {
            // valid return multiple expressions
            var returnStatement = fromString("return 1,true,\"\";").returnStatement();
        }, () -> {
            // valid return nothing
            var returnStatement = fromString("return;").returnStatement();
            assertNotNull(returnStatement);
            assertEquals(0, returnStatement.getExpressions().length);
        }, () -> {
            // invalid return multiple expressions
            assertThrows(SyntaxError.class, () -> fromString("return 1,2,3,;").returnStatement());
        }, () -> {
            // missing semi colon
            assertThrows(SyntaxError.class, () -> fromString("return if;").returnStatement());
        });
    }

    @Test
    void testVariableDeclaration() {
        assertAll("variable define statement", () -> {
            // valid variable declaration.
            var variableDefine = fromString("def_bool $test = true;").variableDeclaration();
            assertNotNull(variableDefine);
            assertEquals(variableDefine.getType(), PrimitiveType.BOOL);
            assertEquals(variableDefine.getName().getText(), "test");
            assertTrue(variableDefine.getExpression() instanceof AstLiteralBool);
        }, () -> {
            // invalid variable scope.
            assertThrows(SyntaxError.class, () -> fromString("def_bool %test = true;").variableDeclaration());
        }, () -> {
            // missing variable scope.
            assertThrows(SyntaxError.class, () -> fromString("def_int noscope = 5;").variableDeclaration());
        }, () -> {
            // missing variable name.
            assertThrows(SyntaxError.class, () -> fromString("def_string $ = \"no name\";").variableDeclaration());
        }, () -> {
            // missing variable expression.
            assertThrows(SyntaxError.class, () -> fromString("def_long $noexpr = ;").variableDeclaration());
        }, () -> {
            // illegal variable  type.
            assertThrows(SyntaxError.class, () -> fromString("def_void $illegal = 0;").variableDeclaration());
        });
    }

    @Test
    void testVariableInitialiser() {
        assertAll("variable initialise statement", () -> {
            // valid local variable initialise.
            var variableInitialise = fromString("$test = true;").variableInitializer();
            assertNotNull(variableInitialise);
            assertEquals(variableInitialise.getScope(), VariableScope.LOCAL);
            assertEquals(variableInitialise.getName().getText(), "test");
            assertTrue(variableInitialise.getExpression() instanceof AstLiteralBool);
        }, () -> {
            // valid global variable initialise.
            var variableInitialise = fromString("%hello = 1234;").variableInitializer();
            assertNotNull(variableInitialise);
            assertEquals(variableInitialise.getScope(), VariableScope.GLOBAL);
            assertEquals(variableInitialise.getName().getText(), "hello");
            assertTrue(variableInitialise.getExpression() instanceof AstLiteralInteger);
        }, () -> {
            // missing variable scope.
            assertThrows(SyntaxError.class, () -> fromString("noscope = 5;").variableInitializer());
        }, () -> {
            // missing variable name.
            assertThrows(SyntaxError.class, () -> fromString("% = \"no name\";").variableInitializer());
        }, () -> {
            // missing variable expression.
            assertThrows(SyntaxError.class, () -> fromString("%noexpr = ;").variableInitializer());
        });
    }

    @Test
    void testSwitchStatement() {
        assertAll("switch statement", () -> {
            // valid switch statement with only one emptycase
            var _switch = fromString("switch_int($test){case 1,2,3,4,5: return true; case default: return false;}").switchStatement();
            assertNotNull(_switch);
            assertNotNull(_switch.getDefaultCase());
            assertEquals(1, _switch.getCases().length);
            assertEquals(5, _switch.getCases()[0].getKeys().length);
        });
    }

    @Test
    void testSwitchCase() {
        assertAll("switch case", () -> {
            // valid multiple expressions case
            var _case = fromString("case 0,1,2,3,4,5,6: return true;").switchCase();
            var values = new int[]{0, 1, 2, 3, 4, 5, 6};
            assertNotNull(_case);
            assertEquals(7, _case.getKeys().length);
            for (var index = 0; index < values.length; index++) {
                var expr = _case.getKeys()[index];
                assertTrue(expr instanceof AstLiteralInteger);
                assertEquals(values[index], ((AstLiteralInteger) expr).getValue());
            }
            assertEquals(1, _case.getCode().getStatements().length);
            assertTrue(_case.getCode().getStatements()[0] instanceof AstReturnStatement);
        }, () -> {
            // valid case default
            var _case = fromString("case default: return true;").switchCase();
            assertNotNull(_case);
            assertTrue(_case.isDefault());
            assertEquals(1, _case.getCode().getStatements().length);
            assertTrue(_case.getCode().getStatements()[0] instanceof AstReturnStatement);
        }, () -> {
            // empty case
            assertEquals(0, fromString("case 1:").switchCase().getCode().getStatements().length);
        }, () -> {
            // missing case expression
            assertThrows(SyntaxError.class, () -> fromString("case :").switchCase());
        });
    }

    @Test
    void testExpressionStatement() {
        assertAll("expression statement", () -> {
            var stmt = fromString("true;").statement();
            assertTrue(stmt instanceof AstExpressionStatement);
            assertTrue(((AstExpressionStatement) stmt).getExpression() instanceof AstLiteralBool);
        });
    }

    @Test
    void testInt() {
        assertAll("int", () -> {
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
            assertThrows(SyntaxError.class, () -> fromString("-2147483649").integerNumber());
        }, () -> {
            // integer overflow
            assertThrows(SyntaxError.class, () -> fromString("2147483648").integerNumber());
        }, () -> {
            // within range
            assertEquals(fromString("1785498889").integerNumber().getValue(), 1785498889);
        });
    }

    @Test
    void testLong() {
        assertAll("long", () -> {
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
            assertThrows(SyntaxError.class, () -> fromString("-9223372036854775809L").longNumber());
        }, () -> {
            // long overflow
            assertThrows(SyntaxError.class, () -> fromString("9223372036854775808L").longNumber());
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
        assertTrue(nested[0] instanceof AstLiteralString);
        assertTrue(nested[1] instanceof AstConcatenation);
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
                if (type != PrimitiveType.UNDEFINED) {
                    assertEquals(fromString(type.getRepresentation()).primitiveType(), type);
                }
            }
        }, () -> {
            // invalid primitive type.
            assertThrows(SyntaxError.class, () -> fromString("int0").primitiveType());
        }, () -> {
            assertThrows(SyntaxError.class, () -> fromString("voi").primitiveType());
        });
    }

    @Test
    void testArrayType() {
        assertAll("array type", () -> {
            // all valid array types.
            for (var type : PrimitiveType.values()) {
                if (type.isArrayable()) {
                    assertEquals(fromString(type.getRepresentation() + "array").arrayType(), type);
                }
            }
        }, () -> {
            // invalid array type.
            assertThrows(SyntaxError.class, () -> fromString("noarray").primitiveType());
        }, () -> {
            // missing component type.
            assertThrows(SyntaxError.class, () -> fromString("array").primitiveType());
        });
    }

    public static ScriptParser fromString(String text) {
        try (var stream = new StringBufferInputStream(text)) {
            var tokenizer = new Tokenizer(Compiler.createLexicalTable(), new BufferedCharStream(stream));
            var lexer = new Lexer(tokenizer);
            return new ScriptParser(environment, lexer);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ScriptParser fromResource(String name) {
        try (var stream = ClassLoader.getSystemResourceAsStream(name)) {
            Tokenizer tokenizer = new Tokenizer(Compiler.createLexicalTable(), new BufferedCharStream(stream));
            Lexer lexer = new Lexer(tokenizer);
            ScriptParser scriptParser = new ScriptParser(environment, lexer);
            return scriptParser;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequiredArgsConstructor
    public enum TestTriggerType implements TriggerType {
        PROC("proc", Kind.TILDE, CoreOpcode.GOSUB_WITH_PARAMS, true, true),
        CLIENTSCRIPT("clientscript",null, null, true, false),
        LABEL("label", Kind.AT, CoreOpcode.JUMP_WITH_PARAMS, true, false);

        @Getter
        private final String representation;
        @Getter
        private final Kind operator;
        @Getter
        private final CoreOpcode opcode;
        private final boolean hasArguments;
        private final boolean hasReturns;


        @Override
        public boolean hasArguments() {
            return hasArguments;
        }

        @Override
        public Type[] getArgumentTypes() {
            return null;
        }

        @Override
        public boolean hasReturns() {
            return hasReturns;
        }

        @Override
        public Type[] getReturnTypes() {
            return null;
        }
    }
}

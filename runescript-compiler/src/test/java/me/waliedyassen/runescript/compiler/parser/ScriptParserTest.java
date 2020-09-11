/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.commons.stream.BufferedCharStream;
import me.waliedyassen.runescript.compiler.ScriptCompiler;
import me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode;
import me.waliedyassen.runescript.compiler.env.CompilerEnvironment;
import me.waliedyassen.runescript.compiler.error.ThrowingErrorReporter;
import me.waliedyassen.runescript.compiler.lexer.Lexer;
import me.waliedyassen.runescript.compiler.lexer.token.Kind;
import me.waliedyassen.runescript.compiler.lexer.tokenizer.Tokenizer;
import me.waliedyassen.runescript.compiler.symbol.ScriptSymbolTable;
import me.waliedyassen.runescript.compiler.syntax.ParameterSyntax;
import me.waliedyassen.runescript.compiler.syntax.SyntaxParser;
import me.waliedyassen.runescript.compiler.syntax.expr.*;
import me.waliedyassen.runescript.compiler.syntax.expr.literal.LiteralBooleanSyntax;
import me.waliedyassen.runescript.compiler.syntax.expr.literal.LiteralIntegerSyntax;
import me.waliedyassen.runescript.compiler.syntax.expr.literal.LiteralLongSyntax;
import me.waliedyassen.runescript.compiler.syntax.expr.literal.LiteralStringSyntax;
import me.waliedyassen.runescript.compiler.syntax.expr.op.BinaryOperationSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.*;
import me.waliedyassen.runescript.compiler.syntax.stmt.conditional.IfStatementSyntax;
import me.waliedyassen.runescript.compiler.syntax.stmt.loop.WhileStatementSyntax;
import me.waliedyassen.runescript.compiler.type.ArrayReference;
import me.waliedyassen.runescript.compiler.util.Operator;
import me.waliedyassen.runescript.compiler.util.VariableScope;
import me.waliedyassen.runescript.compiler.util.trigger.TriggerType;
import me.waliedyassen.runescript.type.PrimitiveType;
import me.waliedyassen.runescript.type.TupleType;
import me.waliedyassen.runescript.type.Type;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setupEnvironment() {
        environment = new CompilerEnvironment();
        for (var triggerType : TestTriggerType.values()) {
            environment.registerTrigger(triggerType);
        }
    }

    @Test
    void testScript() {
        assertAll("script", () -> {
            var script = fromResource("parse-script.rs2").script();
            assertEquals(script.getCode().getStatements().length, 2);
            assertTrue(script.getCode().getStatements()[0] instanceof IfStatementSyntax);
            assertTrue(script.getCode().getStatements()[1] instanceof IfStatementSyntax);
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
            assertEquals(fromString("[trigger,name](boolean) return;").script().getType(), PrimitiveType.BOOLEAN);
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
            assertTrue(parameter instanceof ParameterSyntax);
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
            assertTrue(fromString("\"myString\"").simpleExpression() instanceof LiteralStringSyntax);
        }, () -> {
            // interpolated string.
            assertTrue(fromString("\"my interpolated string <br>\"").simpleExpression() instanceof ConcatenationSyntax);
        }, () -> {
            // integer
            assertTrue(fromString("123456").simpleExpression() instanceof LiteralIntegerSyntax);
        }, () -> {
            // long
            assertTrue(fromString("123456L").simpleExpression() instanceof LiteralLongSyntax);
        }, () -> {
            // boolean.
            assertTrue(fromString("true").simpleExpression() instanceof LiteralBooleanSyntax);
        }, () -> {
            // local variable.
            assertTrue(fromString("$local_var").simpleExpression() instanceof VariableExpressionSyntax);
        }, () -> {
            // local variable.
            assertTrue(fromString("%global_var").simpleExpression() instanceof VariableExpressionSyntax);
        }, () -> {
            // constant.
            assertTrue(fromString("^constant").simpleExpression() instanceof ConstantSyntax);
        }, () -> {
            // empty
            assertThrows(SyntaxError.class, () -> fromString("").simpleExpression());
        }, () -> {
            // expression in parenthesis.
            assertTrue(fromString("(^constant)").simpleExpression() instanceof ConstantSyntax);
        }, () -> {
            // not expression
            assertThrows(SyntaxError.class, () -> fromString("if({});").simpleExpression());
        });
    }

    @Test
    void testExpression() {
        assertAll("expression", () -> {
            // a basic binary operator.
            var expr = fromString("5 > 3").expression();
            assertTrue(expr instanceof BinaryOperationSyntax);
            var bin = (BinaryOperationSyntax) expr;
            assertEquals(bin.getOperator(), Operator.GREATER_THAN);
        }, () -> {
            // a bit more complex binary operator.
            var expr = fromString("5 > 3 ! true").expression();
            assertTrue(expr instanceof BinaryOperationSyntax);
            var bin = (BinaryOperationSyntax) expr;
            assertTrue(bin.getLeft() instanceof BinaryOperationSyntax);
            assertEquals(bin.getOperator(), Operator.NOT_EQUAL);
        });
    }

    @Test
    void testParExpression() {
        assertAll("par expression", () -> {
            // valid expression
            assertTrue(fromString("(1234)").parExpression() instanceof LiteralIntegerSyntax);
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
            assertThrows(SyntaxError.class, () -> fromString("~~();").call());
        }, () -> {
            // invalid gosub arguments
            assertThrows(SyntaxError.class, () -> fromString("~gosub(~~);").call());
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
            assertThrows(SyntaxError.class, () -> fromString("{}").dynamic());
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
            assertTrue(expr.getArguments()[0] instanceof BinaryOperationSyntax);
            assertTrue(expr.getArguments()[1] instanceof CallSyntax);
            assertTrue(expr.getArguments()[2] instanceof LiteralStringSyntax);
        });
    }

    @Test
    void testCalcExpression() {
        assertAll("calc expression", () -> {
            // valid calc par expression
            var expr = fromString("calc(1)").calc();
            assertNotNull(expr);
            assertTrue(expr.getExpression() instanceof LiteralIntegerSyntax);
        }, () -> {
            // valid double calc no par expression
            var parser = fromString("calc(1 = 5) calc (2 = 5)");
            var expr1 = parser.calc();
            assertNotNull(expr1);
            assertTrue(expr1.getExpression() instanceof BinaryOperationSyntax);
            var expr2 = parser.calc();
            assertNotNull(expr2);
            assertTrue(expr2.getExpression() instanceof BinaryOperationSyntax);
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
            assertTrue(fromString("if(1234){}").statement() instanceof IfStatementSyntax);
        }, () -> {
            // valid block statement
            assertTrue(fromString("{}").statement() instanceof BlockStatementSyntax);
        }, () -> {
            // valid return statement
            assertTrue(fromString("return(test);").statement() instanceof ReturnStatementSyntax);
        }, () -> {
            // valid variable define statement
            assertTrue(fromString("def_boolean $mybool = true;").statement() instanceof VariableDeclarationSyntax);
        }, () -> {
            // valid variable initialise statement
            assertTrue(fromString("$varinit = 5;").statement() instanceof VariableInitializerSyntax);
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
            assertTrue(fromString("if(1){}").ifStatement() instanceof IfStatementSyntax);
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
            assertTrue(fromString("if(1) {} else if(2) {}").ifStatement().getFalseStatement() instanceof IfStatementSyntax);
        }, () -> {
            // missing false code statement.
            assertThrows(SyntaxError.class, () -> fromString("if (2) else").ifStatement());
        });
    }

    @Test
    void testWhileStatement() {
        assertAll("while statement", () -> {
            // valid while loop statement.
            assertTrue(fromString("while (true) {}").whileStatement() instanceof WhileStatementSyntax);
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
            assertTrue(blockStatement.getStatements()[0] instanceof IfStatementSyntax);
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
                assertTrue(ifStatement instanceof IfStatementSyntax);
                assertTrue(((IfStatementSyntax) ifStatement).getCondition() instanceof LiteralIntegerSyntax);
            }
        }, () -> {
            // empty block
            assertTrue(fromString("").unbracedBlockStatement() instanceof BlockStatementSyntax);
        });
    }

    @Test
    void testReturnStatement() {
        assertAll("return statement", () -> {
            // valid return one expression
            var returnStatement = fromString("return(\"am valid\");").returnStatement();
            assertNotNull(returnStatement);
            assertEquals(1, returnStatement.getExpressions().length);
            assertTrue(returnStatement.getExpressions()[0] instanceof LiteralStringSyntax);
        }, () -> {
            // valid return multiple expressions
            var returnStatement = fromString("return(1,true,\"\");").returnStatement();
        }, () -> {
            // valid return nothing
            var returnStatement = fromString("return;").returnStatement();
            assertNotNull(returnStatement);
            assertEquals(0, returnStatement.getExpressions().length);
        }, () -> {
            // invalid return multiple expressions
            assertThrows(SyntaxError.class, () -> fromString("return(1,2,3,);").returnStatement());
        }, () -> {
            // missing semi colon
            assertThrows(SyntaxError.class, () -> fromString("return if;").returnStatement());
        });
    }

    @Test
    void testVariableDeclaration() {
        assertAll("variable define statement", () -> {
            // valid variable declaration.
            var variableDefine = fromString("def_boolean $test = true;").variableDeclaration();
            assertNotNull(variableDefine);
            assertEquals(variableDefine.getType(), PrimitiveType.BOOLEAN);
            assertEquals(variableDefine.getName().getText(), "test");
            assertTrue(variableDefine.getExpression() instanceof LiteralBooleanSyntax);
        }, () -> {
            // invalid variable scope.
            assertThrows(SyntaxError.class, () -> fromString("def_boolean %test = true;").variableDeclaration());
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
            assertEquals(((ScopedVariableSyntax) variableInitialise.getVariables()[0]).getScope(), VariableScope.LOCAL);
            assertEquals(variableInitialise.getVariables()[0].getName().getText(), "test");
            assertTrue(variableInitialise.getExpressions()[0] instanceof LiteralBooleanSyntax);
        }, () -> {
            // valid global variable initialise.
            var variableInitialise = fromString("%hello = 1234;").variableInitializer();
            assertNotNull(variableInitialise);
            assertEquals(((ScopedVariableSyntax) variableInitialise.getVariables()[0]).getScope(), VariableScope.GLOBAL);
            assertEquals(variableInitialise.getVariables()[0].getName().getText(), "hello");
            assertTrue(variableInitialise.getExpressions()[0] instanceof LiteralIntegerSyntax);
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
            var _switch = fromString("switch_int($test){case 1,2,3,4,5 : return(true); case default : return(false);}").switchStatement();
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
            var _case = fromString("case 0,1,2,3,4,5,6 : return(true);").switchCase();
            var values = new int[]{0, 1, 2, 3, 4, 5, 6};
            assertNotNull(_case);
            assertEquals(7, _case.getKeys().length);
            for (var index = 0; index < values.length; index++) {
                var expr = _case.getKeys()[index];
                assertTrue(expr instanceof LiteralIntegerSyntax);
                assertEquals(values[index], ((LiteralIntegerSyntax) expr).getValue().intValue());
            }
            assertEquals(1, _case.getCode().getStatements().length);
            assertTrue(_case.getCode().getStatements()[0] instanceof ReturnStatementSyntax);
        }, () -> {
            // valid case default
            var _case = fromString("case default : return(true);").switchCase();
            assertNotNull(_case);
            assertTrue(_case.isDefault());
            assertEquals(1, _case.getCode().getStatements().length);
            assertTrue(_case.getCode().getStatements()[0] instanceof ReturnStatementSyntax);
        }, () -> {
            // empty case
            assertEquals(0, fromString("case 1 :").switchCase().getCode().getStatements().length);
        }, () -> {
            // missing case expression
            assertThrows(SyntaxError.class, () -> fromString("case :").switchCase());
        });
    }

    @Test
    void testExpressionStatement() {
        var stmt = fromString(".command;").statement();
        assertTrue(stmt instanceof ExpressionStatementSyntax);
        assertTrue(((ExpressionStatementSyntax) stmt).getExpression() instanceof CommandSyntax);
    }

    @Test
    void testInt() {
        assertAll("int", () -> {
            // non-signed integer.
            assertEquals(fromString("881251628").literalInteger().getValue().intValue(), 881251628);
        }, () -> {
            // negative signed integer.
            assertEquals(fromString("-1040462968").literalInteger().getValue().intValue(), -1040462968);
        }, () -> {
            // positive signed integer.
            assertEquals(fromString("1035471165").literalInteger().getValue().intValue(), 1035471165);
        });
    }

    @Test
    void testIntRange() {
        assertAll("int range", () -> {
            // integer underflow
            assertThrows(SyntaxError.class, () -> fromString("-2147483649").literalInteger());
        }, () -> {
            // integer overflow
            assertThrows(SyntaxError.class, () -> fromString("2147483648").literalInteger());
        }, () -> {
            // within range
            assertEquals(fromString("1785498889").literalInteger().getValue().intValue(), 1785498889);
        });
    }

    @Test
    void testLong() {
        assertAll("long", () -> {
            // lower case long identifier
            assertEquals(fromString("4327430278518173700l").literalLong().getValue().longValue(), 4327430278518173700l);
        }, () -> {
            // upper case long identifier
            assertEquals(fromString("5837188049693458000L").literalLong().getValue().longValue(), 5837188049693458000L);
        }, () -> {
            // non-signed long.
            assertEquals(fromString("6883184492006257000L").literalLong().getValue().longValue(), 6883184492006257000L);
        }, () -> {
            // negative signed long.
            assertEquals(fromString("-7226522914666815000L").literalLong().getValue().longValue(), -7226522914666815000L);
        }, () -> {
            // positive signed long.
            assertEquals(fromString("+4809541778570648000L").literalLong().getValue().longValue(), +4809541778570648000L);
        });
    }

    @Test
    void testLongRange() {
        assertAll("long range", () -> {
            // long underflow
            assertThrows(SyntaxError.class, () -> fromString("-9223372036854775809L").literalLong());
        }, () -> {
            // long overflow
            assertThrows(SyntaxError.class, () -> fromString("9223372036854775808L").literalLong());
        }, () -> {
            // within range
            assertEquals(fromString("8490600559331033000L").literalLong().getValue().longValue(), 8490600559331033000L);
        });
    }

    @Test
    void testString() {
        assertEquals(fromString("\"my test string\"").literalString().getValue(), "my test string");
    }

    @Test
    void testStringConcat() {
        assertEquals(fromString("\"my interpolated <br> text\"").concatString().getExpressions().length, 3);
        var nested = fromString("\"my nested interpolated string <\"<test>\">\"").concatString().getExpressions();
        assertEquals(nested.length, 2);
        assertTrue(nested[0] instanceof LiteralStringSyntax);
        assertTrue(nested[1] instanceof ConcatenationSyntax);
    }

    @Test
    void testBool() {
        assertAll("boolean", () -> {
            // valid boolean
            assertTrue(fromString("true").literalBool().getValue());
            assertFalse(fromString("false").literalBool().getValue());
        }, () -> {
            // invalid boolean
            assertThrows(SyntaxError.class, () -> fromString("tru").literalBool());
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
                if (type.isReferencable()) {
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

    public static SyntaxParser fromString(String text) {
        try (var stream = new StringBufferInputStream(text)) {
            var tokenizer = new Tokenizer(new ThrowingErrorReporter(), ScriptCompiler.createLexicalTable(), new BufferedCharStream(stream));
            var lexer = new Lexer(tokenizer);
            return new SyntaxParser(environment, new ScriptSymbolTable(), new ThrowingErrorReporter(), lexer, "cs2");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SyntaxParser fromResource(String name) {
        try (var stream = ClassLoader.getSystemResourceAsStream(name)) {
            Tokenizer tokenizer = new Tokenizer(new ThrowingErrorReporter(), ScriptCompiler.createLexicalTable(), new BufferedCharStream(stream));
            Lexer lexer = new Lexer(tokenizer);
            return new SyntaxParser(environment, new ScriptSymbolTable(), new ThrowingErrorReporter(), lexer, "cs2");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequiredArgsConstructor
    public enum TestTriggerType implements TriggerType {
        PROC("proc", Kind.TILDE, CoreOpcode.GOSUB_WITH_PARAMS, true, true),
        CLIENTSCRIPT("clientscript", null, null, true, false),
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

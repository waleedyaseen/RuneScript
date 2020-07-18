/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.ast.visitor;

import lombok.var;
import me.waliedyassen.runescript.commons.stream.BufferedCharStream;
import me.waliedyassen.runescript.compiler.Compiler;
import me.waliedyassen.runescript.compiler.ast.AstParameter;
import me.waliedyassen.runescript.compiler.ast.AstScript;
import me.waliedyassen.runescript.compiler.ast.expr.*;
import me.waliedyassen.runescript.compiler.ast.expr.literal.*;
import me.waliedyassen.runescript.compiler.ast.stmt.*;
import me.waliedyassen.runescript.compiler.ast.stmt.conditional.AstIfStatement;
import me.waliedyassen.runescript.compiler.ast.stmt.conditional.AstWhileStatement;
import me.waliedyassen.runescript.compiler.env.CompilerEnvironment;
import me.waliedyassen.runescript.compiler.lexer.Lexer;
import me.waliedyassen.runescript.compiler.lexer.tokenizer.Tokenizer;
import me.waliedyassen.runescript.compiler.parser.ScriptParser;
import me.waliedyassen.runescript.compiler.parser.ScriptParserTest;
import me.waliedyassen.runescript.compiler.symbol.ScriptSymbolTable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringBufferInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AstTreeVisitorTest {


    private static final CompilerEnvironment environment = new CompilerEnvironment();
    private final CountingVisitor visitor = new CountingVisitor();

    @BeforeAll
    static void setupEnvironment() {
        for (ScriptParserTest.TestTriggerType triggerType : ScriptParserTest.TestTriggerType.values()) {
            environment.registerTrigger(triggerType);
        }
    }

    @BeforeEach
    void resetCounters() {
        visitor.reset();
    }

    @Test
    void testAll() {
        var script = fromResource("visitor-tree-script.rs2").script();
        script.accept(visitor);
        assertEquals(1, visitor.scripts.count());
        assertEquals(4, visitor.parameters.count());
        assertEquals(4, visitor.literalBool.count());
        assertEquals(23, visitor.literalInteger.count());
        assertEquals(1, visitor.literalLong.count());
        assertEquals(7, visitor.literalString.count());
        assertEquals(1, visitor.concatenation.count());
        assertEquals(7, visitor.variableExpression.count());
        assertEquals(1, visitor.call.count());
        assertEquals(2, visitor.dynamic.count());
        assertEquals(1, visitor.constant.count());
        assertEquals(4, visitor.command.count());
        assertEquals(2, visitor.binaryOperation.count());
        assertEquals(6, visitor.variableDeclaration.count());
        assertEquals(7, visitor.variableInitializer.count());
        assertEquals(2, visitor.switchStatement.count());
        assertEquals(5, visitor.switchCase.count());
        assertEquals(2, visitor.ifStatement.count());
        assertEquals(1, visitor.whileStatement.count());
        assertEquals(4, visitor.expressionStatement.count());
        assertEquals(1, visitor.returnStatement.count());
    }

    static class CountingVisitor extends AstTreeVisitor {

        final Counter scripts = new Counter();
        final Counter parameters = new Counter();
        final Counter literalBool = new Counter();
        final Counter literalInteger = new Counter();
        final Counter literalLong = new Counter();
        final Counter literalString = new Counter();
        final Counter literalCoordgrid = new Counter();
        final Counter concatenation = new Counter();
        final Counter component = new Counter();
        final Counter variableExpression = new Counter();
        final Counter call = new Counter();
        final Counter dynamic = new Counter();
        final Counter constant = new Counter();
        final Counter command = new Counter();
        final Counter calc = new Counter();
        final Counter binaryOperation = new Counter();
        final Counter variableDeclaration = new Counter();
        final Counter variableInitializer = new Counter();
        final Counter switchStatement = new Counter();
        final Counter switchCase = new Counter();
        final Counter ifStatement = new Counter();
        final Counter whileStatement = new Counter();
        final Counter expressionStatement = new Counter();
        final Counter returnStatement = new Counter();

        void reset() {
            scripts.reset();
            parameters.reset();
            literalBool.reset();
            literalInteger.reset();
            literalLong.reset();
            literalString.reset();
            literalCoordgrid.reset();
            component.reset();
            concatenation.reset();
            variableExpression.reset();
            call.reset();
            dynamic.reset();
            constant.reset();
            command.reset();
            calc.reset();
            binaryOperation.reset();
            variableDeclaration.reset();
            variableInitializer.reset();
            switchStatement.reset();
            switchCase.reset();
            ifStatement.reset();
            whileStatement.reset();
            expressionStatement.reset();
            returnStatement.reset();
        }

        @Override
        public void enter(AstScript script) {
            super.enter(script);
            this.scripts.numEnters++;
        }

        @Override
        public void exit(AstScript script) {
            super.exit(script);
            this.scripts.numExits++;
        }

        @Override
        public void enter(AstParameter parameter) {
            super.enter(parameter);
            this.parameters.numEnters++;
        }

        @Override
        public void exit(AstParameter parameter) {
            super.exit(parameter);
            this.parameters.numExits++;
        }

        @Override
        public void enter(AstLiteralBool bool) {
            super.enter(bool);
            this.literalBool.numEnters++;
        }

        @Override
        public void exit(AstLiteralBool bool) {
            super.exit(bool);
            this.literalBool.numExits++;
        }

        @Override
        public void enter(AstLiteralInteger integer) {
            super.enter(integer);
            this.literalInteger.numEnters++;
        }

        @Override
        public void exit(AstLiteralInteger integer) {
            super.exit(integer);
            this.literalInteger.numExits++;
        }

        @Override
        public void enter(AstLiteralLong longInteger) {
            super.enter(longInteger);
            this.literalLong.numEnters++;
        }

        @Override
        public void exit(AstLiteralLong longInteger) {
            super.exit(longInteger);
            this.literalLong.numExits++;
        }

        @Override
        public void enter(AstLiteralString string) {
            super.enter(string);
            this.literalString.numEnters++;
        }

        @Override
        public void exit(AstLiteralString string) {
            super.exit(string);
            this.literalString.numExits++;
        }

        @Override
        public void enter(AstLiteralCoordgrid coordgrid) {
            super.enter(coordgrid);
            this.literalCoordgrid.numEnters++;
        }

        @Override
        public void exit(AstLiteralCoordgrid coordgrid) {
            super.exit(coordgrid);
            this.literalCoordgrid.numExits++;
        }

        @Override
        public void enter(AstConcatenation concatenation) {
            super.enter(concatenation);
            this.concatenation.numEnters++;
        }

        @Override
        public void exit(AstConcatenation concatenation) {
            super.exit(concatenation);
            this.concatenation.numExits++;
        }

        @Override
        public void enter(AstComponent component) {
            super.enter(component);
            this.component.numEnters++;
        }

        @Override
        public void exit(AstComponent component) {
            super.exit(component);
            this.component.numExits++;
        }

        @Override
        public void enter(AstVariableExpression variableExpression) {
            super.enter(variableExpression);
            this.variableExpression.numEnters++;
        }

        @Override
        public void exit(AstVariableExpression variableExpression) {
            super.exit(variableExpression);
            this.variableExpression.numExits++;
        }

        @Override
        public void enter(AstCall call) {
            super.enter(call);
            this.call.numEnters++;
        }

        @Override
        public void exit(AstCall call) {
            super.exit(call);
            this.call.numExits++;
        }

        @Override
        public void enter(AstDynamic dynamic) {
            super.enter(dynamic);
            this.dynamic.numEnters++;
        }

        @Override
        public void exit(AstDynamic dynamic) {
            super.exit(dynamic);
            this.dynamic.numExits++;
        }

        @Override
        public void enter(AstConstant constant) {
            super.enter(constant);
            this.constant.numEnters++;
        }

        @Override
        public void exit(AstConstant constant) {
            super.exit(constant);
            this.constant.numExits++;
        }

        @Override
        public void enter(AstCommand command) {
            super.enter(command);
            this.command.numEnters++;
        }

        @Override
        public void exit(AstCommand command) {
            super.exit(command);
            this.command.numExits++;
        }

        @Override
        public void enter(AstCalc calc) {
            super.enter(calc);
            this.calc.numEnters++;
        }

        @Override
        public void exit(AstCalc calc) {
            super.exit(calc);
            this.calc.numExits++;
        }

        @Override
        public void enter(AstBinaryOperation binaryOperation) {
            super.enter(binaryOperation);
            this.binaryOperation.numEnters++;
        }

        @Override
        public void exit(AstBinaryOperation binaryOperation) {
            super.exit(binaryOperation);
            this.binaryOperation.numExits++;
        }

        @Override
        public void enter(AstVariableDeclaration variableDeclaration) {
            super.enter(variableDeclaration);
            this.variableDeclaration.numEnters++;
        }

        @Override
        public void exit(AstVariableDeclaration variableDeclaration) {
            super.exit(variableDeclaration);
            this.variableDeclaration.numExits++;
        }

        @Override
        public void enter(AstVariableInitializer variableInitializer) {
            super.enter(variableInitializer);
            this.variableInitializer.numEnters++;
        }

        @Override
        public void exit(AstVariableInitializer variableInitializer) {
            super.exit(variableInitializer);
            this.variableInitializer.numExits++;
        }

        @Override
        public void enter(AstSwitchStatement switchStatement) {
            super.enter(switchStatement);
            this.switchStatement.numEnters++;
        }

        @Override
        public void exit(AstSwitchStatement switchStatement) {
            super.exit(switchStatement);
            this.switchStatement.numExits++;
        }

        @Override
        public void enter(AstSwitchCase switchCase) {
            super.enter(switchCase);
            this.switchCase.numEnters++;
        }

        @Override
        public void exit(AstSwitchCase switchCase) {
            super.exit(switchCase);
            this.switchCase.numExits++;
        }

        @Override
        public void enter(AstIfStatement ifStatement) {
            super.enter(ifStatement);
            this.ifStatement.numEnters++;
        }

        @Override
        public void exit(AstIfStatement ifStatement) {
            super.exit(ifStatement);
            this.ifStatement.numExits++;
        }

        @Override
        public void enter(AstWhileStatement whileStatement) {
            super.enter(whileStatement);
            this.whileStatement.numEnters++;
        }

        @Override
        public void exit(AstWhileStatement whileStatement) {
            super.exit(whileStatement);
            this.whileStatement.numExits++;
        }

        @Override
        public void enter(AstExpressionStatement expressionStatement) {
            super.enter(expressionStatement);
            this.expressionStatement.numEnters++;
        }

        @Override
        public void exit(AstExpressionStatement expressionStatement) {
            super.exit(expressionStatement);
            this.expressionStatement.numExits++;
        }

        @Override
        public void enter(AstReturnStatement returnStatement) {
            super.enter(returnStatement);
            this.returnStatement.numEnters++;
        }

        @Override
        public void exit(AstReturnStatement returnStatement) {
            super.exit(returnStatement);
            this.returnStatement.numExits++;
        }
    }

    static class Counter {
        int numEnters;
        int numExits;

        void reset() {
            numEnters = 0;
            numExits = 0;
        }

        public int count() {
            if (numEnters != numExits) {
                throw new IllegalStateException("The amount of enters does not match the amount of exits");
            }
            return numEnters;
        }
    }

    public static ScriptParser fromString(String text) {
        try (var stream = new StringBufferInputStream(text)) {
            var tokenizer = new Tokenizer(Compiler.createLexicalTable(), new BufferedCharStream(stream));
            var lexer = new Lexer(tokenizer);
            return new ScriptParser(environment, new ScriptSymbolTable(), lexer);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ScriptParser fromResource(String name) {
        try (var stream = ClassLoader.getSystemResourceAsStream(name)) {
            Tokenizer tokenizer = new Tokenizer(Compiler.createLexicalTable(), new BufferedCharStream(stream));
            Lexer lexer = new Lexer(tokenizer);
            return new ScriptParser(environment, new ScriptSymbolTable(), lexer);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
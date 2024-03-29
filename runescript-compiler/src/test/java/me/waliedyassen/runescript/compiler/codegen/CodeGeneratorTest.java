/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen;

import me.waliedyassen.runescript.commons.stream.BufferedCharStream;
import me.waliedyassen.runescript.compiler.CompiledScriptUnit;
import me.waliedyassen.runescript.compiler.ScriptCompiler;
import me.waliedyassen.runescript.compiler.codegen.block.Label;
import me.waliedyassen.runescript.compiler.codegen.local.Local;
import me.waliedyassen.runescript.compiler.codegen.opcode.BasicOpcode;
import me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode;
import me.waliedyassen.runescript.compiler.codegen.script.BinaryScript;
import me.waliedyassen.runescript.compiler.env.CompilerEnvironment;
import me.waliedyassen.runescript.compiler.error.ThrowingErrorReporter;
import me.waliedyassen.runescript.compiler.lexer.Lexer;
import me.waliedyassen.runescript.compiler.lexer.tokenizer.Tokenizer;
import me.waliedyassen.runescript.compiler.parser.ScriptParserTest;
import me.waliedyassen.runescript.compiler.semantics.SemanticChecker;
import me.waliedyassen.runescript.compiler.symbol.ScriptSymbolTable;
import me.waliedyassen.runescript.compiler.symbol.impl.script.ScriptInfo;
import me.waliedyassen.runescript.compiler.syntax.SyntaxParser;
import me.waliedyassen.runescript.type.Type;
import me.waliedyassen.runescript.type.primitive.PrimitiveType;
import me.waliedyassen.runescript.type.stack.StackType;
import me.waliedyassen.runescript.type.tuple.TupleType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CodeGeneratorTest {

    CodeGenerator generator;
    SemanticChecker checker;
    InstructionMap instructionMap;
    static CompilerEnvironment environment;

    @BeforeAll
    static void setupEnvironment() {
        environment = new CompilerEnvironment();
        for (ScriptParserTest.TestTriggerType triggerType : ScriptParserTest.TestTriggerType.values()) {
            environment.registerTrigger(triggerType);
        }
    }

    @BeforeEach
    void setupGenerator() {
        var table = new ScriptSymbolTable(true);
        instructionMap = new InstructionMap();
        for (var opcode : CoreOpcode.values()) {
            instructionMap.registerCore(opcode, opcode.ordinal(), opcode.isLargeOperand());
        }
        checker = new SemanticChecker(null, environment, table, false);
        generator = new CodeGenerator(environment, table, instructionMap, ScriptParserTest.TestTriggerType.CLIENTSCRIPT);
        generator.initialise();
        table.defineCommand(new BasicOpcode(0, false), "func_i_i", new Type[]{PrimitiveType.INT.INSTANCE}, PrimitiveType.INT.INSTANCE,  null, false);
        table.defineCommand(new BasicOpcode(3100, false), "writeconsole", new Type[]{PrimitiveType.STRING.INSTANCE}, PrimitiveType.VOID.INSTANCE, null, false);
        table.defineCommand(new BasicOpcode(3101, false), "tostring", new Type[]{PrimitiveType.INT.INSTANCE}, PrimitiveType.STRING.INSTANCE, null, false);

    }

    @Test
    void testLocalDefine() {
        var script = fromResource("local_01.rs2")[0];
        var block = script.getBlockList().getBlock(new Label(0, "entry_0"));
        assertEquals(4, script.getParameters().values().stream().mapToInt(Collection::size).sum());
        assertEquals(new Local("bool_param", PrimitiveType.BOOLEAN.INSTANCE), script.getParameters().get(StackType.INT).get(0));
        assertEquals(new Local("int_param", PrimitiveType.INT.INSTANCE), script.getParameters().get(StackType.INT).get(1));
        assertEquals(new Local("string_param", PrimitiveType.STRING.INSTANCE), script.getParameters().get(StackType.STRING).get(0));
        assertEquals(new Local("long_param", PrimitiveType.LONG.INSTANCE), script.getParameters().get(StackType.LONG).get(0));
        assertEquals(11, block.getInstructions().size());
        assertInstructionEquals(block.getInstructions().get(0), CoreOpcode.PUSH_INT_CONSTANT, 1234);
        assertInstructionEquals(block.getInstructions().get(1), CoreOpcode.POP_INT_LOCAL, new Local("my_int", PrimitiveType.INT.INSTANCE));
        assertInstructionEquals(block.getInstructions().get(2), CoreOpcode.PUSH_STRING_CONSTANT, "test");
        assertInstructionEquals(block.getInstructions().get(3), CoreOpcode.POP_STRING_LOCAL, new Local("my_string", PrimitiveType.STRING.INSTANCE));
        assertInstructionEquals(block.getInstructions().get(4), CoreOpcode.PUSH_INT_CONSTANT, 1);
        assertInstructionEquals(block.getInstructions().get(5), CoreOpcode.POP_INT_LOCAL, new Local("my_true_bool", PrimitiveType.BOOLEAN.INSTANCE));
        assertInstructionEquals(block.getInstructions().get(6), CoreOpcode.PUSH_LONG_CONSTANT, 1234L);
        assertInstructionEquals(block.getInstructions().get(7), CoreOpcode.POP_LONG_LOCAL, new Local("my_long", PrimitiveType.LONG.INSTANCE));
        assertInstructionEquals(block.getInstructions().get(8), CoreOpcode.PUSH_INT_CONSTANT, 0);
        assertInstructionEquals(block.getInstructions().get(9), CoreOpcode.POP_INT_LOCAL, new Local("my_false_bool", PrimitiveType.BOOLEAN.INSTANCE));
        assertInstructionEquals(block.getInstructions().get(10), CoreOpcode.RETURN, 0);
    }

    @Test
    void testOperatorBitwiseAnd() {
        var script = fromResource("operator_bitwise_and.rs2")[0];
        var block = script.getBlockList().getBlock(new Label(0, "entry_0"));
        assertInstructionEquals(block.getInstructions().get(0), CoreOpcode.PUSH_INT_LOCAL, new Local("a", PrimitiveType.INT.INSTANCE));
        assertInstructionEquals(block.getInstructions().get(1), CoreOpcode.PUSH_INT_LOCAL, new Local("b", PrimitiveType.INT.INSTANCE));
        assertInstructionEquals(block.getInstructions().get(2), CoreOpcode.AND, 0);
        assertInstructionEquals(block.getInstructions().get(3), CoreOpcode.RETURN, 0);
    }

    @Test
    void testOperatorBitwiseOr() {
        var script = fromResource("operator_bitwise_or.rs2")[0];
        var block = script.getBlockList().getBlock(new Label(0, "entry_0"));
        assertInstructionEquals(block.getInstructions().get(0), CoreOpcode.PUSH_INT_LOCAL, new Local("a", PrimitiveType.INT.INSTANCE));
        assertInstructionEquals(block.getInstructions().get(1), CoreOpcode.PUSH_INT_LOCAL, new Local("b", PrimitiveType.INT.INSTANCE));
        assertInstructionEquals(block.getInstructions().get(2), CoreOpcode.OR, 0);
        assertInstructionEquals(block.getInstructions().get(3), CoreOpcode.RETURN, 0);
    }

    @Test
    void testCalcPrecedence() {
        var script = fromString("[proc,test](int $parameter)(int) return(calc(1 + $parameter * 5));")[0];
        var block = script.getBlockList().getBlock(new Label(0, "entry_0"));
        assertInstructionEquals(block.getInstructions().get(0), CoreOpcode.PUSH_INT_CONSTANT, 1);
        assertInstructionEquals(block.getInstructions().get(1), CoreOpcode.PUSH_INT_LOCAL, new Local("parameter", PrimitiveType.INT.INSTANCE));
        assertInstructionEquals(block.getInstructions().get(2), CoreOpcode.PUSH_INT_CONSTANT, 5);
        assertInstructionEquals(block.getInstructions().get(3), CoreOpcode.MUL, 0);
        assertInstructionEquals(block.getInstructions().get(4), CoreOpcode.ADD, 0);
        assertInstructionEquals(block.getInstructions().get(5), CoreOpcode.RETURN, 0);
    }

    @Test
    void testCalcSimple() {
        var script = fromString("[proc,test](int $parameter)(int) return(calc($parameter));")[0];
        var block = script.getBlockList().getBlock(new Label(0, "entry_0"));
        assertInstructionEquals(block.getInstructions().get(0), CoreOpcode.PUSH_INT_LOCAL, new Local("parameter", PrimitiveType.INT.INSTANCE));
        assertInstructionEquals(block.getInstructions().get(1), CoreOpcode.RETURN, 0);
    }

    @Test
    void testDiscard() {
        var script = fromString("[proc,test](int $parameter) func_i_i($parameter);")[0];
        var block = script.getBlockList().getBlock(new Label(0, "entry_0"));
        assertInstructionEquals(block.getInstructions().get(0), CoreOpcode.PUSH_INT_LOCAL, new Local("parameter", PrimitiveType.INT.INSTANCE));
        assertInstructionEquals(block.getInstructions().get(1), 0, false, 0);
        assertInstructionEquals(block.getInstructions().get(2), CoreOpcode.POP_INT_DISCARD, 0);
        assertInstructionEquals(block.getInstructions().get(3), CoreOpcode.RETURN, 0);
    }

    @Test
    void testCall01() {
        var scripts = fromString("[proc,my_proc](int $parameter) @my_label(0); [label,my_label](int $parameter) ~my_proc(0);");
        assertEquals(2, scripts.length);
        var first = scripts[0];
        var first_block = first.getBlockList().getBlock(new Label(0, "entry_0"));
        assertInstructionEquals(first_block.getInstructions().get(0), CoreOpcode.PUSH_INT_CONSTANT, 0);
        assertInstructionEquals(first_block.getInstructions().get(1), CoreOpcode.JUMP_WITH_PARAMS, new ScriptInfo("my_label", -1, ScriptParserTest.TestTriggerType.LABEL, new TupleType(), new Type[]{PrimitiveType.INT.INSTANCE}));
        var second = scripts[1];
        var second_block = second.getBlockList().getBlock(new Label(0, "entry_0"));
        assertInstructionEquals(second_block.getInstructions().get(0), CoreOpcode.PUSH_INT_CONSTANT, 0);
        assertInstructionEquals(second_block.getInstructions().get(1), CoreOpcode.GOSUB_WITH_PARAMS, new ScriptInfo( "my_proc", -1, ScriptParserTest.TestTriggerType.PROC, new TupleType(), new Type[]{PrimitiveType.INT.INSTANCE}));
    }

    void assertInstructionEquals(Instruction instruction, CoreOpcode opcode, Object operand) {
        var mapped = (InstructionMap.MappedOpcode) instruction.getOpcode();
        assertEquals(opcode, mapped.getOpcode());
        assertEquals(opcode.isLargeOperand(), mapped.isLarge());
        assertEquals(operand, instruction.getOperand());
    }


    void assertInstructionEquals(Instruction instruction, int code, boolean large, Object operand) {
        var opcode = instruction.getOpcode();
        assertEquals(code, opcode.getCode());
        assertEquals(large, opcode.isLarge());
        assertEquals(operand, instruction.getOperand());
    }


    BinaryScript[] fromResource(String name) {
        try (var stream = getClass().getResourceAsStream(name)) {
            var tokenizer = new Tokenizer(new ThrowingErrorReporter(), ScriptCompiler.createLexicalTable(), new BufferedCharStream(stream));
            var lexer = new Lexer(tokenizer);
            var parser = new SyntaxParser(environment, new ScriptSymbolTable(true), new ThrowingErrorReporter(), lexer, "cs2");
            var scripts = new ArrayList<CompiledScriptUnit>();
            do {
                var unit = new CompiledScriptUnit();
                unit.setSyntax(parser.script());
                scripts.add(unit);
            } while (lexer.remaining() > 0);
            checker.executePre(scripts);
            checker.execute(scripts);
            var parsed = new BinaryScript[scripts.size()];
            for (var index = 0; index < parsed.length; index++) {
                parsed[index] = generator.visit(scripts.get(index).getSyntax());
            }
            return parsed;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    BinaryScript[] fromString(String text) {
        var tokenizer = new Tokenizer(new ThrowingErrorReporter(), ScriptCompiler.createLexicalTable(), new BufferedCharStream(text.toCharArray()));
        var lexer = new Lexer(tokenizer);
        var parser = new SyntaxParser(environment, new ScriptSymbolTable(true), new ThrowingErrorReporter(), lexer, "cs2");
        var scripts = new ArrayList<CompiledScriptUnit>();
        do {
            var unit = new CompiledScriptUnit();
            unit.setSyntax(parser.script());
            scripts.add(unit);
        } while (lexer.remaining() > 0);
        checker.executePre(scripts);
        checker.execute(scripts);
        checker.getErrors().forEach(System.out::println);
        var parsed = new BinaryScript[scripts.size()];
        for (var index = 0; index < parsed.length; index++) {
            parsed[index] = generator.visit(scripts.get(index).getSyntax());
        }
        return parsed;
    }
}
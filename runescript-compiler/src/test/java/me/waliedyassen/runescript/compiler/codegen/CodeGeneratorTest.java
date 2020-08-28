/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen;

import lombok.var;
import me.waliedyassen.runescript.commons.stream.BufferedCharStream;
import me.waliedyassen.runescript.compiler.CompiledScriptUnit;
import me.waliedyassen.runescript.compiler.ScriptCompiler;
import me.waliedyassen.runescript.compiler.codegen.block.Label;
import me.waliedyassen.runescript.compiler.codegen.local.Local;
import me.waliedyassen.runescript.compiler.codegen.opcode.BasicOpcode;
import me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode;
import me.waliedyassen.runescript.compiler.codegen.script.BinaryScript;
import me.waliedyassen.runescript.compiler.env.CompilerEnvironment;
import me.waliedyassen.runescript.compiler.lexer.Lexer;
import me.waliedyassen.runescript.compiler.lexer.tokenizer.Tokenizer;
import me.waliedyassen.runescript.compiler.parser.ScriptParserTest;
import me.waliedyassen.runescript.compiler.semantics.SemanticChecker;
import me.waliedyassen.runescript.compiler.symbol.ScriptSymbolTable;
import me.waliedyassen.runescript.compiler.symbol.impl.script.ScriptInfo;
import me.waliedyassen.runescript.compiler.syntax.SyntaxParser;
import me.waliedyassen.runescript.type.PrimitiveType;
import me.waliedyassen.runescript.type.StackType;
import me.waliedyassen.runescript.type.Type;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CodeGeneratorTest {

    CodeGenerator generator;
    SemanticChecker checker;
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
        var table = new ScriptSymbolTable();
        var map = new InstructionMap();
        for (var opcode : CoreOpcode.values()) {
            map.registerCore(opcode, opcode.ordinal(), opcode.isLargeOperand());
        }
        checker = new SemanticChecker(environment, table, false);
        generator = new CodeGenerator(environment, table, map, ScriptParserTest.TestTriggerType.CLIENTSCRIPT);
        generator.initialise();
        table.defineCommand(new BasicOpcode(0, false), "func_i_i", PrimitiveType.INT, new Type[]{PrimitiveType.INT}, false, null, false);
    }

    @Test
    void testLocalDefine() {
        var script = fromResource("local_01.rs2")[0];
        var block = script.getBlocks().get(new Label(0, "entry_0"));
        assertEquals(4, script.getParameters().values().stream().mapToInt(Collection::size).sum());
        assertEquals(new Local("bool_param", PrimitiveType.BOOLEAN), script.getParameters().get(StackType.INT).get(0));
        assertEquals(new Local("int_param", PrimitiveType.INT), script.getParameters().get(StackType.INT).get(1));
        assertEquals(new Local("string_param", PrimitiveType.STRING), script.getParameters().get(StackType.STRING).get(0));
        assertEquals(new Local("long_param", PrimitiveType.LONG), script.getParameters().get(StackType.LONG).get(0));
        assertEquals(11, block.getInstructions().size());
        assertInstructionEquals(block.getInstructions().get(0), CoreOpcode.PUSH_INT_CONSTANT, 1234);
        assertInstructionEquals(block.getInstructions().get(1), CoreOpcode.POP_INT_LOCAL, new Local("my_int", PrimitiveType.INT));
        assertInstructionEquals(block.getInstructions().get(2), CoreOpcode.PUSH_STRING_CONSTANT, "test");
        assertInstructionEquals(block.getInstructions().get(3), CoreOpcode.POP_STRING_LOCAL, new Local("my_string", PrimitiveType.STRING));
        assertInstructionEquals(block.getInstructions().get(4), CoreOpcode.PUSH_INT_CONSTANT, 1);
        assertInstructionEquals(block.getInstructions().get(5), CoreOpcode.POP_INT_LOCAL, new Local("my_true_bool", PrimitiveType.BOOLEAN));
        assertInstructionEquals(block.getInstructions().get(6), CoreOpcode.PUSH_LONG_CONSTANT, 1234L);
        assertInstructionEquals(block.getInstructions().get(7), CoreOpcode.POP_LONG_LOCAL, new Local("my_long", PrimitiveType.LONG));
        assertInstructionEquals(block.getInstructions().get(8), CoreOpcode.PUSH_INT_CONSTANT, 0);
        assertInstructionEquals(block.getInstructions().get(9), CoreOpcode.POP_INT_LOCAL, new Local("my_false_bool", PrimitiveType.BOOLEAN));
        assertInstructionEquals(block.getInstructions().get(10), CoreOpcode.RETURN, 0);
    }

    @Test
    void testCalcPrecedence() {
        var script = fromString("[proc,test](int $parameter)(int) return(calc(1 + $parameter * 5));")[0];
        var block = script.getBlocks().get(new Label(0, "entry_0"));
        assertInstructionEquals(block.getInstructions().get(0), CoreOpcode.PUSH_INT_CONSTANT, 1);
        assertInstructionEquals(block.getInstructions().get(1), CoreOpcode.PUSH_INT_LOCAL, new Local("parameter", PrimitiveType.INT));
        assertInstructionEquals(block.getInstructions().get(2), CoreOpcode.PUSH_INT_CONSTANT, 5);
        assertInstructionEquals(block.getInstructions().get(3), CoreOpcode.MUL, 0);
        assertInstructionEquals(block.getInstructions().get(4), CoreOpcode.ADD, 0);
        assertInstructionEquals(block.getInstructions().get(5), CoreOpcode.RETURN, 0);
    }

    @Test
    void testCalcSimple() {
        var script = fromString("[proc,test](int $parameter)(int) return(calc($parameter));")[0];
        var block = script.getBlocks().get(new Label(0, "entry_0"));
        assertInstructionEquals(block.getInstructions().get(0), CoreOpcode.PUSH_INT_LOCAL, new Local("parameter", PrimitiveType.INT));
        assertInstructionEquals(block.getInstructions().get(1), CoreOpcode.RETURN, 0);
    }

    @Test
    void testDiscard() {
        var script = fromString("[proc,test](int $parameter) func_i_i($parameter);")[0];
        var block = script.getBlocks().get(new Label(0, "entry_0"));
        assertInstructionEquals(block.getInstructions().get(0), CoreOpcode.PUSH_INT_LOCAL, new Local("parameter", PrimitiveType.INT));
        assertInstructionEquals(block.getInstructions().get(1), 0, false, 0);
        assertInstructionEquals(block.getInstructions().get(2), CoreOpcode.POP_INT_DISCARD, 0);
        assertInstructionEquals(block.getInstructions().get(3), CoreOpcode.RETURN, 0);
    }

    @Test
    void testCall01() {
        var scripts = fromString("[proc,my_proc](int $parameter) @my_label(0); [label,my_label](int $parameter) ~my_proc(0);");
        assertEquals(2, scripts.length);
        var first = scripts[0];
        var first_block = first.getBlocks().get(new Label(0, "entry_0"));
        assertInstructionEquals(first_block.getInstructions().get(0), CoreOpcode.PUSH_INT_CONSTANT, 0);
        assertInstructionEquals(first_block.getInstructions().get(1), CoreOpcode.JUMP_WITH_PARAMS, new ScriptInfo(Collections.emptyMap(), "my_label", ScriptParserTest.TestTriggerType.LABEL, PrimitiveType.VOID, new Type[]{PrimitiveType.INT}, null));
        var second = scripts[1];
        var second_block = second.getBlocks().get(new Label(0, "entry_0"));
        assertInstructionEquals(second_block.getInstructions().get(0), CoreOpcode.PUSH_INT_CONSTANT, 0);
        assertInstructionEquals(second_block.getInstructions().get(1), CoreOpcode.GOSUB_WITH_PARAMS, new ScriptInfo(Collections.emptyMap(), "my_proc", ScriptParserTest.TestTriggerType.PROC, PrimitiveType.VOID, new Type[]{PrimitiveType.INT}, null));
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
            var tokenizer = new Tokenizer(ScriptCompiler.createLexicalTable(), new BufferedCharStream(stream));
            var lexer = new Lexer(tokenizer);
            var parser = new SyntaxParser(environment, new ScriptSymbolTable(), lexer, "cs2");
            var scripts = new ArrayList<CompiledScriptUnit>();
            do {
                var unit = new CompiledScriptUnit();
                unit.setScript(parser.script());
                scripts.add(unit);
            } while (lexer.remaining() > 0);
            checker.executePre(scripts);
            checker.execute(scripts);
            var parsed = new BinaryScript[scripts.size()];
            for (var index = 0; index < parsed.length; index++) {
                parsed[index] = generator.visit(scripts.get(index).getScript());
            }
            return parsed;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    BinaryScript[] fromString(String text) {
        try (var stream = new ByteArrayInputStream(text.getBytes())) {
            var tokenizer = new Tokenizer(ScriptCompiler.createLexicalTable(), new BufferedCharStream(stream));
            var lexer = new Lexer(tokenizer);
            var parser = new SyntaxParser(environment, new ScriptSymbolTable(), lexer, "cs2");
            var scripts = new ArrayList<CompiledScriptUnit>();
            do {
                var unit = new CompiledScriptUnit();
                unit.setScript(parser.script());
                scripts.add(unit);
            } while (lexer.remaining() > 0);
            checker.executePre(scripts);
            checker.execute(scripts);
            checker.getErrors().forEach(System.out::println);
            var parsed = new BinaryScript[scripts.size()];
            for (var index = 0; index < parsed.length; index++) {
                parsed[index] = generator.visit(scripts.get(index).getScript());
            }
            return parsed;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
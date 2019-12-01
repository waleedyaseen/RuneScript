/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen;

import me.waliedyassen.runescript.commons.stream.BufferedCharStream;
import me.waliedyassen.runescript.compiler.Compiler;
import me.waliedyassen.runescript.compiler.ast.AstScript;
import me.waliedyassen.runescript.compiler.codegen.block.Label;
import me.waliedyassen.runescript.compiler.codegen.local.Local;
import me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode;
import me.waliedyassen.runescript.compiler.codegen.script.Script;
import me.waliedyassen.runescript.compiler.env.CompilerEnvironment;
import me.waliedyassen.runescript.compiler.lexer.Lexer;
import me.waliedyassen.runescript.compiler.lexer.tokenizer.Tokenizer;
import me.waliedyassen.runescript.compiler.parser.ScriptParser;
import me.waliedyassen.runescript.compiler.parser.ScriptParserTest;
import me.waliedyassen.runescript.compiler.semantics.SemanticChecker;
import me.waliedyassen.runescript.compiler.symbol.SymbolTable;
import me.waliedyassen.runescript.compiler.symbol.impl.script.ScriptInfo;
import me.waliedyassen.runescript.compiler.util.trigger.TriggerType;
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
        var table = new SymbolTable();
        var map = new InstructionMap();
        for (var opcode : CoreOpcode.values()) {
            map.registerCore(opcode, opcode.ordinal(), opcode.isLargeOperand());
        }
        checker = new SemanticChecker(environment, table);
        generator = new CodeGenerator(table, map);
        generator.initialise();
    }

    @Test
    void testLocalDefine() {
        var script = fromResource("local_01.rs2")[0];
        var block = script.getBlocks().get(new Label(0, "entry_0"));
        assertEquals(4, script.getParameters().values().stream().mapToInt(Collection::size).sum());
        assertEquals(new Local("bool_param", PrimitiveType.BOOL), script.getParameters().get(StackType.INT).get(0));
        assertEquals(new Local("int_param", PrimitiveType.INT), script.getParameters().get(StackType.INT).get(1));
        assertEquals(new Local("string_param", PrimitiveType.STRING), script.getParameters().get(StackType.STRING).get(0));
        assertEquals(new Local("long_param", PrimitiveType.LONG), script.getParameters().get(StackType.LONG).get(0));
        assertEquals(11, block.getInstructions().size());
        assertInstructionEquals(block.getInstructions().get(0), CoreOpcode.PUSH_INT_CONSTANT, 1234);
        assertInstructionEquals(block.getInstructions().get(1), CoreOpcode.POP_INT_LOCAL, new Local("my_int", PrimitiveType.INT));
        assertInstructionEquals(block.getInstructions().get(2), CoreOpcode.PUSH_STRING_CONSTANT, "test");
        assertInstructionEquals(block.getInstructions().get(3), CoreOpcode.POP_STRING_LOCAL, new Local("my_string", PrimitiveType.STRING));
        assertInstructionEquals(block.getInstructions().get(4), CoreOpcode.PUSH_INT_CONSTANT, 1);
        assertInstructionEquals(block.getInstructions().get(5), CoreOpcode.POP_INT_LOCAL, new Local("my_true_bool", PrimitiveType.BOOL));
        assertInstructionEquals(block.getInstructions().get(6), CoreOpcode.PUSH_LONG_CONSTANT, 1234L);
        assertInstructionEquals(block.getInstructions().get(7), CoreOpcode.POP_LONG_LOCAL, new Local("my_long", PrimitiveType.LONG));
        assertInstructionEquals(block.getInstructions().get(8), CoreOpcode.PUSH_INT_CONSTANT, 0);
        assertInstructionEquals(block.getInstructions().get(9), CoreOpcode.POP_INT_LOCAL, new Local("my_false_bool", PrimitiveType.BOOL));
        assertInstructionEquals(block.getInstructions().get(10), CoreOpcode.RETURN, 0);
    }

    @Test
    void testCalcPrecedence() {
        var script = fromString("[proc,test](int $param)(int) return calc(1 + $param * 5);")[0];
        var block = script.getBlocks().get(new Label(0, "entry_0"));
        assertInstructionEquals(block.getInstructions().get(0), CoreOpcode.PUSH_INT_CONSTANT, 1);
        assertInstructionEquals(block.getInstructions().get(1), CoreOpcode.PUSH_INT_LOCAL, new Local("param", PrimitiveType.INT));
        assertInstructionEquals(block.getInstructions().get(2), CoreOpcode.PUSH_INT_CONSTANT, 5);
        assertInstructionEquals(block.getInstructions().get(3), CoreOpcode.MUL, 0);
        assertInstructionEquals(block.getInstructions().get(4), CoreOpcode.ADD, 0);
        assertInstructionEquals(block.getInstructions().get(5), CoreOpcode.RETURN, 0);
    }

    @Test
    void testCalcSimple() {
        var script = fromString("[proc,test](int $param)(int) return calc($param);")[0];
        var block = script.getBlocks().get(new Label(0, "entry_0"));
        assertInstructionEquals(block.getInstructions().get(0), CoreOpcode.PUSH_INT_LOCAL, new Local("param", PrimitiveType.INT));
        assertInstructionEquals(block.getInstructions().get(1), CoreOpcode.RETURN, 0);
    }

    @Test
    void testDiscard() {
        var script = fromString("[proc,test](int $param) calc($param);")[0];
        var block = script.getBlocks().get(new Label(0, "entry_0"));
        assertInstructionEquals(block.getInstructions().get(0), CoreOpcode.PUSH_INT_LOCAL, new Local("param", PrimitiveType.INT));
        assertInstructionEquals(block.getInstructions().get(1), CoreOpcode.POP_INT_DISCARD, 0);
        assertInstructionEquals(block.getInstructions().get(2), CoreOpcode.RETURN, 0);
    }

    @Test
    void testCall01() {
        var scripts = fromString("[proc,my_proc](int $param) @my_label(0); [label,my_label](int $param) ~my_proc(0);");
        assertEquals(2, scripts.length);
        var first = scripts[0];
        var first_block = first.getBlocks().get(new Label(0, "entry_0"));
        assertInstructionEquals(first_block.getInstructions().get(0), CoreOpcode.PUSH_INT_CONSTANT, 0);
        assertInstructionEquals(first_block.getInstructions().get(1), CoreOpcode.JUMP_WITH_PARAMS, new ScriptInfo(Collections.emptyMap(), "my_label", ScriptParserTest.TestTriggerType.LABEL, PrimitiveType.VOID, new Type[]{PrimitiveType.INT}));
        var second = scripts[1];
        var second_block = second.getBlocks().get(new Label(0, "entry_0"));
        assertInstructionEquals(second_block.getInstructions().get(0), CoreOpcode.PUSH_INT_CONSTANT, 0);
        assertInstructionEquals(second_block.getInstructions().get(1), CoreOpcode.GOSUB_WITH_PARAMS, new ScriptInfo(Collections.emptyMap(), "my_proc", ScriptParserTest.TestTriggerType.PROC, PrimitiveType.VOID, new Type[]{PrimitiveType.INT}));
    }

    void assertInstructionEquals(Instruction instruction, CoreOpcode opcode, Object operand) {
        var mapped = (InstructionMap.MappedOpcode) instruction.getOpcode();
        assertEquals(opcode, mapped.getOpcode());
        assertEquals(opcode.isLargeOperand(), mapped.isLarge());
        assertEquals(operand, instruction.getOperand());
    }

    Script[] fromResource(String name) {
        try (var stream = getClass().getResourceAsStream(name)) {
            var tokenizer = new Tokenizer(Compiler.createLexicalTable(), new BufferedCharStream(stream));
            var lexer = new Lexer(tokenizer);
            var parser = new ScriptParser(environment, lexer);
            var scripts = new ArrayList<AstScript>();
            do {
                scripts.add(parser.script());
            } while (lexer.remaining() > 0);
            checker.executePre(scripts);
            checker.execute(scripts);
            var parsed = new Script[scripts.size()];
            for (var index = 0; index < parsed.length; index++) {
                parsed[index] = generator.visit(scripts.get(index));
            }
            return parsed;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    Script[] fromString(String text) {
        try (var stream = new ByteArrayInputStream(text.getBytes())) {
            var tokenizer = new Tokenizer(Compiler.createLexicalTable(), new BufferedCharStream(stream));
            var lexer = new Lexer(tokenizer);
            var parser = new ScriptParser(environment, lexer);
            var scripts = new ArrayList<AstScript>();
            do {
                scripts.add(parser.script());
            } while (lexer.remaining() > 0);
            checker.executePre(scripts);
            checker.execute(scripts);
            checker.getErrors().forEach(System.out::println);
            var parsed = new Script[scripts.size()];
            for (var index = 0; index < parsed.length; index++) {
                parsed[index] = generator.visit(scripts.get(index));
            }
            return parsed;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
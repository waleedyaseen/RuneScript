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
import me.waliedyassen.runescript.compiler.codegen.block.Label;
import me.waliedyassen.runescript.compiler.codegen.local.Local;
import me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode;
import me.waliedyassen.runescript.compiler.codegen.script.Script;
import me.waliedyassen.runescript.compiler.lexer.Lexer;
import me.waliedyassen.runescript.compiler.lexer.tokenizer.Tokenizer;
import me.waliedyassen.runescript.compiler.parser.ScriptParser;
import me.waliedyassen.runescript.compiler.semantics.SemanticChecker;
import me.waliedyassen.runescript.compiler.stack.StackType;
import me.waliedyassen.runescript.compiler.symbol.SymbolTable;
import me.waliedyassen.runescript.compiler.type.primitive.PrimitiveType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CodeGeneratorTest {

    private CodeGenerator generator;
    private SemanticChecker checker;

    @BeforeEach
    void setupGenerator() {
        var table = new SymbolTable();
        var map = new InstructionMap();
        for (var opcode : CoreOpcode.values()) {
            map.registerCore(opcode, opcode.ordinal(), opcode.isLargeOperand());
        }
        checker = new SemanticChecker(table);
        generator = new CodeGenerator(table, map);
        generator.initialise();
    }

    @Test
    void testLocalDefine() {
        var script = fromResource("local_define.rs2");
        var block = script.getBlocks().get(new Label(0, "entry_0"));
        assertEquals(4, script.getParameters().values().stream().mapToInt(Collection::size).sum());
        assertEquals(new Local("bool_param", PrimitiveType.BOOL), script.getParameters().get(StackType.INT).get(0));
        assertEquals(new Local("int_param", PrimitiveType.INT), script.getParameters().get(StackType.INT).get(1));
        assertEquals(new Local("string_param", PrimitiveType.STRING), script.getParameters().get(StackType.STRING).get(0));
        assertEquals(new Local("long_param", PrimitiveType.LONG), script.getParameters().get(StackType.LONG).get(0));
        assertEquals(9, block.getInstructions().size());
        assertInstructionEquals(block.getInstructions().get(0), CoreOpcode.PUSH_INT_CONSTANT, 1234);
        assertInstructionEquals(block.getInstructions().get(1), CoreOpcode.POP_INT_LOCAL, new Local("my_int", PrimitiveType.INT));
        assertInstructionEquals(block.getInstructions().get(2), CoreOpcode.PUSH_STRING_CONSTANT, "test");
        assertInstructionEquals(block.getInstructions().get(3), CoreOpcode.POP_STRING_LOCAL, new Local("my_string", PrimitiveType.STRING));
        assertInstructionEquals(block.getInstructions().get(4), CoreOpcode.PUSH_INT_CONSTANT, 1);
        assertInstructionEquals(block.getInstructions().get(5), CoreOpcode.POP_INT_LOCAL, new Local("my_bool", PrimitiveType.BOOL));
        assertInstructionEquals(block.getInstructions().get(6), CoreOpcode.PUSH_LONG_CONSTANT, 1234L);
        assertInstructionEquals(block.getInstructions().get(7), CoreOpcode.POP_LONG_LOCAL, new Local("my_long", PrimitiveType.LONG));
        assertInstructionEquals(block.getInstructions().get(8), CoreOpcode.RETURN, 0);
    }

    void assertInstructionEquals(Instruction instruction, CoreOpcode opcode, Object operand) {
        var mapped = (InstructionMap.MappedOpcode) instruction.getOpcode();
        assertEquals(opcode, mapped.getOpcode());
        assertEquals(opcode.isLargeOperand(), mapped.isLarge());
        assertEquals(operand, instruction.getOperand());
    }

    Script fromResource(String name) {
        try (var stream = getClass().getResourceAsStream(name)) {
            var tokenizer = new Tokenizer(Compiler.createLexicalTable(), new BufferedCharStream(stream));
            var lexer = new Lexer(tokenizer);
            var parser = new ScriptParser(lexer);
            var script = parser.script();
            checker.executePre(Collections.singletonList(script));
            checker.executePre(Collections.singletonList(script));
            return generator.visit(script);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
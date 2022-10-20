/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen.optimizer.impl;

import me.waliedyassen.runescript.commons.stream.BufferedCharStream;
import me.waliedyassen.runescript.compiler.CompiledScriptUnit;
import me.waliedyassen.runescript.compiler.ScriptCompiler;
import me.waliedyassen.runescript.compiler.codegen.CodeGenerator;
import me.waliedyassen.runescript.compiler.codegen.InstructionMap;
import me.waliedyassen.runescript.compiler.codegen.opcode.CoreOpcode;
import me.waliedyassen.runescript.compiler.codegen.optimizer.Optimizer;
import me.waliedyassen.runescript.compiler.codegen.script.BinaryScript;
import me.waliedyassen.runescript.compiler.env.CompilerEnvironment;
import me.waliedyassen.runescript.compiler.error.ThrowingErrorReporter;
import me.waliedyassen.runescript.compiler.lexer.Lexer;
import me.waliedyassen.runescript.compiler.lexer.tokenizer.Tokenizer;
import me.waliedyassen.runescript.compiler.parser.ScriptParserTest;
import me.waliedyassen.runescript.compiler.semantics.SemanticChecker;
import me.waliedyassen.runescript.compiler.symbol.ScriptSymbolTable;
import me.waliedyassen.runescript.compiler.syntax.SyntaxParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConstantFoldingOptimizationTest {


    private static final String TEMPLATE = "[proc,temp]return(calc(%s));";

    static CompilerEnvironment environment;
    static InstructionMap instructionMap;
    static Optimizer optimizer;
    static SemanticChecker checker;
    static CodeGenerator generator;

    @BeforeAll
    static void setupAll() {
        environment = new CompilerEnvironment();
        for (ScriptParserTest.TestTriggerType triggerType : ScriptParserTest.TestTriggerType.values()) {
            environment.registerTrigger(triggerType);
        }
        instructionMap = new InstructionMap();
        for (var opcode : CoreOpcode.values()) {
            instructionMap.registerCore(opcode, opcode.ordinal(), opcode.isLargeOperand());
        }
        optimizer = new Optimizer(instructionMap);
        optimizer.register(new ConstantFoldingOptimization());
        var table = new ScriptSymbolTable(true);
        checker = new SemanticChecker(null, environment, table, false);
        generator = new CodeGenerator(environment, table, instructionMap, ScriptParserTest.TestTriggerType.CLIENTSCRIPT);
    }

    @BeforeEach
    void setupEach() {
        generator.initialise();
    }

    @Test
    void testFolding() {
        var script = fromString(" 11 * 0 + 15 * 1 * 19 + 9 % 6 * 2 % 16 - 4");
        optimize(script);
        assertEquals(287, script.getBlockList().getBlocks().get(0).getInstructions().get(0).getOperand());
    }

    private void optimize(BinaryScript script) {
        optimizer.run(script);
    }

    BinaryScript fromString(String expression) {
        var text = String.format(TEMPLATE, expression);
        var tokenizer = new Tokenizer(new ThrowingErrorReporter(), ScriptCompiler.createLexicalTable(), new BufferedCharStream(text.toCharArray()));
        var lexer = new Lexer(tokenizer);
        var parser = new SyntaxParser(environment, new ScriptSymbolTable(true), new ThrowingErrorReporter(), lexer, "cs2");
        var scripts = new ArrayList<CompiledScriptUnit>();
        do {
            var unit = new CompiledScriptUnit();
            unit.setSyntax(parser.script());
            scripts.add(unit);
        } while (lexer.remaining() > 0);
        if (scripts.size() != 1) {
            throw new IllegalStateException();
        }
        checker.executePre(scripts);
        checker.execute(scripts);
        return generator.visit(scripts.get(0).getSyntax());
    }
}
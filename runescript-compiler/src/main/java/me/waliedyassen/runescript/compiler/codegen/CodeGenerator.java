/*
 * Copyright (c) 2019 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler.codegen;

import me.waliedyassen.runescript.compiler.ast.AstScript;
import me.waliedyassen.runescript.compiler.ast.stmt.AstBlockStatement;
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;
import me.waliedyassen.runescript.compiler.codegen.asm.Block;
import me.waliedyassen.runescript.compiler.codegen.asm.Label;
import me.waliedyassen.runescript.compiler.codegen.asm.Script;

/**
 * Represents the compiler bytecode generator.
 *
 * @author Walied K. Yassen
 */
public final class CodeGenerator implements AstVisitor {

    /**
     * The blocks map of the current script.
     */
    private final BlockMap blockMap = new BlockMap();

    /**
     * The label generator used to generate any label for this code generator.
     */
    private final LabelGenerator labelGenerator = new LabelGenerator();

    /**
     * Initialises the code generator and reset its state.
     */
    public void initialise() {
        blockMap.reset();
        labelGenerator.reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Script visit(AstScript script) {
        var generated = new Script("[" + script.getTrigger().getText() + "," + script.getName().getText() + "]");
        script.getCode().accept(this);
        return generated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Block visit(AstBlockStatement blockStatement) {
        var block = generateBlock();
        for (var statement : blockStatement.getStatements()) {
            statement.accept(this);
        }
        return block;
    }

    /**
     * Generates a new {@link Block} object.
     *
     * @return the generated {@link Block} object.
     * @see BlockMap#generate(Label)
     */
    private Block generateBlock() {
        return blockMap.generate(generateLabel());
    }

    /**
     * Generates a new unique {@link Label} object.
     *
     * @return the generated {@link Label} object.
     * @see LabelGenerator#generate()
     */
    private Label generateLabel() {
        return labelGenerator.generate();
    }
}

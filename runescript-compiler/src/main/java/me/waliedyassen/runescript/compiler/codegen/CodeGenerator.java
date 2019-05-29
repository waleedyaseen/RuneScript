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
import me.waliedyassen.runescript.compiler.codegen.asm.Script;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the compiler bytecode generator.
 *
 * @author Walied K. Yassen
 */
public final class CodeGenerator implements AstVisitor {

    /**
     * A list of the generated blocks for the current script.
     */
    private final List<Block> blocks = new ArrayList<>();

    /**
     * Initialises the code generator and reset it's state.
     */
    public void initialise() {
        blocks.clear();
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
        var block = new Block();
        blocks.add(block);
        return block;
    }
}

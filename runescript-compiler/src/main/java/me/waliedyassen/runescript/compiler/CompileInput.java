/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;
import me.waliedyassen.runescript.compiler.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all of the data that we need to perform a compile call.
 *
 * @author Walied K. Yassen
 */
public final class CompileInput {

    /**
     * A list of all the source code that want to compile as {@code byte} arrays.
     */
    @Getter
    private final List<Pair<Object, byte[]>> sourceData = new ArrayList<>();

    /**
     * A list of all the visitors to accept after building the AST tree.
     */
    @Getter
    private final List<AstVisitor<?, ?>> visitors = new ArrayList<>();

    /**
     * A list of all the compiler feedbacks to call through the process.
     */
    @Getter
    private final List<CompilerFeedback> feedbacks = new ArrayList<>();

    /**
     * Adds a new source code to compile to the input object.
     *
     * @param key    the key of the source code.
     * @param source the data of the source code.
     */
    public void addSourceCode(Object key, byte[] source) {
        sourceData.add(Pair.of(key, source));
    }

    /**
     * Adds the specified {@link AstVisitor} object to the list of the visitors we are going to accept
     * after finish building the AST tree of all the specified scripts.
     *
     * @param visitor the visitor to visit after building the tree.
     */
    public void addVisitor(AstVisitor<?, ?> visitor) {
        visitors.add(visitor);
    }

    /**
     * Adds the specified {@link CompilerFeedback} to the feedbacks list.
     *
     * @param feedback the feedback object to add to the list.
     */
    public void addFeedback(CompilerFeedback feedback) {
        feedbacks.add(feedback);
    }

    /**
     * Create a new {@link CompileInput} type object and add the specified {@code source} data as source code data
     * in the object.
     *
     * @param key    the attachment key of the source code object.
     * @param source the source code data to add to the newly created {@link CompileInput} object.
     * @return the created {@link CompileInput} object.
     */
    public static CompileInput of(Object key, byte[] source) {
        var input = new CompileInput();
        input.addSourceCode(key, source);
        return input;
    }
}

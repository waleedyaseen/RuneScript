/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler;

import lombok.Getter;
import lombok.var;
import me.waliedyassen.runescript.compiler.ast.visitor.AstVisitor;
import me.waliedyassen.runescript.compiler.message.CompilerMessage;
import me.waliedyassen.runescript.compiler.message.CompilerMessenger;
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
     * The messenger that we are going to use for posting messages.
     */
    @Getter
    private CompilerMessenger messenger;

    /**
     * Whether or not we should run the code generation phase on the input.
     */
    @Getter
    private boolean runCodeGen;

    /**
     * Adds a new source code to compile to the input object.
     *
     * @param key    the key of the source code.
     * @param source the data of the source code.
     * @return this {@link CompileInput} object instance.
     */
    public CompileInput withSourceCode(Object key, byte[] source) {
        sourceData.add(Pair.of(key, source));
        return this;
    }

    /**
     * Adds the specified {@link AstVisitor} object to the list of the visitors we are going to accept
     * after finish building the AST tree of all the specified scripts.
     *
     * @param visitor the visitor to visit after building the tree.
     * @return this {@link CompileInput} object instance.
     */
    public CompileInput withAstVisitor(AstVisitor<?, ?> visitor) {
        visitors.add(visitor);
        return this;
    }

    /**
     * Sets the messenger we are going to use for posting messages.
     *
     * @param messenger the messenger we are going to use for posting messages.
     * @return this {@link CompileInput} object instance.
     */
    public CompileInput withMessenger(CompilerMessenger messenger) {
        this.messenger = messenger;
        return this;
    }

    /**
     * Sets whether or or not we should run the code generation phase on the input.
     *
     * @param runCodeGen whether or not to run the code generation phase on the input.
     * @return this {@link CompileInput} object instance.
     */
    public CompileInput withRunCodeGen(boolean runCodeGen) {
        this.runCodeGen = runCodeGen;
        return this;
    }

    /**
     * Posts the specified {@link CompilerMessage message} to the {@link CompilerMessenger messenger}. If the
     * messenger if not present, nothing will happen.
     *
     * @param message the message that we want to post.
     */
    public void postMessage(CompilerMessage message) {
        if (messenger == null) {
            return;
        }
        messenger.handleCompilerMessage(message);
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
        input.withSourceCode(key, source);
        return input;
    }
}

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * The input object of a compiler
 *
 * @author Walied K. Yassen
 */
public final class Input {

    /**
     * The source files that we want to compile.
     */
    @Getter
    private final List<SourceFile> sourceFiles = new ArrayList<>();

    /**
     * Whether or not we want to run the code generation.
     */
    @Getter
    @Setter
    private boolean runCodeGeneration;

    /**
     * Adds the specified {@link SourceFile} to the list of files we want to compile.
     *
     * @param sourceFile
     *         the source file that we want to compile.
     */
    public void addSourceFile(SourceFile sourceFile) {
        sourceFiles.add(sourceFile);
    }
}

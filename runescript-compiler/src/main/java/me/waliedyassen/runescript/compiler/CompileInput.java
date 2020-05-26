/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.compiler;

import lombok.Getter;

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
    private final List<byte[]> sourceData = new ArrayList<>();

    /**
     * Adds a new source code to compile to the input object.
     *
     * @param source the data of the source code.
     */
    public void addSourceCode(byte[] source) {
        sourceData.add(source);
    }

    /**
     * Create a new {@link CompileInput} type object and add the specified {@code source} data as source code data
     * in the object.
     *
     * @param source the source code data to add to the newly created {@link CompileInput} object.
     * @return the created {@link CompileInput} object.
     */
    public static CompileInput of(byte[] source) {
        var input = new CompileInput();
        input.addSourceCode(source);
        return input;
    }
}

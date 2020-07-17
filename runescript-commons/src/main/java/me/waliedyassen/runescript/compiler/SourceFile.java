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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A source file that a compiler can take in for compiling.
 *
 * @author Walied K. Yassen
 */
@Getter
@RequiredArgsConstructor
public final class SourceFile {

    /**
     * The location of the source file.
     */
    private final String location;

    /**
     * The name of the source file.
     */
    private final String name;

    /**
     * The extension of the source file.
     */
    private final String extension;

    /**
     * The content of the source file.
     */
    private final byte[] content;

    /**
     * Returns the source file full name (the name with it's location and extension).
     *
     * @return the source file full name with it's location.
     */
    public String getFullNameWithLocation() {
        return location + "/" + name + "." + extension;
    }

    /**
     * Creates a {@link SourceFile} object for the specified {@link Path}.
     *
     * @param path
     *         the path which leads to the source file.
     *
     * @return the {@link SourceFile} object.
     *
     * @throws IOException
     *         if anything occurs while reading the file data.
     */
    public static SourceFile of(Path path) throws IOException {
        path = path.toAbsolutePath();
        byte[] content = Files.readAllBytes(path);
        String nameWithExtension = path.getFileName().toString();
        String fullPath = path.getParent().toString();
        String extension = nameWithExtension.substring(nameWithExtension.lastIndexOf('.') + 1);
        String name = nameWithExtension.substring(0, nameWithExtension.lastIndexOf('.'));
        return new SourceFile(fullPath, name, extension, content);
    }
}

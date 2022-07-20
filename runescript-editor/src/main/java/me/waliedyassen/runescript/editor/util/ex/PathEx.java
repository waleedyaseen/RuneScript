/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.util.ex;


import java.nio.file.Path;

/**
 * A static utility class which contains extension methods for the {@link Path} objects.
 *
 * @author Walied K. Yassen
 */
public final class PathEx {

    /**
     * Extracts the extension from the specified file {@code path}.
     *
     * @param path the path of the file that we want to extract the extension from.
     * @return the extracted extension or an empty string if no extension is found.
     * @see #getExtension(String)
     */
    public static String getExtension(Path path) {
        return getExtension(path.getFileName().toString());
    }

    /**
     * Extracts the extension from the specified file {@code name}.
     *
     * @param name the name of the file that we want to extract the extension from.
     * @return the extracted extension or an empty string if no extension is found.
     */
    public static String getExtension(String name) {
        var lastDot = name.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return name.substring(lastDot + 1).toLowerCase();
    }

    /**
     * Normalizes the specified {@link Path path} to a key string.
     *
     * @param root the root path, which when present the other {@code path} will be relativized against.
     * @param path the path which we want to normalize to a key string.
     * @return the normalized string form of the path.
     */
    public static String normalizeRelative(Path root, Path path) {
        var relative = root != null ? root.relativize(path.toAbsolutePath()) : path;
        var builder = new StringBuilder();
        for (var index = 0; index < relative.getNameCount(); index++) {
            if (index != 0) {
                builder.append('/');
            }
            builder.append(relative.getName(index).toString());
        }
        return builder.toString();
    }

    private PathEx() {
        // NOOP
    }
}

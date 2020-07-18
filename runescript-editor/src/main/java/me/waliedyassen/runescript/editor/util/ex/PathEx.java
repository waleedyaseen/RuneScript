/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.util.ex;

import lombok.var;

import java.nio.file.Path;

/**
 * A static utility class which contains extension methods for the {@link Path} objects.
 *
 * @author Walied K. Yassen
 */
public final class PathEx {

    /**
     * Returns the extension of the specified {@link Path}.
     *
     * @param path
     *         the path to get it's extension.
     *
     * @return the extension of the path or an empty string if there was no extension.
     */
    public static String getExtension(Path path) {
        var name = path.getFileName().toString();
        var lastDot = name.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return name.substring(lastDot + 1).toLowerCase();
    }

    /**
     * Normalizes the specified {@link Path path} to a key string.
     *
     * @param root
     *         the root path, which when present the other {@code path} will be relativized against.
     * @param path
     *         the path which we want to normalize to a key string.
     *
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

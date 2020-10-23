/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.project.compile;

import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for managing and providing {@link ProjectCompiler} objects from file name extensions.
 *
 * @author Walied K. Yassen
 */
public final class ProjectCompilerProvider {

    /**
     * ِA map of all the registered compilers in this provider.
     */
    private final Map<String, ProjectCompiler<?, ?>> compilers = new HashMap<>();

    /**
     * Registers the {@link ProjectCompiler} for the specified {@code extension}.
     *
     * @param extension the file name extension to register the compiler for.
     * @param compiler  the compiler that we want to register.
     */
    public void register(String extension, ProjectCompiler<?, ?> compiler) {
        compilers.put(extension, compiler);
    }

    /**
     * Returns the {@link ProjectCompiler} that is registered for the specified {@code extension}.
     *
     * @param extension the file name extension that we want to retrieve it's associated compiler.
     * @return the {@link ProjectCompiler} object if found otherwise {@code null}.
     */
    public ProjectCompiler<?, ?> get(String extension) {
        return compilers.get(extension);
    }
}

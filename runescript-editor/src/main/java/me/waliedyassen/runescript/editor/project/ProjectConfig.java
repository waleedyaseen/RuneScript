/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.project;

import com.electronwill.nightconfig.core.CommentedConfig;
import me.waliedyassen.runescript.type.PrimitiveType;
import me.waliedyassen.runescript.type.Type;

import java.util.List;

/**
 * A static-class that is responsible for loading and saving the project configuration.
 *
 * @author Walied K. Yassen
 */
public final class ProjectConfig {

    /**
     * Attempts to parse an array of {@link Type} from the specified {@link CommentedConfig} object.
     *
     * @param config the configuration object to attempt to parse from.
     * @param name   the name of the configuration to parse.
     * @return the parsed array {@link Type} object.
     */
    public static Type[] parseTypes(CommentedConfig config, String name) {
        var types = config.<List<String>>get(name);
        var mapped = new Type[types.size()];
        for (var index = 0; index < types.size(); index++) {
            mapped[index] = PrimitiveType.valueOf(types.get(index));
        }
        return mapped;
    }

    private ProjectConfig() {
        // NOOP
    }
}

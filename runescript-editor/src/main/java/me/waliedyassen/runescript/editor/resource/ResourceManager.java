/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.resource;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.var;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * The resource manager of the editor, it is responsible for loading the built-in resources of the editor.
 *
 * @author Walied K. Yassen
 */
public final class ResourceManager {

    /**
     * The singleton instance of the {@link ResourceManager} type.
     */
    @Getter
    private static final ResourceManager instance = new ResourceManager();

    /**
     * The resources that are currently cached.
     */
    // Most likely it will only be accessed by the AWT thread, so no need to make it
    // concurrent at this moment.
    private final Map<String, Object> cachedResources = new HashMap<>();

    private ResourceManager() {
        // NOOP
    }

    /**
     * Loads an {@link ImageIcon} from the specified resources file {@code path} .
     *
     * @param path
     *         the path of the icon.
     *
     * @return the loaded {@link ImageIcon} object.
     */
    public ImageIcon loadIcon(String... path) {
        var fullPath = /*normalise(*/String.join("/", path)/*)*/;
        var cached = (ImageIcon) cachedResources.get(fullPath);
        if (cached == null) {
            var url = getClass().getResource(fullPath);
            if (url == null) {
                return null;
            }
            cached = new ImageIcon(url);
            cachedResources.put(fullPath, cached);
        }
        return cached;
    }

    /**
     * Loads an {@link Image} from the specified resources file {@code path}.
     *
     * @param path
     *         the path of the icon.
     *
     * @return the loaded {@link Image} object.
     */
    @SneakyThrows
    public Image loadImage(String... path) {
        var fullPath = /*normalise(*/String.join("/", path)/*)*/;
        var cached = (Image) cachedResources.get(fullPath);
        if (cached == null) {
            var url = getClass().getResource(fullPath);
            if (url == null) {
                return null;
            }
            cached = ImageIO.read(url);
            cachedResources.put(fullPath, cached);
        }
        return cached;
    }
}

/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.util;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility static-class that is responsible for loading and caching the fonts.
 *
 * @author Walied K. Yassen
 */
@Slf4j
public final class FontManager {

    /**
     * The fonts that are currently cached in the memory.
     */
    private static final Map<String, Font> cachedFonts = new HashMap<>();

    /**
     * Attempts to fetch the font with the specified {@code name} from the cache, if the font is currently not present,
     * it will attempt to load it from the local disk and cache it for later use, if there is a problem with loading
     * the font, a {@code null} will be returned instead.
     *
     * @param name the name of the font that we want to load.
     * @return the loaded {@link Font} object or {@code null} if there was a problem with loading the font.
     */
    public static Font getFont(String name) {
        var font = cachedFonts.get(name);
        if (font != null) {
            return font;
        }
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, FontManager.class.getResourceAsStream(name + ".ttf"));
            cachedFonts.put(name, font);
        } catch (Throwable e) {
            log.error("Error while loading font: {}", name, e);
            return null;
        }
        return font;
    }

    private FontManager() {
        // NOOP
    }
}

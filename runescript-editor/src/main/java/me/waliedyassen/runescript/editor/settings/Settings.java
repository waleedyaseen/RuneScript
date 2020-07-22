/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.settings;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import me.waliedyassen.runescript.editor.RuneScriptEditor;
import me.waliedyassen.runescript.editor.util.JsonUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

/**
 * The settings container type for the RuneScript Editor.
 *
 * @author Walied K. Yassen
 */
@Slf4j
@RequiredArgsConstructor
public final class Settings {

    /**
     * A map which acts like a cache for all of the last paths in directory and file chooser(s).
     */
    @Getter
    private final Map<String, Path> pathsCache = new HashMap<>();

    /**
     * The path of this settings file.
     */
    @Getter
    private final Path path;

    /**
     * Attempts to save the settings data to the local disk.
     */
    public void save() {
        // Create the root node of the settings.
        var root = JsonUtil.getMapper().createObjectNode();
        // Serialise the pathsCache field.
        if (!pathsCache.isEmpty()) {
            var object = root.putObject("paths-cache");
            pathsCache.forEach((key, value) -> {
                object.put(key, value.toString());
            });
        }
        // WRite the settings to the local disk.
        try (var writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValue(writer, root);
        } catch (Throwable e) {
            log.error("Failed to save the settings file to the local disk: {}", path, e);
        }
    }

    /**
     * Attempts to load the settings data from the local disk.
     */
    public void load() {
        // The root node of the settings.
        JsonNode root;
        // Read the root node of the settings.
        try (var reader = Files.newBufferedReader(path)) {
            root = JsonUtil.getMapper().readTree(reader);
        } catch (Throwable e) {
            log.error("Failed to read the settings file from the local disk: {}", path, e);
            return;
        }
        // Load the paths cache if it is present.
        if (root.has("paths-cache")) {
            var object = root.get("paths-cache");
            object.fields().forEachRemaining(entry -> pathsCache.put(entry.getKey(), Paths.get(entry.getValue().asText())));
        }
    }

    /**
     * Gets the cached {@link Path} of the specified cache {@code key}.
     *
     * @param key
     *         the key of the cached path.
     *
     * @return the {@link Path} if found otherwise {@link RuneScriptEditor#getUserDirectory()}.
     */
    public Path getCachedPath(String key) {
        return pathsCache.getOrDefault(key, RuneScriptEditor.getUserDirectory());
    }

    /**
     * Sets the cached {@link Path} for the specified cache {@code key}.
     *
     * @param key
     *         the key of the cached path.
     * @param path
     *         the path to set for the cache.
     */
    public void setCachedPath(String key, Path path) {
        pathsCache.put(key, path);
        save();
    }
}

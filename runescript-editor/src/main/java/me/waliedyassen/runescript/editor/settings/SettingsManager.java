/*
 * Copyright (c) 2020 Walied K. Yassen, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.waliedyassen.runescript.editor.settings;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import me.waliedyassen.runescript.editor.RuneScriptEditor;
import me.waliedyassen.runescript.editor.settings.impl.LookAndFeelSettings;
import me.waliedyassen.runescript.editor.settings.state.SettingsState;
import me.waliedyassen.runescript.editor.util.JsonUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Responsible for providing all of the settings to the rest of the editor.
 *
 * @author Walied K. Yassen
 */
@Slf4j
@SuppressWarnings("rawtypes")
public final class SettingsManager {

    /**
     * The path which leads to the configurations file.
     */
    private static Path configurationsPath;

    /**
     * The "Look & Feel" settings object.
     */
    private static final Map<String, Settings> map = new HashMap<>();

    /**
     * Initializes the settings manager.
     */
    public static void initialize() {
        configurationsPath = RuneScriptEditor.getUserDirectory().resolve("configurations.json");
        add(new LookAndFeelSettings());
        if (Files.exists(configurationsPath)) {
            load();
        } else {
            save();
        }
    }

    /**
     * Loads all of the settings data from the user configurations file.
     */
    @SuppressWarnings("unchecked")
    public static void load() {
        final var mapper = JsonUtil.getMapper();
        JsonNode root;
        try (var reader = Files.newBufferedReader(configurationsPath)) {
            root = mapper.readTree(reader);
        } catch (IOException e) {
            log.error("Failed to load the settings from the user directory", e);
            return;
        }
        if (!root.isObject()) {
            throw new IllegalStateException("Expected a JSON object as the root element of the user configurations file");
        }
        var object = (ObjectNode) root;
        for (var settings : collection()) {
            var node = object.get(settings.getName());
            if (node == null) {
                continue;
            }
            try {
                settings.setPersistentState(settings.loadState(mapper, node));
            } catch (IOException e) {
                log.error("Failed to read the user configurations for settings with key: {}", settings.getName(), e);
            }
        }
    }

    /**
     * Saves all of the settings data to the user configurations file.
     */
    @SuppressWarnings("unchecked")
    public static void save() {
        final var mapper = JsonUtil.getMapper();
        final var root = mapper.createObjectNode();
        for (var settings : collection()) {
            root.set(settings.getName(), settings.storeState(mapper, settings.getPersistentState()));
        }
        try (var writer = Files.newBufferedWriter(configurationsPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(root.toPrettyString());
            writer.flush();
        } catch (IOException e) {
            log.error("Failed to write the settings to the user directory", e);
        }
    }

    /**
     * Adds the specified {@link Settings} object of type {@code T} to the cached map of settings.
     *
     * @param settings the settings object that  we want to add.
     * @param <S>      the type of the settings state.
     * @param <T>      the type of the settings object.
     * @throws IllegalStateException if another settings with the same name as the specified {@code settings}
     *                               is already added.
     */
    public static <S extends SettingsState, T extends Settings<S>> void add(T settings) {
        if (map.containsKey(settings.getName())) {
            throw new IllegalStateException();
        }
        map.put(settings.getName(), settings);
    }

    /**
     * Returns the {@link Settings} object with the specified {@code name} from the cached settings map.
     * If no setting object is found, a {@code null} will be returned.
     *
     * @param name the name of the
     * @param <S>  the type of the settings state.
     * @param <T>  the type of the settings object.
     * @return the {@link Settings} object of type {@link T} or {@code null}.
     */
    @SuppressWarnings("unchecked")
    public static <S extends SettingsState, T extends Settings<S>> T get(String name) {
        return (T) map.get(name);
    }

    /**
     * Returns the look and feel settings object from the cached map of settings.
     *
     * @return the {@link LookAndFeelSettings} object.
     */
    public static LookAndFeelSettings getLookAndFeelSettings() {
        return get(LookAndFeelSettings.NAME);
    }

    /**
     * Returns a collection of all the settings.
     *
     * @return a collection of all the settings.
     */
    @SuppressWarnings("rawtypes")
    public static Collection<Settings> collection() {
        return Collections.unmodifiableCollection(map.values());
    }

    /**
     * Returns a stream of all the settings.
     *
     * @return a stream of all the settings.
     */
    @SuppressWarnings("rawtypes")
    public static Stream<Settings> stream() {
        return collection().stream();
    }
}
